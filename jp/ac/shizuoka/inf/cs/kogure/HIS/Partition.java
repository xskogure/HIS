package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;

public class Partition {
	static ArrayList<Partition> modifiedPartitionArrayList = new ArrayList<Partition>();
	static ArrayList<Partition> newPartitionArrayList      = new ArrayList<Partition>();
	static HashMap<String,Integer> numberOfPatternHashMap  = new HashMap<String,Integer>();
	
	ArrayList<BeliefState> beliefStateArrayList;
	BeliefState maxBeliefState;
	Partition parent;
	OntologyTree node;
	ArrayList<Integer> idList;
	ArrayList<Partition> childArrayList;
	double prob;
	String type;
	
	Partition(OntologyTree ot){
		node = ot;
		idList = new ArrayList<Integer>();
		childArrayList = new ArrayList<Partition>();
		beliefStateArrayList = new ArrayList<BeliefState>();
	}

	Partition(OntologyBase b){
		OntologyTree t = new OntologyTree(b);
		ItemONode io = new ItemONode(b.getRootNode());
		t.rootNode = io;
		t.setItem(io);
		node = t;
		idList = new ArrayList<Integer>();
		childArrayList = new ArrayList<Partition>();
		beliefStateArrayList = new ArrayList<BeliefState>();
	}
	
	public void initialize(){
		idList.add(new Integer(1));
	}
	
	public int getPartitionNum(){
		return getPartitionNum(this);
	}
	
	public int getPartitionNum(Partition p){
		int count = 1;
		for(Partition child: p.childArrayList){
			count = count + getPartitionNum(child);
		}
		return count;
	}
	
	public void setChild(Partition p){
		childArrayList.add(p);
		p.idList.addAll(this.idList);
		p.idList.add(new Integer(childArrayList.size()));
		p.parent = this;
	}
	
	public void storeNumberOfPattern(){
		numberOfPatternHashMap.clear();
		storeNumberOfPatternSub(this);
	}
	
	public void calcProb(){
		prob = 1.0;
		calcProbSub(this);
	}
	
	public void calcProbSub(Partition p){
		int total = 0;
		for(Partition child: p.childArrayList){
			total = total + child.node.rootNode.getNumberOfPattern(true);
		}
		for(Partition child: p.childArrayList){
			child.prob = (double)child.node.rootNode.getNumberOfPattern() / total;
		}
		for(Partition child: p.childArrayList){
			calcProbSub(child);
		}
	}
	
	public void storeNumberOfPatternSub(Partition p){
		numberOfPatternHashMap.put(p.getIDLabel(), p.node.rootNode.getNumberOfPattern(true));
		for(int i = 0;i < p.childArrayList.size(); i++){
			Partition temp = p.childArrayList.get(i);
			storeNumberOfPatternSub(temp);
		}
	}
	
	public void update(Action a){
		modifiedPartitionArrayList.clear();
		newPartitionArrayList.clear();
		updatePartition(this, a);
	}
	
	public void updatePartition(Partition p, Action a){
		updateNode(p, a);
		for (int i = 0; i < p.childArrayList.size(); i++){
			updatePartition(p.childArrayList.get(i), a);
		}
	}

	public void updateNode(Partition p, Action a){
		OntologyTree ot = p.node; // ç°íçñ⁄ÇµÇƒÇ¢ÇÈ OntologyTree 
		
		if (a.type == ActionEnum.HELLO || a.type == ActionEnum.INFORM ||
				a.type == ActionEnum.NEGATE || a.type == ActionEnum.DENY ){
			for (int i = 0; i < a.itemArrayList.size(); i++){
				Info acInfo = a.itemArrayList.get(i);
				if (acInfo.isSubtypeInfo()){  // Action ÉmèÓïÒÇÃÇ§ÇøÅCItemINfo -> SubtypeInfo ÇÃèÍçá
					SubtypeInfo sInfo = (SubtypeInfo)acInfo;
					ItemInfo    iInfo = sInfo.getParent();
					if (ot.itemONodeHashMap.containsKey(iInfo.getName())){ // ÉAÉNÉVÉáÉìíÜÇ…Ç†ÇÈ Item Ç™ Ontology Ç…Ç†Ç¡ÇΩèÍçá
						ONode onode = ot.itemONodeHashMap.get(iInfo.getName());
						if (! onode.isExistData() &&                                // íçñ⁄ Item Ç…Ç‹ÇæÉfÅ[É^Ç™äiî[Ç≥ÇÍÇƒÇ¢Ç»Ç¢ 
							! onode.isExistDenyData(sInfo) &&                       // íçñ⁄ Item ÇÃ DenyList Ç…ì¸Ç¡ÇƒÇ¢Ç»Ç¢
							onode.baseInfo.getChildHashMap().containsValue(sInfo)){ // íçñ⁄ Item ÇÃ baseInfo Ç… sInfo Ç™ìoò^Ç≥ÇÍÇƒÇ¢ÇÈ(Ç¬Ç‹ÇËÇªÇ±Ç…ì¸ÇÍÇÈÇ±Ç∆Ç™Ç≈Ç´ÇÈ)
							OntologyTree newot = (OntologyTree)ot.clone();
							int numberOforiginalPattern = ot.rootNode.getNumberOfPattern(true);
							if (numberOfPatternHashMap.containsKey(p.getIDLabel())){
								numberOforiginalPattern = numberOfPatternHashMap.get(p.getIDLabel());
							}
							int oriDatanum = numberOforiginalPattern;
							ItemONode changeItem;
							ItemONode targetItem;
							changeItem = (ItemONode)newot.itemONodeHashMap.get(iInfo.getName());
							targetItem = (ItemONode)ot.itemONodeHashMap.get(iInfo.getName());
							SubtypeONode newSubtype = new SubtypeONode(changeItem, sInfo);
							for(int j = 0; j < sInfo.getChildArrayList().size(); j++){
								Info temp = sInfo.getChildArrayList().get(j);
								if (temp.isItemInfo()){
									ItemONode newit = new ItemONode(newSubtype,temp);
									newot.setItem(newit);
									newSubtype.setChild(newit);
								}else if (temp.isAtomInfo()){
									AtomONode newat = new AtomONode(newSubtype, temp);
									newot.setAtom(newat);
									newSubtype.setChild(newat);
								}
							}
							targetItem.setDenyData((Info)sInfo);
							changeItem.setChild(newSubtype);
							Partition newPartition = new Partition(newot);
							newPartition.maxBeliefState = p.maxBeliefState;
							p.setChild(newPartition);
							p.type = "modified";
							newPartition.type = "new";
							int newDatanum = newot.rootNode.getNumberOfPattern(true);
							int modifiedDatanum = ot.rootNode.getNumberOfPattern(true);
//							p.prob            = modifiedDatanum == 0 ? 0.01 : (double)modifiedDatanum / oriDatanum;
//							newPartition.prob = newDatanum == 0 ? 0.01 : (double)newDatanum / oriDatanum;
							p.prob            = modifiedDatanum == 0 ? 0.01/ot.base.getDataNum() : (double)modifiedDatanum / ot.base.getDataNum();
							newPartition.prob = newDatanum == 0 ? 0.01/ot.base.getDataNum() : (double)newDatanum / ot.base.getDataNum();
						}
					}
				} else if (acInfo.isDataInfo()){
					DataInfo dInfo = (DataInfo)acInfo;
					AtomInfo aInfo = dInfo.getParent();
					if (ot.atomONodeHashMap.containsKey(aInfo.getName())){ // ÉAÉNÉVÉáÉìíÜÇ…Ç†ÇÈ Atom Ç™ Ontology Ç…Ç†Ç¡ÇΩèÍçá
						ONode onode = ot.atomONodeHashMap.get(aInfo.getName());
						if (! onode.isExistData() && 
							! onode.isExistDenyData(dInfo)  &&
							onode.baseInfo.getChildHashMap().containsValue(dInfo)){ // íçñ⁄ Item ÇÃ baseInfo Ç… sInfo Ç™ìoò^Ç≥ÇÍÇƒÇ¢ÇÈ(Ç¬Ç‹ÇËÇªÇ±Ç…ì¸ÇÍÇÈÇ±Ç∆Ç™Ç≈Ç´ÇÈ)

							OntologyTree newot = (OntologyTree)ot.clone();
							int numberOforiginalPattern = ot.rootNode.getNumberOfPattern(true);
							if (numberOfPatternHashMap.containsKey(getIDLabel())){
								numberOforiginalPattern = numberOfPatternHashMap.get(getIDLabel());
							}
							int oriDatanum = numberOforiginalPattern;
							AtomONode changeItem = (AtomONode)newot.atomONodeHashMap.get(aInfo.getName());
							AtomONode targetItem = (AtomONode)ot.atomONodeHashMap.get(aInfo.getName());
							targetItem.setDenyData((Info)dInfo);
							changeItem.dataInfo = dInfo;
							Partition newPartition = new Partition(newot);
							newPartition.maxBeliefState = p.maxBeliefState;
							p.setChild(newPartition);
							p.type = "modified";
							newPartition.type = "new";
							int newDatanum = newot.rootNode.getNumberOfPattern(true);
							int modifiedDatanum = ot.rootNode.getNumberOfPattern(true);
//							p.prob            = modifiedDatanum == 0 ? 0.01 : (double)modifiedDatanum / oriDatanum;
//							newPartition.prob = newDatanum == 0 ? 0.01 : (double)newDatanum / oriDatanum;
							p.prob            = modifiedDatanum == 0 ? 0.01/ot.base.getDataNum() : (double)modifiedDatanum / ot.base.getDataNum();
							newPartition.prob = newDatanum == 0 ? 0.01/ot.base.getDataNum() : (double)newDatanum / ot.base.getDataNum();
						}
					}
				}
			}
		}
	}
	
	public ArrayList<Partition> getConsistencyPartition(Action a){
		ArrayList<Partition> pArrayList;
		pArrayList = new ArrayList<Partition>();
		getConsistencyPartitionSub(this, a, pArrayList);
		return pArrayList;
	}
	
	public void getConsistencyPartitionSub(Partition p, Action a, ArrayList<Partition> pArrayList){
		int number[] = new int[4];
		p.node.checkMatchingUserAction(a, number);
		if (a.type == ActionEnum.DENY){
			if (number[OntologyTree.DENY_MATCH] == 1 || number[OntologyTree.OTHER_MAATCH] == 1){
				pArrayList.add(p);
			}
		}else {
			if (number[OntologyTree.MATCH] != 0 && number[OntologyTree.DENY_MATCH] == 0 && 
					number[OntologyTree.NO_MATCH] == 0 && number[OntologyTree.OTHER_MAATCH] == 0 ) {
				pArrayList.add(p);
			}
		}
		for(Partition child: p.childArrayList){
			getConsistencyPartitionSub(child, a, pArrayList);
		}
	}
	
	public void updateMaxBeliefState(){
		updateMaxBeliefStateSub(this);
	}
	
	public void updateMaxBeliefStateSub(Partition p){
		p.setMaxBeliefState();
		p.beliefStateArrayList.clear();
		for (Partition child: p.childArrayList){
			updateMaxBeliefStateSub(child);
		}
	}
	
	public void setMaxBeliefState(){
		if (beliefStateArrayList.size()>0){
			BeliefState max = beliefStateArrayList.get(0);
			for (int i = 1; i < beliefStateArrayList.size(); i++){
				if (max.prob < beliefStateArrayList.get(i).prob){
					max = beliefStateArrayList.get(i);
				}
			}
			maxBeliefState = max;
		}
	}
	
	public String getIDLabel() {
		String delim = ".";
        StringBuffer joinedString = new StringBuffer("");

        for (int i = 0; i < idList.size(); i++) {
                joinedString.append(delim);
                joinedString.append(idList.get(i));
        }

        return (joinedString.substring(delim.length())).toString();
	}

	public String toString(){
		return toString_P(0);
	}
	
	public String toString_P(int lv){
		String str = "";
		for(int i = 0; i < lv*2; i++) {str = str + " ";}
		str = str + "PARTITION: " + getIDLabel() + "\n";
		if (maxBeliefState != null){
			for(int i = 0; i < lv*2; i++) {str = str + " ";}
			str = str + "  prevBeliefProb: " + maxBeliefState.prob + "\n";
		}
		for(int i = 0; i < lv*2; i++) {str = str + " ";}
		str = str + "  prob: " + prob + "\n";
		for(int i = 0; i < lv*2; i++) {str = str + " ";}
		str = str + "  node: \n";
		str = str + node.rootNode.toString_I(lv+1);
		for(int i = 0; i < childArrayList.size(); i++){
			str = str + childArrayList.get(i).toString_P(lv+1);
		}
		return str;
	}	

	public String toString_this(String sp){
		String str = "";
		str = str + sp + "PARTITION: id: " + getIDLabel() + ", ";
		if (maxBeliefState != null){
			str = str + "pbsp: " + maxBeliefState.prob;
		}
		str = str + "prob: " + prob + "\n";
		str = str + sp + "            node: ";
		str = str + node.rootNode.toString_I_simple();
		str = str + "\n";
		return str;
	}	
}
