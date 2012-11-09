package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

public class Dialogue {
	/** ドメイン情報を保持する OntologyBase */
	OntologyBase    ontologyBase;
	/** ユーザアクションセット */
	UserActionSet   userActionSet;
	/** システムアクションセット */
	SystemActionSet systemActionSet;
	
	/** システムアクションをランダムに選択する */ 
	static final char SYSTEM_ACTION_ALWAYS_RANDOM = 'r';
	/** システムアクションを学習した政策Paiから選択する */ 
	static final char SYSTEM_ACTION_ALWAYS_PAI    = 'p';
	/** システムアクションを e-greedy 法で選択する */ 
	static final char SYSTEM_ACTION_E_GREEDY      = 'e';
	
	static final double THREASHHOLD = 1; // 小さいほどGPが増えて，大きいほどGPが減る
	static final double GAMMA = 0.95;
	static final double EPSILON = 0.2;
	
	Dialogue(String oFile){
		ontologyBase    = new OntologyBase();
		ontologyBase.loadOntology(oFile);
		userActionSet   = new UserActionSet(ontologyBase);
		systemActionSet = new SystemActionSet(ontologyBase);
	}
	
	public static void main (String[] argv){
		Dialogue testDialogue = new Dialogue("jp/ac/shizuoka/inf/cs/kogure/HIS/data/ontology-info6.xml");
//		testDialogue.test01();
//		testDialogue.Learning(SYSTEM_ACTION_ALWAYS_RANDOM, new GridPointSet());
	
		GridPointSet gps = new GridPointSet();

//		RecognitionRate rr = new RecognitionRate(new double[]{0.7});
//		RecognitionRate rr = new RecognitionRate(new double[]{0.9, 0.05});
//		RecognitionRate rr = new RecognitionRate(new double[]{0.9, 0.05, 0.03});
		RecognitionRate rr = new RecognitionRate(new double[]{0.5, 0.3, 0.1, 0.07, 0.03});

//		RecognitionRate rr = new RecognitionRate(new double[]{0.5}); // 認識率90%で1-best
//		RecognitionRate rr = new RecognitionRate(new double[]{0.8,0.1}); // 1位認識率80% (n-best認識率90%))

		// Learning
		testDialogue.Learning(SYSTEM_ACTION_E_GREEDY, gps, rr);
		
		// Using Policy
//		testDialogue.Learning(SYSTEM_ACTION_ALWAYS_PAI, "policy3best(90-0.05-0.03)/10000.policy", gps, rr);
//		testDialogue.Learning(SYSTEM_ACTION_ALWAYS_PAI, "policy1best(70)64/10000.policy", gps, rr);

		
		// Random
//		testDialogue.Learning(SYSTEM_ACTION_ALWAYS_RANDOM, gps, rr);

	}
	
	public void setGridPointSet(String policyFileName, GridPointSet gps){
		try {
			File policyFile = new File(policyFileName);
			if (policyFile.exists() && policyFile.isFile() && policyFile.canRead()){
				BufferedReader br = new BufferedReader(new FileReader(policyFile));
				String str;
				while((str = br.readLine()) != null){
					String[] input = str.split(",");
					BeliefPoint bTemp = 
						new BeliefPoint(Double.parseDouble(input[0]),
										Double.parseDouble(input[1]),
										HypothesisStatusEnum.getTest(input[2]),
										PartitionStatusEnum.getTest(input[3]),
										ActionEnum.getTest(input[4]));
					GridPoint gTemp = new GridPoint(bTemp);
					gTemp.optimizedAction = ActionEnum.getTest(input[5]);
					gps.gridPointArrayList.add(gTemp);
				}
			}else{
				System.err.println(policyFileName + "が開けません");
			}
			
		}catch(FileNotFoundException e){
			 e.printStackTrace();
			 System.err.println(e);
		}catch(IOException e){
			e.printStackTrace();
			 System.err.println(e);
		}
		
	}

	public void Learning(char systemActionType, String policy, GridPointSet gps, RecognitionRate recogInfo){
		setGridPointSet(policy, gps);
		Learning(systemActionType, gps, recogInfo);
	}
	
	public void Learning(char systemActionType, GridPointSet gps, RecognitionRate recogInfo){
		Date nowDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
		
		File outFile = new File("result/" + df.format(nowDate) + ".txt");
		try {
			FileWriter fw = new FileWriter(outFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
//			PrintWriter pw = new PrintWriter(System.out);
			ArrayList<BeliefPointAndSelectedAction> history = new ArrayList<BeliefPointAndSelectedAction>();
			
			System.out.println("output file: " + outFile.getName());
			int[]   turnNum = {0};
			double[] reword = {0.0};
			double[] h_rH = new double[10001];
			double totalReword = 0.0;
			double aveRewoadMax = -1000.0;
			double hyouzyun = 0.0;

			int   totalTurnNum = 0;
			int      trainingNum = 0;
			int total = 0;
			int trainingMax = systemActionType == SYSTEM_ACTION_E_GREEDY ? 10001 : 1001;
			long start = System.currentTimeMillis();
			while (trainingNum < trainingMax){
				pw.println("++++++++++++++++++++++++++++++++++++++++++");
				pw.println("+ training: " + trainingNum);
				pw.println("++++++++++++++++++++++++++++++++++++++++++");
				int status;
			
				status = mainDialogue(systemActionType, gps, recogInfo, history, turnNum, reword, pw);
				if (systemActionType == SYSTEM_ACTION_E_GREEDY){
					mainLearning(history, gps, reword[0]);
				}
				
				trainingNum++;
				totalReword = totalReword + reword[0];
				totalTurnNum = totalTurnNum + turnNum[0];
				h_rH[trainingNum-1]=reword[0];
				
				if (status == 1){
					if (trainingNum % 5 == 0) System.out.print("+"); else System.out.print("."); 
				}else{
					System.out.print("|");
				}
				if (trainingNum % 50 == 0){
					double ave = totalReword / trainingNum;
					double aveTurn = (double)totalTurnNum / trainingNum;
					long end = System.currentTimeMillis();
					
					System.out.printf("%5d:aveR[%5.3f]:areTurn[%5.3f]:gridPointNum[%4d]:time[%8.3f]\n", trainingNum, ave, aveTurn, gps.gridPointArrayList.size(), (end-start)/1000.0);
					if (aveRewoadMax - 1 > ave){
						break;
					}
					if (aveRewoadMax < ave){
						aveRewoadMax = ave;
					}
				}
				if (trainingNum % 500 == 0 && systemActionType == SYSTEM_ACTION_E_GREEDY){
					File policyFile = new File("policy/" + trainingNum + ".policy");
					try{
						FileWriter fwp = new FileWriter(policyFile);
						BufferedWriter bwp = new BufferedWriter(fwp);
						PrintWriter pwp = new PrintWriter(bwp);
						for(int i = 0;i < gps.gridPointArrayList.size(); i++){
							pwp.println("" + gps.gridPointArrayList.get(i));
						}
						pwp.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
			trainingNum--;
			double ave =(double) totalReword / trainingNum;
			double vt = 0;
			double v = 0;
			for(int i = 0; i<trainingNum;i++){
				vt = vt + (h_rH[i] - ave)*(h_rH[i] - ave);
			}
			v = vt / (trainingNum - 1);
			hyouzyun = Math.sqrt(v);
			System.out.printf("average = %7.2f ± %7.2f\n", ave, hyouzyun);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public void mainLearning(ArrayList<BeliefPointAndSelectedAction> history, GridPointSet gps, double R){
		// 学習フェーズ
		for(int i = history.size() - 1; i>=0; i--){
			BeliefPoint bp = history.get(i).beliefPoint;
			ActionEnum  ma = history.get(i).selectedAction.type;
			
			double[] distance = {THREASHHOLD * 10};
			GridPoint gTemp = gps.getNearestGridPoint(distance, bp);
			
			if (gps.gridPointArrayList.size()>0 && distance[0] < THREASHHOLD){
//				System.out.println("bp=" + bp + "gp=" + gTemp.beliefPoint + "O: distance = " + distance[0]);
				gps.updateGridPoint(gTemp, ma, R);
			}else{
//				if (gps.gridPointArrayList.size() > 0)
//					System.out.println("bp=" + bp + "gp=" + gTemp.beliefPoint + "X: distance = " + distance[0]);
				gps.addGridPoint(bp, ma, R);
			}
			R = R * GAMMA;
		}
		
		// 政策最適化フェーズ
		for (int i = 0; i < gps.gridPointArrayList.size(); i++){
			GridPoint gTemp = gps.gridPointArrayList.get(i);
			Iterator<ActionEnum> it = gTemp.number.keySet().iterator();
			ActionEnum bestAction = null;
			for(boolean isFirst = true;it.hasNext();){
				ActionEnum aTemp = it.next();
				if (isFirst) {
					bestAction = aTemp;
					isFirst = false;
				}else{
					if (gTemp.valueFunction.get(bestAction) < gTemp.valueFunction.get(aTemp)){
						bestAction = aTemp;
					}
				}
			}
			gTemp.optimizedAction = bestAction;
		}
		
	}

	public int mainDialogue(char systemActionType, GridPointSet gps, RecognitionRate recogInfo, ArrayList<BeliefPointAndSelectedAction> history, int[] turnNum, double[] reword, PrintWriter pw){
		/** 観測のセット */ 
		Observation[] O = new Observation[recogInfo.nbest];
		// パーティションとユーザゴールの初期化
		OntologyTree goal = userActionSet.userGoalArrayList.get((int)(Math.random() * userActionSet.userGoalArrayList.size()));
		BeliefStateSet bss = new BeliefStateSet(ontologyBase, userActionSet, systemActionSet, goal);
		
		// システムアクションの初期化
		Action lastSystemAction = systemActionSet.searchAction(ActionEnum.HELLO);
		
		ArrayList<ONode> miList  = new ArrayList<ONode>();  // マッチするアイテム
		ArrayList<ONode> neiList = new ArrayList<ONode>(); // 存在しないアイテム
		ArrayList<ONode> ddiList = new ArrayList<ONode>(); // アイテムはあるけど中のデータが違うアイテム
		ArrayList<ONode> deiList = new ArrayList<ONode>(); // アイテムはあるけど中が空のアイテム
		ArrayList<ONode> oeiList = new ArrayList<ONode>(); // ゴールには存在しないアイテム
		
		double R = 0.0;
		int turn = 1;
		
		do {
			pw.println("=================================================================");
			pw.println("================= turn: " + turn + "========================================"); 
						
			miList.clear();  neiList.clear(); ddiList.clear();
			deiList.clear(); oeiList.clear();
			boolean isConsistency;

			bss.first.state.partition.node.setInconsistencyItemList(goal, miList, neiList, ddiList, deiList, oeiList);
			if (ddiList.size()>0 || oeiList.size()>0){
				isConsistency = false;
			}else{
				isConsistency = true;
			}
			
			makeObservation(O, recogInfo, bss.first.state.partition.node, goal, lastSystemAction, isConsistency, 
					miList, neiList, ddiList, deiList, oeiList, pw);
			pw.println("**************************************");
			pw.println("Goal: " + goal.rootNode.toString_simple());
			pw.println("**************************************");
			pw.println("Obserbation");
			for(Observation oTemp: O) {
				pw.println("  " + oTemp);
				if (oTemp.action == null){
					R = R - 10;
					reword[0]  = R;
					turnNum[0] = turn - 1;
					return 0;
				}
			}
			
			if (! bss.update(lastSystemAction, O, isConsistency)){
				pw.close(); System.exit(0);
			}
			
			bss.partition.updateMaxBeliefState();
			
//			pw.println("**************************************");
//			pw.println("After  Partition");
//			pw.println("" + bss.partition);
			
			pw.println("**************************************");
			pw.println("Belief State");
			pw.println("" + bss);
			
			// 信念ポイントを計算する
			BeliefPoint bp = new BeliefPoint(bss.first.prob, bss.second.prob, bss.first.state, lastSystemAction);
			pw.println("**************************************");
			pw.println("" + bp);
			
			
			lastSystemAction = selectSystemAction(systemActionType, bss.first.state.partition.node, bp, gps);
			
			pw.println("**************************************");
			pw.println("System Action: " + lastSystemAction);
			
			
			// beliefPoint と 選択したシステムアクションを履歴として保存する
			history.add(new BeliefPointAndSelectedAction(bp, lastSystemAction));
			pw.println("**************************************");
			pw.println("Partition num: " + bss.partition.getPartitionNum());
			pw.println("Partition: " + bss.partition);
			
			turn = turn + 1;
			R = R - 1; 

		} while(lastSystemAction.type != ActionEnum.BYE && turn < 200);
		turnNum[0] = turn;
		if (lastSystemAction.type == ActionEnum.BYE){ 
			pw.println("turn: " + turn + " / complete.");
			R = R + 20;
			reword[0]  = R;
			return 1;
		} else {
			pw.println("turn: " + turn + " / not complete.");
			R = R - 10;
			reword[0]  = R;
			return 0;

		}
		
	}
	
	/** 
	 * @param oList 観測を入れる ArrayList
	 * @param o     前回の理解結果を示す OntologyTree
	 * @param g     現在の対話におけるユーザゴール
	 * @param lsa   前回のシステムアクション
	 */
	public void makeObservation(Observation[] oList, RecognitionRate recogInfo, OntologyTree o, OntologyTree g, Action lsa, 
			boolean isConsistency, ArrayList<ONode> miList, ArrayList<ONode> neiList, 
			ArrayList<ONode> ddiList, ArrayList<ONode> deiList, ArrayList<ONode> oeiList, PrintWriter pw){

		ActionEnum nextUserActionType;
		nextUserActionType = userActionSet.randomSelectUserActionType(lsa, isConsistency);
		if (isConsistency && lsa.type == ActionEnum.REQUEST)             nextUserActionType = ActionEnum.INFORM;
		if (isConsistency && nextUserActionType == ActionEnum.DENY)      nextUserActionType = ActionEnum.INFORM;
		if (isConsistency && lsa.type == ActionEnum.CONFIRM)             nextUserActionType = ActionEnum.AFFIRM;
		if (isConsistency && nextUserActionType == ActionEnum.BYE)       nextUserActionType = ActionEnum.INFORM;
		if (! isConsistency && nextUserActionType == ActionEnum.AFFIRM)  nextUserActionType = ActionEnum.INFORM;

//		System.err.println("  nextUserActionType = " + nextUserActionType);
//		System.err.println("  isConsistency      = " + isConsistency);
//		System.err.println("  miLst = "  + miList);
//		System.err.println("  neiLst = " + neiList);
//		System.err.println("  ddiLst = " + ddiList);
//		System.err.println("  deiLst = " + deiList);
//		System.err.println("  oeiLst = " + oeiList);
		
		Action correctAction = null;
		if (isConsistency){  // ゴールと矛盾していないとき
			if (nextUserActionType == ActionEnum.INFORM){
				if (deiList.size()>0){ // 詳細化
					ArrayList<Info> iArray = new ArrayList<Info>();
					for(ONode t: miList) iArray.add(t.dataInfo);
					for(ONode t: deiList) iArray.add(t.dataInfo);
					correctAction = userActionSet.selectRandomAction(nextUserActionType, iArray);
				}else if (miList.size()>0){ // 復唱
					ArrayList<Info> iArray = new ArrayList<Info>();
					for(ONode t: miList) iArray.add(t.dataInfo);
					correctAction = userActionSet.selectRandomAction(nextUserActionType, iArray);
				}else{
					System.err.println("inform: User Action not found.");
					System.exit(0);
				}
			}else if (nextUserActionType == ActionEnum.AFFIRM){
				ArrayList<Info> iArray = new ArrayList<Info>();
				for(Info t: lsa.itemArrayList) iArray.add(t);
				correctAction = userActionSet.selectRandomAction(nextUserActionType, iArray);
			}
		} else { // ゴールと矛盾しているとき
			if (nextUserActionType == ActionEnum.INFORM){
				ArrayList<Info> iArray = new ArrayList<Info>();
				for(ONode t: miList) iArray.add(t.dataInfo);
				for(ONode t: ddiList) iArray.add(t.dataInfo);
				correctAction = userActionSet.selectRandomAction(nextUserActionType, iArray);
			} else if (nextUserActionType == ActionEnum.DENY){
				ArrayList<Info> iArray = new ArrayList<Info>();
				iArray.add(ddiList.get((int)(Math.random() * ddiList.size())).dataInfo);
				correctAction = userActionSet.selectRandomAction(nextUserActionType, iArray);
			}
		}
		double rand = Math.random();
		ArrayList<Action> list = userActionSet.searchRandomAction(correctAction);
		
		for(int i = 0; i < recogInfo.nbest; i++){
			oList[i] = new Observation();
			if (recogInfo.recogRate[i].isContains(rand)){
				oList[i].action = correctAction;
				oList[i].isCorrect = true;
			}else{
				oList[i].action = list.get(i);
				oList[i].isCorrect = false;
			}
			oList[i].prob = recogInfo.observationProb[i];
		}
	}
	
	public Action selectSystemAction(char sat, OntologyTree oTree, BeliefPoint bp, GridPointSet gps){
		Action lma = null;
		ActionEnum lmaType = ActionEnum.INFORM;
		double rand = Math.random();
		EnumMap<ActionEnum,Range> randList = new EnumMap<ActionEnum, Range>(ActionEnum.class);
		randList.put(ActionEnum.CONFIRM, new Range(0    , 0.333));
		randList.put(ActionEnum.INFORM,  new Range(0.333, 0.667));
		randList.put(ActionEnum.REQUEST, new Range(0.667, 1.001));
		ActionEnum temp;
		Range epsilonGreedy = new Range(0,EPSILON);
		double r = Math.random();
		Range range;
		if (sat != Dialogue.SYSTEM_ACTION_ALWAYS_PAI &&
			(sat == Dialogue.SYSTEM_ACTION_ALWAYS_RANDOM || 
			epsilonGreedy.isContains(r) ||
			gps.gridPointArrayList.size() == 0)){
			Iterator<ActionEnum> it = randList.keySet().iterator();
			
			while (it.hasNext()) {
				temp = it.next();
				range = randList.get(temp);
				if (range.isContains(rand)){
					lmaType = temp;
				}
			}
		}else {
			double[] dTemp = {0.0};
			lmaType = gps.getNearestGridPoint(dTemp, bp).optimizedAction;
		}
			
		if (bp.partitionStatus == PartitionStatusEnum.UNIQUE &&
				(bp.hypothesisStatus == HypothesisStatusEnum.OFFERED || 
				bp.hypothesisStatus == HypothesisStatusEnum.ACCEPTED)){
			lmaType = ActionEnum.CONFIRM;
		}
		
		if (bp.partitionStatus == PartitionStatusEnum.UNIQUE && bp.hypothesisStatus == HypothesisStatusEnum.SUPPORTED){
			lmaType = ActionEnum.BYE;
		}
			
		if (lmaType == ActionEnum.REQUEST){
			lma = systemActionSet.getConsistencySystemAction(oTree, lmaType);
			if (lma == null){
				lma = systemActionSet.getActionFromOntologyTree(oTree, ActionEnum.INFORM);
			}
		}else if (lmaType == ActionEnum.INFORM || lmaType == ActionEnum.CONFIRM){
			lma 	= systemActionSet.getActionFromOntologyTree(oTree, lmaType);
			if (lma == null){
				lma = systemActionSet.getConsistencySystemAction(oTree, ActionEnum.REQUEST);
			}
		}else if (lmaType == ActionEnum.BYE){
			lma = systemActionSet.searchAction(ActionEnum.BYE);
		}
		
		return lma;
	}
	
	public void test01(){
		OntologyBase base = ontologyBase;
		UserActionSet ual = userActionSet;
		SystemActionSet sal = systemActionSet;
		
		System.out.println("****************************************");
		System.out.println("* User Action Set *");
		System.out.println("****************************************");
		for(Action a: ual.actionArrayList){
			System.out.println("" + a);
		}
		System.out.println("****************************************");
		System.out.println("* System Action Set *");
		System.out.println("****************************************");
		for(Action a: sal.actionArrayList){
			System.out.println("" + a);
		}
		
		ItemONode io1    = new ItemONode(base.itemInfoHashMap.get("I:目的地タイプ"));
		SubtypeONode so1 = new SubtypeONode(io1, base.subtypeInfoHashMap.get("S:コンビニ"));
		io1.child = so1;
		ItemONode io2    = new ItemONode(so1, base.itemInfoHashMap.get("I:コンビニ"));
		so1.setChild(io2);
		SubtypeONode so2 = new SubtypeONode(io2, base.subtypeInfoHashMap.get("S:セブンイレブン"));
		io2.child = so2;
		AtomONode ao1    = new AtomONode(so2, base.atomInfoHashMap.get("P:セブンイレブン施設名"));
		so2.setChild(ao1);
		
		System.out.println("" + io1);

		System.out.println("****************************************");
		System.out.println("* Ontology Test *");
		System.out.println("****************************************");
		
		OntologyTree to = new OntologyTree(base);
		for(Action a: ual.actionArrayListforEachTypeHashMap.get(ActionEnum.INFORM)){
			System.out.println("****************************************");
			System.out.println("Action: " + a);
			to.initialize(a);
			System.out.println("" + to);
		}
		
		Partition p = new Partition(base);
		p.initialize();
		System.out.println("" + p);
		
		System.out.println("****************************************");
		System.out.println("* Action Set Test *");
		System.out.println("****************************************");
		
		ArrayList<Info> iArray = new ArrayList<Info>();
		iArray.add(base.subtypeInfoHashMap.get("S:コンビニ"));
		iArray.add(base.subtypeInfoHashMap.get("S:セブンイレブン"));
		System.out.println("**NORMAL**************************************");
		ArrayList<Action> temp = ual.searchActionList(ActionEnum.INFORM, iArray, ActionSet.NORMAL_MATCH);
		for(int i = 0; i < temp.size(); i++){
			System.out.println("" + temp.get(i));
		}
		System.out.println("**ONLY**************************************");
		temp = ual.searchActionList(ActionEnum.INFORM, iArray, ActionSet.ONLY_MATCH);
		for(int i = 0; i < temp.size(); i++){
			System.out.println("" + temp.get(i));
		}

		System.out.println("****************************************");
		System.out.println("* User Goal Set *");
		System.out.println("****************************************");
		for(int i = 0;i < ual.userGoalArrayList.size(); i++){
			OntologyTree tempOn = ual.userGoalArrayList.get(i);
			System.out.println("" + tempOn);
		}
		OntologyTree goal = ual.userGoalArrayList.get(0);  // サークルＫ浜松古人見店
		
		System.out.println("****************************************");
		System.out.println("* Partition and Dialogue History Update Test *");
		System.out.println("****************************************");
		
		iArray.clear();
		Action lsa;
		Action ua;
		
		lsa = sal.searchAction(ActionEnum.HELLO);            // hello(目的地タイプ=S:コンビニ) 
		iArray.add(base.subtypeInfoHashMap.get("S:コンビニ"));  // I:目的地タイプ=S:コンビニ
		ua = ual.searchAction(ActionEnum.INFORM, iArray);    // inform(目的地タイプ=S:コンビニ) 
		DialogueHistory dh = new DialogueHistory();
		System.out.println("****************************************");
		ArrayList<ONode> miList  = new ArrayList<ONode>();  // マッチするアイテム
		ArrayList<ONode> neiList = new ArrayList<ONode>(); // 存在しないアイテム
		ArrayList<ONode> ddiList = new ArrayList<ONode>(); // アイテムはあるけど中のデータが違うアイテム
		ArrayList<ONode> deiList = new ArrayList<ONode>(); // アイテムはあるけど中が空のアイテム
		ArrayList<ONode> oeiList = new ArrayList<ONode>(); // ゴールには存在しないアイテム
		p.node.setInconsistencyItemList(goal, miList, neiList, ddiList, deiList, oeiList);
		System.out.print("matchItem: ");
		for(ONode t: miList){
			System.out.print(t.baseInfo.getName() + "=" + t.dataInfo.getName() + ", ");
		}
		System.out.println();
		System.out.print("notExistItem: ");
		for(ONode t: neiList){
			System.out.print(t.baseInfo.getName() + "=" + t.dataInfo.getName() + ", ");
		}
		System.out.println();
		System.out.print("differentDataItem: ");
		for(ONode t: ddiList){
			System.out.print(t.baseInfo.getName() + "=" + t.dataInfo.getName() + ", ");
		}
		System.out.println();
		System.out.print("dataEmptyItem: ");
		for(ONode t: deiList){
			System.out.print(t.baseInfo.getName() + "=" + t.dataInfo.getName() + ", ");
		}
		System.out.println();
		System.out.print("onlyExistItem: ");
		for(ONode t: oeiList){
			System.out.print(t.baseInfo.getName() + "=" + t.dataInfo.getName() + ", ");
		}
		System.out.println();
		System.out.println("****************************************");
		System.out.println("last system action : " + lsa);
		System.out.println("user action        : " + ua);
		dh.update(lsa, ua);
		System.out.println("" + dh);
		p.update(ua);
		System.out.println("" + p);
		
		System.out.println("****************************************");
		iArray.clear();
		iArray.add(base.subtypeInfoHashMap.get("S:コンビニ"));  // I:目的地タイプ=S:コンビニ
		iArray.add(base.itemInfoHashMap.get("I:コンビニ"));  // I:コンビニ
//		lsa = sal.searchAction(ActionEnum.REQUEST, iArray);  // request(I:コンビニ,I:目的地タイプ=S:コンビニ)
		Partition targetP = p.childArrayList.get(0);
		lsa = sal.getConsistencySystemAction(targetP.node, ActionEnum.REQUEST);
		iArray.clear();
		iArray.add(base.subtypeInfoHashMap.get("S:コンビニ"));  // I:目的地タイプ=S:コンビニ
		iArray.add(base.subtypeInfoHashMap.get("S:セブンイレブン"));  // 
		iArray.add(base.dataInfoHashMap.get("セブンイレブン浜松相生店"));  // 
		ua  = ual.searchAction(ActionEnum.INFORM, iArray);       // inform(I:目的地タイプ=S:コンビニ,I:コンビニ=S:セブンイレブン) 
		System.out.println("last system action : " + lsa);
		System.out.println("user action        : " + ua);
		dh.update(lsa, ua);
		System.out.println("" + dh);
		p.update(ua);
		System.out.println("" + p);
		p.update(ua);
		System.out.println("" + p);
		
		
		System.out.println("****************************************");
		System.out.println("* System Action Generation Test *");
		System.out.println("****************************************");
		
		OntologyTree oTemp1 = (OntologyTree)p.node;
		OntologyTree oTemp2 = (OntologyTree)p.node.clone();
		OntologyTree oTemp3 = (OntologyTree)p.childArrayList.get(0).node;
		OntologyTree oTemp4 = (OntologyTree)p.childArrayList.get(0).node.clone();
		OntologyTree oTemp5 = (OntologyTree)p.childArrayList.get(0).childArrayList.get(0).node;
		OntologyTree oTemp6 = (OntologyTree)p.childArrayList.get(0).childArrayList.get(0).node.clone();
		OntologyTree oTemp7 = (OntologyTree)p.childArrayList.get(0).childArrayList.get(0).childArrayList.get(0).node;
		OntologyTree oTemp8 = (OntologyTree)p.childArrayList.get(0).childArrayList.get(0).childArrayList.get(0).node.clone();
		
		System.out.println("****************************************");
		System.out.println("oTemp1\n" + oTemp1);
		System.out.println("oTemp1 clone\n" + oTemp2);
		System.out.println("****************************************");
		System.out.println("oTemp3\n" + oTemp3);
		System.out.println("oTemp3 clone\n" + oTemp4);
		System.out.println("****************************************");
		System.out.println("oTemp5\n" + oTemp5);
		System.out.println("oTemp5 clone\n" + oTemp6);
		System.out.println("****************************************");
		System.out.println("oTemp7\n" + oTemp7);
		System.out.println("oTemp7 clone\n" + oTemp8);
		System.out.println("****************************************");
		
		
		
	}
	
	public static void selectUserAction(ArrayList<Observation> oList){
		
	}
}
