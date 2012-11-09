package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.EnumMap;

import javax.naming.directory.InitialDirContext;

public class BeliefStateSet {
	UserActionSet userActionSet;
	SystemActionSet systemActionSet;
	Partition partition;
	
	OntologyBase ontologyBase;
	OntologyTree userGoal;
	ArrayList<BeliefState> beliefStateArrayList;
	BeliefState first;
	BeliefState second;
	
	BeliefStateSet(OntologyBase b, UserActionSet uas, SystemActionSet sas, OntologyTree goal){
		ontologyBase = b;
		beliefStateArrayList = new ArrayList<BeliefState>();
		userActionSet        = uas;
		systemActionSet      = sas;
		userGoal             = goal;
		
		// 対話状態とユーザアクションの初期状態
		DialogueHistory dh0 = new DialogueHistory();
		Action ua0 = userActionSet.searchAction(ActionEnum.NONE);

		// パーティションとユーザゴールの初期化
		Partition p = new Partition(ontologyBase);
		p.initialize();
		partition = p;
		
		// 初期状態の作成
		State s0 = new State(p, ua0, dh0);
		
		// 信念状態の初期化
		BeliefState b0 = new BeliefState(s0);
		b0.prob = 1.0;
		beliefStateArrayList.add(b0);
		
		
		p.beliefStateArrayList.add(b0);	
		p.maxBeliefState = b0;
		
		first = b0;
		second = b0;
		
	}
	
	
	
	public boolean update(Action lsa, Observation[] oList, boolean isConsistency){
		ArrayList<PartitionAndObservation> partitionList = new ArrayList<PartitionAndObservation>();
		// Partition を update する
		int oldStateNum = beliefStateArrayList.size();
		for(Observation oTemp: oList){
			partition.update(oTemp.action);
		}
		// それぞれのユーザアクションに矛盾しない partition を探索する
		for(Observation oTemp: oList){
			for (Partition pTemp: partition.getConsistencyPartition(oTemp.action)){
				partitionList.add(new PartitionAndObservation(pTemp, oTemp));	
			}
		}
		
		for(PartitionAndObservation poTemp: partitionList) {
			BeliefState prevBeliefState = poTemp.partition.maxBeliefState;
			DialogueHistory newsd;
			newsd = (DialogueHistory)prevBeliefState.state.dialogueHistory.clone();
			newsd.update(lsa, poTemp.observation.action);
			State newst = new State(poTemp.partition, poTemp.observation.action, newsd);
			BeliefState newBeliefState = new BeliefState(newst);
			EnumMap<ActionEnum, Range> probList; 
			if (isConsistency) {
				probList = userActionSet.userActionProbEachSystemActionC.get(lsa.type); 
			}else{
				probList = userActionSet.userActionProbEachSystemActionW.get(lsa.type); 
			}
			newBeliefState.prob = 
					poTemp.observation.prob *  // 観測確率
					probList.get(poTemp.observation.action.type).value() *  // ユーザアクションモデル
					poTemp.partition.prob *   // ユーザゴールモデル
					prevBeliefState.prob;
			
			if (Double.isNaN(newBeliefState.prob)){
				newBeliefState.prob = 0.000001;
			}
			
			poTemp.partition.beliefStateArrayList.add(newBeliefState); // パーティションに信念状態をリンクさせておく
			
			beliefStateArrayList.add(newBeliefState);
		}
		
		// 古い信念状態の情報を削除
		for(int i = 0; i < oldStateNum; i++){
			beliefStateArrayList.remove(0);
		}
		// 各パーティションのパターン数(下に持ち得る施設数)を保存
		partition.storeNumberOfPattern();
		// 各パーティションの確率を計算
//		partition.calcProb();
		
		// 正規化
		double total = 0.0;
		for(BeliefState temp: beliefStateArrayList){
			total = total + temp.prob;
		}
		for(BeliefState temp: beliefStateArrayList){
			temp.prob = temp.prob / total;
		}
		
		int firstIndex = 0;
		int secondIndex;
		
		for(int i = 1; i < beliefStateArrayList.size(); i++){
			BeliefState temp = beliefStateArrayList.get(i);
			if (beliefStateArrayList.get(firstIndex).prob < temp.prob) firstIndex = i;
		}
		
		secondIndex = 0;
		if (beliefStateArrayList.size()>1) {
			if (firstIndex == 0){
				secondIndex = 1;
				for (int i = 0; i < firstIndex; i++){
					BeliefState temp = beliefStateArrayList.get(i);
					if (beliefStateArrayList.get(secondIndex).prob < temp.prob) secondIndex = i;
				}
				for (int i = firstIndex+1; i < beliefStateArrayList.size(); i++){
					BeliefState temp = beliefStateArrayList.get(i);
					if (beliefStateArrayList.get(secondIndex).prob < temp.prob) secondIndex = i;
				}
			}
		}
		if (beliefStateArrayList.size()<1){
			// 対話状態とユーザアクションの初期状態
			DialogueHistory dh0 = new DialogueHistory();
			Action ua0 = userActionSet.searchAction(ActionEnum.NONE);
			
			// パーティションとユーザゴールの初期化
			Partition p = new Partition(ontologyBase);
			p.initialize();
			partition = p;
			
			// 初期状態の作成
			State s0 = new State(p, ua0, dh0);
			
			// 信念状態の初期化
			BeliefState b0 = new BeliefState(s0);
			b0.prob = 1.0;
			beliefStateArrayList.add(b0);
			
			
			p.beliefStateArrayList.add(b0);	
			p.maxBeliefState = b0;
			
			first = b0;
			second = b0;
			return true;
		}
		first  = beliefStateArrayList.get(firstIndex);
		second = beliefStateArrayList.get(secondIndex);
		return true;
	}
	
	public String toString(){
		String str;
		str = "BeliefStateSet\n";
		for(int i = 0; i < beliefStateArrayList.size(); i++){
			BeliefState temp = beliefStateArrayList.get(i);
			if (first == temp){
				str = str + "**";
			} else if (second == temp){
				str = str + " *";
			}else{
				str = str + "  ";
			}
			
			str = str + "BeliefState[" + i + "] = " + temp.prob + "\n";
			str = str + "      UserAction: " + temp.state.userAction + "\n";
			str = str + "      " + temp.state.dialogueHistory + "\n";
			str = str + temp.state.partition.toString_this("      ") + "\n";
		}
		return str;
	}
	
	
}

