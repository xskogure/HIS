package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.Iterator;

public class BeliefPoint {
	double first;
	double second;
	HypothesisStatusEnum hypothesisStatus;
	PartitionStatusEnum  partitionStatus;
	ActionEnum lastSystemAction;
	
	BeliefPoint(Double b1, Double b2, State s, Action sa){
		first = b1;
		second = b2;
		hypothesisStatus = getHypothesisStatus(s.partition, s.dialogueHistory, s.userAction);
		partitionStatus  = getPartitionStatus(s.partition, s.userAction);
		lastSystemAction = sa.type;
	}
	
	BeliefPoint(Double b1, Double b2, HypothesisStatusEnum h, PartitionStatusEnum p, ActionEnum a){
		first = b1;
		second = b2;
		hypothesisStatus = h;
		partitionStatus  = p;
		lastSystemAction = a;
	}
	
	private HypothesisStatusEnum getHypothesisStatus(Partition p, DialogueHistory sd, Action ua){
		if (p.idList.size() == 1 && p.childArrayList.size() == 0){
			return HypothesisStatusEnum.INITIAL;
		}
		
		if (p.node.rootNode.getNumberOfPattern(true) == 0){
			return HypothesisStatusEnum.NOTFOUND;
		}
		
		for(int i = 0; i < p.node.atomONodeArrayList.size(); i++){
			ONode temp = p.node.atomONodeArrayList.get(i);
			if (sd.infoHashMap.containsKey(temp.baseInfo) && sd.infoHashMap.get(temp.baseInfo) == DHenum.GRND) {
				return HypothesisStatusEnum.SUPPORTED;
			}
			
		}
		
		Iterator<Info> it = sd.infoHashMap.keySet().iterator();
		for(;it.hasNext();){
			Info   iTemp = it.next();
			DHenum dTemp = sd.infoHashMap.get(iTemp);
			if (dTemp == DHenum.DENY){
				return HypothesisStatusEnum.REJECTED;
			}
		}
		
		return HypothesisStatusEnum.ACCEPTED;
	}
	
	private PartitionStatusEnum getPartitionStatus(Partition p, Action ua){
		if (p.idList.size() == 1 && p.childArrayList.size() == 0){
			return PartitionStatusEnum.INITIAL;
		}
		
		if (p.node.rootNode.getNumberOfPattern(true) == 1){
			return PartitionStatusEnum.UNIQUE;
		}else if (p.node.rootNode.getNumberOfPattern() == 0){
			return PartitionStatusEnum.UNKNOWN;
		}
		
		if ("new".equals(p.type)){
			return PartitionStatusEnum.SMALLGROUP;
		}else if  ("modified".equals(p.type)){
			return PartitionStatusEnum.HUGEGROUP;
		}
		return PartitionStatusEnum.GENERIC;
		
	}
	
	public String toString(){
		return "bp("+first+","+second+","+partitionStatus+","+hypothesisStatus+","+lastSystemAction+")";
	}
}
