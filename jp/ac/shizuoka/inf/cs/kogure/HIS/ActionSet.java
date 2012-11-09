package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ActionSet {
	OntologyBase base;
	ArrayList<Action>                 actionArrayList;
	HashMap<ActionEnum,ArrayList<Action>> actionArrayListforEachTypeHashMap;
	HashMap<Info,ArrayList<Action>>   actionArrayListforEachInfoHashMap;
	ArrayList<Action> searchResult;
	
	/** 渡したリストが全て存在し，かつそれ以外の Info が存在しない場合 */
	static final char ONLY_MATCH = 'o';
	/** 渡したリストが全て存在する場合 (それ以外の Info が存在してもよい) */
	static final char NORMAL_MATCH = 'n';
	
	ActionSet(){
		base = null;
		actionArrayList                   = new ArrayList<Action>();
		actionArrayListforEachTypeHashMap = new HashMap<ActionEnum,ArrayList<Action>>(); 
		actionArrayListforEachInfoHashMap = new HashMap<Info,ArrayList<Action>>();
		searchResult                      = new ArrayList<Action>(); 
	}
	
	ActionSet(OntologyBase b){
		base = b;
		actionArrayList                   = new ArrayList<Action>();
		actionArrayListforEachTypeHashMap = new HashMap<ActionEnum,ArrayList<Action>>(); 
		actionArrayListforEachInfoHashMap = new HashMap<Info,ArrayList<Action>>();
		searchResult                      = new ArrayList<Action>(); 
	}
	
	public void setAction(Action a){
		actionArrayList.add(a);
		if (actionArrayListforEachTypeHashMap.containsKey(a.type)){
			actionArrayListforEachTypeHashMap.get(a.type).add(a);
		}else{
			actionArrayListforEachTypeHashMap.put(a.type, new ArrayList<Action>());
			actionArrayListforEachTypeHashMap.get(a.type).add(a);
		}
		
		// Action の targetItem を登録する
		if (actionArrayListforEachInfoHashMap.containsKey(a.targetItem)){
			actionArrayListforEachInfoHashMap.get(a.targetItem).add(a);
		}else{
			actionArrayListforEachInfoHashMap.put(a.targetItem, new ArrayList<Action>());
			actionArrayListforEachInfoHashMap.get(a.targetItem).add(a);
		}
		
		// Action の itemList を登録する (SubtypeInfo か DataInfo)
		for (int i = 0; i < a.itemArrayList.size(); i++){
			Info temp = a.itemArrayList.get(i);
			if (actionArrayListforEachInfoHashMap.containsKey(temp)){
				actionArrayListforEachInfoHashMap.get(temp).add(a);
			}else{
				actionArrayListforEachInfoHashMap.put(temp, new ArrayList<Action>());
				actionArrayListforEachInfoHashMap.get(temp).add(a);
			}
		}
	}
	
	public void setActionToLast(Action a){
		actionArrayList.add(a);
		if (actionArrayListforEachTypeHashMap.containsKey(a.type)){
			actionArrayListforEachInfoHashMap.get(a.type).add(a);
		}else{
			actionArrayListforEachTypeHashMap.put(a.type, new ArrayList<Action>());
			actionArrayListforEachTypeHashMap.get(a.type).add(a);
		}
	}
	
	public void initialize(OntologyBase base){
		initializeType1(base, ActionEnum.INFORM);
	}
	
	public void initializeType1(OntologyBase base, ActionEnum type){ 
		Action ca = new Action(type);
		ca.setItem(base.getRootNode());

		ArrayList<Action> stack  = new ArrayList<Action>();
		stack.add(ca);

		int[][][] combinfo ={
				{{}},
				{{}, {1}},
				{{}, {1}, {2}, {1,2}},
				{{}, {1}, {2}, {1,2},{3}, {1,3}, {2,3}, {1,2,3}},
				{{}, {1}, {2}, {1,2},{3}, {1,3}, {2,3}, {1,2,3}, {4}, {1,4}, {2,4}, {1,2,4},{3,4}, {1,3,4}, {2,3,4}, {1,2,3,4}},
				{{}, {1}, {2}, {1,2},{3}, {1,3}, {2,3}, {1,2,3}, {4}, {1,4}, {2,4}, {1,2,4},{3,4}, {1,3,4}, {2,3,4}, {1,2,3,4},
                 {5}, {1,5}, {2,5}, {1,2,5},{3,5}, {1,3,5}, {2,3,5}, {1,2,3,5}, {4,5}, {1,4,5}, {2,4,5}, {1,2,4,5},{3,4,5}, {1,3,4,5}, {2,3,4,5}, {1,2,3,4,5}}
		};
				
		while(stack.size()>0){
			ca = stack.get(0);
			stack.remove(0);
			int flag = 1;
			Info iv;
			for (int i = 0; i < ca.itemArrayList.size(); i++){
				iv = ca.itemArrayList.get(i);
				if (iv.isItemInfo() || iv.isAtomInfo()){
					if (iv.isItemInfo()){
						SubtypeInfo st;
						for (int j = 0; j < iv.getChildArrayList().size(); j++){
							st = (SubtypeInfo)iv.getChildArrayList().get(j);
							Action tua = (Action)ca.clone();
							tua.deleteItem(iv);
							tua.setItem(st);
							int[][] ln = combinfo[st.getChildArrayList().size()];
							int[] ii;
							for(int k=0; k < ln.length; k++){
								ii = ln[k];
								Action ttua = (Action)tua.clone();
								ttua.deleteItem(iv);
								
								for (int l = 0; l < ii.length; l++){
									ttua.setItem(st.getChildArrayList().get(ii[l]-1));
								}
								stack.add(ttua);
							}
						}
					} else if (iv.isAtomInfo()){
						DataInfo da;
						for (int j = 0; j < iv.getChildArrayList().size(); j++){
							da = (DataInfo)iv.getChildArrayList().get(j);
							Action tua = (Action)ca.clone();
							tua.deleteItem(iv);
							tua.setItem(da);
							stack.add(tua);
						}
					}
					flag = 0;
				}
			}
			if (flag == 1){
				setAction(ca);
			}
		}
	}
	
	public void initializeType2(OntologyBase base, ActionEnum type) {
		Action oua = new Action(type);

		ItemInfo it;
		for(int i = 0; i < base.itemInfoArrayList.size(); i++){
			it = base.itemInfoArrayList.get(i);
			SubtypeInfo st;
			for(int j = 0; j < it.getChildArrayList().size(); j++){
				st = (SubtypeInfo)it.getChildArrayList().get(j);
				Action cua = (Action)oua.clone();
				cua.setItem(st);
				setAction(cua);
			}
		}
		AtomInfo at;
		for (int i = 0; i < base.atomInfoArrayList.size(); i++){
			at = base.atomInfoArrayList.get(i);
			DataInfo di;
			for (int j = 0; j < at.getChildArrayList().size(); j++){
				di = (DataInfo)at.getChildArrayList().get(j);
				Action cua = (Action)oua.clone();
				cua.setItem(di);
				setAction(cua);
			}
		}
	}


	public void initializeType3(OntologyBase base, ActionEnum type) {
		Action ca = new Action(type);
		ca.setItem(base.getRootNode());
		ArrayList<Action> stack = new ArrayList<Action>();
		stack.add(ca);

		while(stack.size()>0){
			ca = stack.get(0);
			stack.remove(0);
			int flag = 1;
			Info iv;
		    for(int i = 0; i < ca.itemArrayList.size(); i++){
		    	iv = ca.itemArrayList.get(i);
		    	if (iv.isItemInfo() || iv.isAtomInfo()){
		    		if (iv.isItemInfo()){
		    			Action tua = (Action)ca.clone();
		    			tua.deleteItem(iv);
		    			tua.targetItem = iv;
		    			stack.add(tua);
		    			SubtypeInfo st;
		    			for (int j = 0; j < iv.getChildArrayList().size(); j++){
		    				st = (SubtypeInfo)iv.getChildArrayList().get(j);
		    				Action ttua = (Action)ca.clone();
		    				Info it;
		    				for (int k = 0; k < st.getChildArrayList().size(); k++){
		    					it = st.getChildArrayList().get(k);
		    					Action tttua = (Action)ttua.clone();
			    				tttua.deleteItem(iv);
			    				tttua.setItem(st);
			    				tttua.setItem(it);
			    				stack.add(tttua);
		    				}
		    			}
		    		} else if (iv.isAtomInfo()){
		    			ca.deleteItem(iv);
		    			ca.targetItem = iv;
		    			stack.add(ca);
		    		}
		    		flag = 0;
		    	}
		    }
		    if (flag == 1) {
		    	setAction(ca);
		    }
		}
	}
	/**
	 * 
	 * @param t 制約条件：ActionEnum
	 * @return ActionEnum t のアクションリストを取り出して先頭の要素を返す
	 */
	public Action searchAction(ActionEnum t){
		ArrayList<Action> temp = new ArrayList<Action>();
		if (actionArrayListforEachTypeHashMap.containsKey(t)){
			temp = actionArrayListforEachTypeHashMap.get(t);
			if (temp.size()>0) {
				return temp.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param t     制約条件：ActionEnum
	 * @param kList 制約条件：Info の ArrayList
	 * @return ActionEnum と Info ArrayList の製薬でアクションリストを検索し，その結果の先頭の要素を返す
	 */
	public Action searchAction(ActionEnum t, ArrayList<Info> kList){
		ArrayList<Action> temp;
		temp = searchActionList(t, kList, ActionSet.ONLY_MATCH);
		if (temp.size()>0){
			return temp.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @param t 制約条件：ActionEnum
	 * @return ActionEnum t のアクションリストからランダムにひとつ選んで返す
	 */
	public Action selectRandomAction(ActionEnum t){
		ArrayList<Action> temp;
		if (actionArrayListforEachTypeHashMap.containsKey(t)){
			temp = actionArrayListforEachTypeHashMap.get(t);
			if (temp.size()>0){
				return temp.get((int)(Math.random()*temp.size()));
			}else{
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param exclusiveAction 以外の指定
	 * @return exclusiveAction でしてされた以外のアクションセットから一つランダムで選んで返す
	 */
	public Action selectRandomAction(Action exclusiveAction){
		ArrayList<Action> temp = new ArrayList<Action>();
		temp.addAll(actionArrayList);
		if (temp.size()>0){
			if (temp.contains(exclusiveAction)){
				temp.remove(exclusiveAction);
			}
			return temp.get((int)(Math.random()*temp.size()));
		}else{
			return null;
		}
	}

	/**
	 * 
	 * @param exclusiveAction 以外の指定
	 * @return exclusiveAction でしてされた以外のアクションセットから一つランダムで選んで返す
	 */
	public ArrayList<Action> searchRandomAction(Action exclusiveAction){
		ArrayList<Action> temp = new ArrayList<Action>();
		temp.addAll(actionArrayList);
		if (temp.size()>0){
			if (temp.contains(exclusiveAction)){
				temp.remove(exclusiveAction);
			}
			Collections.shuffle(temp);
			return temp;
		}else{
			return null;
		}
	}

	/**
	 * 
	 * @param t               制約条件：ActionEnum
	 * @param exclusiveAction 以外の指定
	 * @return ActionEnum で検索した結果からランダムで一つ選ぶ (exclusiveAction 以外で)
	 */
	public Action selectRandomAction(ActionEnum t, Action exclusiveAction){
		ArrayList<Action> temp = new ArrayList<Action>();
		if (actionArrayListforEachTypeHashMap.containsKey(t)){
			temp.addAll(actionArrayListforEachTypeHashMap.get(t));
			if (temp.size()>0){
				if (temp.contains(exclusiveAction)){
					temp.remove(exclusiveAction);
				}
				return temp.get((int)(Math.random()*temp.size()));
			}else{
				return null;
			}
		}
		return null;
	}
	
	public Action selectRandomAction(ActionEnum t, ArrayList<Info> kList){
		ArrayList<Action> temp;
		temp = searchActionList(t, kList, ActionSet.ONLY_MATCH);
		if (temp.size()>0){
			return temp.get((int)(Math.random()*temp.size()));
		}else{
			return null;
		}
	}
	
	public Action selectRandomAction(ActionEnum t, ArrayList<Info> kList, Action exclusiveAction){
		ArrayList<Action> temp;
		temp = searchActionList(t, kList, ActionSet.ONLY_MATCH);
		if (temp.size()>0){
			if (temp.contains(exclusiveAction)){
				temp.remove(exclusiveAction);
			}
			return temp.get((int)(Math.random()*temp.size()));
		}else{
			return null;
		}
	}
	
	public ArrayList<Action> searchRandomActionList(int num, ActionEnum t, ArrayList<Info> kList, char searchType){
		ArrayList<Action> aList = new ArrayList<Action>();
		aList = new ArrayList<Action>(searchActionList(t, kList, searchType));
		Collections.shuffle(aList);
		while(aList.size() > num){
			aList.remove(aList.size()-1);
		}
		return aList;
	}
	
	public ArrayList<Action> searchActionList(ActionEnum t, ArrayList<Info> kList, char searchType){
		searchResult.clear();
		int minKey = -1;
		boolean isFirst = true;

		// 渡された Info リストのうち，その Info が入っているアクションの数が一番小さい Info を選ぶ
		for(int i = 0; i < kList.size(); i++){
			if (actionArrayListforEachInfoHashMap.containsKey(kList.get(i))){
				if (isFirst) {
					minKey = i; isFirst = false;
				}else{
					if (actionArrayListforEachInfoHashMap.get(kList.get(minKey)).size() >
						actionArrayListforEachInfoHashMap.get(kList.get(i)).size()){
						minKey = i;
					}
				}
			}
		}
		// 一つも存在しない場合は空で返す
		if (minKey == -1) {
			if (kList.size() == 0){
				if (t == ActionEnum.HELLO || t == ActionEnum.BYE){
					searchResult.addAll(actionArrayListforEachTypeHashMap.get(t));
				}
			}
			return searchResult;
		}
		
		ArrayList<Action> firstList = actionArrayListforEachInfoHashMap.get(kList.get(minKey)); 
		for(int i = 0; i < firstList.size(); i++){
			Action tempAction = firstList.get(i);
			int actionItemNum = 0;
			if (tempAction.targetItem != null){
				actionItemNum = 1;
			}
			actionItemNum = actionItemNum + tempAction.itemArrayList.size();
			boolean allFound = true;
			for(int j = 0; j < kList.size(); j++){
				Info tempInfo = kList.get(j);
//				System.err.println("tempAction: " + tempAction + "/ tempInfo: " + tempInfo.getName() );
				if (tempAction.type != t || ! tempAction.itemHashMap.containsKey(tempInfo.getName())){
					if (tempAction.targetItem != null){
						if (! tempAction.targetItem.getName().equals(tempInfo.getName())){
							allFound = false; break;
						}
					}else{
						allFound = false; break;
					}
				}	
			}
			if (allFound){
				if (searchType == ActionSet.ONLY_MATCH) {
					if (actionItemNum == kList.size()){
						searchResult.add(tempAction);
					}
				}else if (searchType == ActionSet.NORMAL_MATCH) {
					searchResult.add(tempAction);
				}
			}
		}
		return searchResult;
	}
}
