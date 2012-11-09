package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.HashMap;
import java.util.Iterator;

public class DialogueHistory implements Cloneable {
	HashMap<Info,DHenum> infoHashMap;
	
	DialogueHistory(){
		infoHashMap = new HashMap<Info,DHenum>();
	}
	
	public void update (Action lastSystemAction, Action userAction){
		if (userAction.type == ActionEnum.REQUEST){
			Info temp = userAction.targetItem;
			if (temp.isItemInfo()){
				infoHashMap.put(temp, DHenum.UREQ);
			}else if (temp.isAtomInfo()){
				infoHashMap.put(temp, DHenum.UREQ);
			}
		}
		if (lastSystemAction.type == ActionEnum.INFORM){
			for(int i = 0; i < lastSystemAction.itemArrayList.size(); i++){
				Info temp = lastSystemAction.itemArrayList.get(i);
				if (temp.isDataInfo()){
					DataInfo value = (DataInfo)temp;
					AtomInfo item  = value.getParent();
					infoHashMap.put(item, DHenum.SINF);
				}else if (temp.isSubtypeInfo()){
					SubtypeInfo value = (SubtypeInfo)temp;
					ItemInfo item     = value.getParent();
					infoHashMap.put(item, DHenum.SINF);
				}
			}
		}
		if (userAction.type == ActionEnum.INFORM){
			for(int i = 0; i < userAction.itemArrayList.size(); i++){
				Info temp = userAction.itemArrayList.get(i);
				if (temp.isDataInfo()){
					DataInfo value = (DataInfo)temp;
					AtomInfo item  = value.getParent();
					infoHashMap.put(item, DHenum.UINF);
				}else if (temp.isSubtypeInfo()){
					SubtypeInfo value = (SubtypeInfo)temp;
					ItemInfo item     = value.getParent();
					infoHashMap.put(item, DHenum.UINF);
				}
			}
		}
		if (lastSystemAction.type == ActionEnum.CONFIRM){
			for(int i = 0; i < lastSystemAction.itemArrayList.size(); i++){
				Info temp = lastSystemAction.itemArrayList.get(i);
				if (temp.isDataInfo()){
					DataInfo value = (DataInfo)temp;
					AtomInfo item  = value.getParent();
					infoHashMap.put(item, DHenum.SQRY);
				}else if (temp.isSubtypeInfo()){
					SubtypeInfo value = (SubtypeInfo)temp;
					ItemInfo item     = value.getParent();
					infoHashMap.put(item, DHenum.SQRY);
				}
			}
		}
		if (userAction.type == ActionEnum.DENY){
			Info temp = userAction.itemArrayList.get(0);
			if (temp.isItemInfo()){
				infoHashMap.put(temp, DHenum.DENY);
			}else if (temp.isAtomInfo()){
				infoHashMap.put(temp, DHenum.DENY);
			}
		}
		if (userAction.type == ActionEnum.AFFIRM){
			for(int i = 0; i < userAction.itemArrayList.size(); i++){
				Info temp = userAction.itemArrayList.get(i);
				if (temp.isDataInfo()){
					DataInfo value = (DataInfo)temp;
					AtomInfo item  = value.getParent();
					infoHashMap.put(item, DHenum.GRND);
				}else if (temp.isSubtypeInfo()){
					SubtypeInfo value = (SubtypeInfo)temp;
					ItemInfo item     = value.getParent();
					infoHashMap.put(item, DHenum.GRND);
				}
			}
		}
	}
	
	@Override
	public Object clone() {	//throwsを無くす
		try {
			DialogueHistory cloneDH = (DialogueHistory)super.clone();
			cloneDH.infoHashMap = new HashMap<Info,DHenum>(this.infoHashMap);
			return cloneDH;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	
	public String toString(){
		String str;
		str = "Dialogue History: ";
		Iterator<Info> it = infoHashMap.keySet().iterator();
		Info obj;
		while (it.hasNext()) {	// 次の要素があるならブロック内を実行
			obj = it.next();	// 次の要素を取り出す
			str = str + obj.getName() + ": " + infoHashMap.get(obj) + ", ";
		}
		return str;
	}
}
