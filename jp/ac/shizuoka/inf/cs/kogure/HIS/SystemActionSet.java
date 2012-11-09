package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;


public class SystemActionSet extends ActionSet {
	
	SystemActionSet(OntologyBase base){
		super(base);
		setAction(new Action(ActionEnum.HELLO));
		setAction(new Action(ActionEnum.BYE));
		initializeType1(base, ActionEnum.INFORM);
		initializeType1(base, ActionEnum.CONFIRM);
		initializeType3(base, ActionEnum.REQUEST);
	}
	
	
	public ArrayList<Action> getConsistencySystemActionList(OntologyTree t, ActionEnum ae){
		ArrayList<Action> result = new ArrayList<Action>();
		ArrayList<Info> existList = new ArrayList<Info>();
		ArrayList<Info> notExistList = new ArrayList<Info>();
		for(int i = 0; i < t.itemONodeArrayList.size(); i++){
			ItemONode temp = (ItemONode)t.itemONodeArrayList.get(i);
			if (! temp.isExistData()){
				notExistList.add(temp.baseInfo);
			}else{
				existList.add(temp.dataInfo);
			}
		}
		if (notExistList.size()>0 && ae == ActionEnum.REQUEST){
			existList.addAll(notExistList);
		}
		result = searchActionList(ae, existList, NORMAL_MATCH);
		return result;
	}

	public Action getConsistencySystemAction(OntologyTree t, ActionEnum ae){
		ArrayList<Action> result = new ArrayList<Action>();
		result.addAll(getConsistencySystemActionList(t, ae));
		return result.get((int)(Math.random() * result.size()));
	}
	
	public Action getActionFromOntologyTree(OntologyTree t, ActionEnum type){
		Action a = null;
		ArrayList<ONode> stack = new ArrayList<ONode>();
		ArrayList<Info> iArray = new ArrayList<Info>();
		
		if (t.rootNode.isExistData()){
			stack.add(t.rootNode);
			while(stack.size()>0){
				ONode it = stack.get(0);
				stack.remove(0);
				
				if (it.isExistData()){
					iArray.add(it.dataInfo);
					
					if (it.isItemONode()){
						ItemONode    io   = (ItemONode)it;
						SubtypeONode temp = io.child;
						for(ONode child : temp.childArrayList){
							if (child.isItemONode()){
								stack.add(child);
							}else if (child.isAtomONode()){
								stack.add(child);
							}
						}
					}
				}
			}
			return selectRandomAction(type, iArray);
		}
		
		return a;
	}
}
