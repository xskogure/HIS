package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;

public class UserActionSet extends ActionSet {
	/** ユーザゴールのリスト */
	ArrayList<OntologyTree> userGoalArrayList;
	
	/** ゴールと矛盾しないオントロジによってシステムアクションが作られた際の次のユーザ発話のタイプ別正規確率 */
	EnumMap<ActionEnum,EnumMap<ActionEnum,Range>> userActionProbEachSystemActionC;
	/** ゴールと矛盾するオントロジによってシステムアクションが作られた際の次のユーザ発話のタイプ別正規確率 */
	EnumMap<ActionEnum,EnumMap<ActionEnum,Range>> userActionProbEachSystemActionW;


	UserActionSet(OntologyBase b){
		super(b);
		setAction(new Action(ActionEnum.NONE));
		initializeType1(b, ActionEnum.INFORM);
		initializeType1(b, ActionEnum.AFFIRM);
		initializeType2(b, ActionEnum.DENY);
		userGoalArrayList = new ArrayList<OntologyTree>();
		setUserGoal();
		userActionProbEachSystemActionC = new EnumMap<ActionEnum,EnumMap<ActionEnum,Range>>(ActionEnum.class); 
		userActionProbEachSystemActionW = new EnumMap<ActionEnum,EnumMap<ActionEnum,Range>>(ActionEnum.class);
		setUserActionProbEachSystemAction();
	}
	
	public void setUserActionProbEachSystemAction (){
		EnumMap<ActionEnum,Range> temp;

		// ゴールと矛盾しない：System Action が Hello の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0   ));
		userActionProbEachSystemActionC.put(ActionEnum.HELLO, temp);
		
		// ゴールと矛盾しない：System Action が Bye の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.001)); temp.put(ActionEnum.DENY, new Range(0.001, 0.002)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.003, 0.004)); temp.put(ActionEnum.BYE , new Range(0.004, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.BYE, temp);
		
		// ゴールと矛盾しない：System Action が Request の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.REQUEST, temp);
		
		// ゴールと矛盾しない：System Action が INFORM の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.INFORM, temp);
		
		// ゴールと矛盾しない：System Action が CONFIRM の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.001)); temp.put(ActionEnum.DENY, new Range(0.001, 0.002)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.002, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.CONFIRM, temp);

		
		
		// ゴールと矛盾する：System Action が Hello の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.HELLO, temp);
		
		// ゴールと矛盾する：System Action が Bye の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.BYE, temp);
		
		// ゴールと矛盾する：System Action が Request の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.REQUEST, temp);
		
		// ゴールと矛盾する：System Action が INFORM の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.INFORM, temp);
		
		// ゴールと矛盾する：System Action が CONFIRM の際の次の User Action の選択確率 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.CONFIRM, temp);
	}
	
	/**
	 * 
	 * @param lsa           前回のシステムアクション
	 * @param isConsistency ゴールと矛盾していなければ true, いたら false
	 * @return 次に取るべきユーザアクションのタイプ
	 */
	public ActionEnum randomSelectUserActionType(Action lsa, boolean isConsistency){
		EnumMap<ActionEnum,Range> temp;
		if (isConsistency) {
			temp = userActionProbEachSystemActionC.get(lsa.type); 
		}else{
			temp = userActionProbEachSystemActionW.get(lsa.type); 
		}
		
		Iterator<ActionEnum> it = temp.keySet().iterator();
		ActionEnum obj = null;
		Range range;
		double r = Math.random();
		
		while (it.hasNext()) {	// 次の要素があるならブロック内を実行
			obj = it.next();	// 次の要素を取り出す
			range = temp.get(obj);
			if (range.isContains(r)){
				return obj;
			}
		}
		return obj;
	}
	
	public void setUserGoal(){
		ArrayList<Action> temp = actionArrayListforEachTypeHashMap.get(ActionEnum.INFORM);
		for(int i = 0;i < temp.size(); i++){
			OntologyTree ot = new OntologyTree(base);
			ot.initialize(temp.get(i));
			if (ot.rootNode.getNumberOfPattern(true) == 1){
				userGoalArrayList.add(ot);
			}
		}
	}
}
