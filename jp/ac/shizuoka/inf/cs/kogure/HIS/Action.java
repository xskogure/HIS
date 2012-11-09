package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;

public class Action implements Cloneable {
	ActionEnum type;
	Info   targetItem;
	ArrayList<Info>       itemArrayList;
	HashMap<String,Info>  itemHashMap;
	
	Action(ActionEnum t){
		targetItem = null;
		itemArrayList = new ArrayList<Info>();
		itemHashMap   = new HashMap<String,Info>();
		type = t;
	}
	
	public void setItem (Info info){
		itemArrayList.add(info);
		itemHashMap.put(info.getName(), info);
	}
	
	public void deleteItem (Info info){
		itemArrayList.remove(info);
		itemHashMap.remove(info.getName());
	}

	@Override
	public Object clone() {	//throws‚ð–³‚­‚·
		try {
			Action cloneAction = (Action)super.clone();
			cloneAction.itemArrayList = new ArrayList<Info>(this.itemArrayList);
			cloneAction.itemHashMap   = new HashMap<String,Info>(this.itemHashMap);
			
			return cloneAction;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public String toString(){
		String str = "";
		str = type + "(";
		boolean firstFlag = true;
		if (targetItem != null){
			str = str + targetItem.getName();
			firstFlag = false;
		}
		for(int i=0;i<itemArrayList.size();i++){
			Info temp = itemArrayList.get(i);
			if (!firstFlag) str = str + ","; else firstFlag = false;
			if (temp.isSubtypeInfo()){
				SubtypeInfo st = (SubtypeInfo)temp;
				ItemInfo    it = st.getParent();
				str = str + it.getName() + "=" + st.getName(); 
			}else if (temp.isDataInfo()){
				DataInfo da = (DataInfo)temp;
				AtomInfo at = da.getParent();
				str = str + at.getName() + "=" + da.getName(); 
			}else if (temp.isItemInfo()){
				ItemInfo    it = (ItemInfo)temp;
				str = str + it.getName() + "=*";
			}else if (temp.isAtomInfo()){
				AtomInfo at = (AtomInfo)temp;
				str = str + at.getName() + "=*"; 
			}
		}
		
		str = str + ")";
		return str;
	}
}
