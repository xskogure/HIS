package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;

public class ItemInfo extends Info {
	private SubtypeInfo parent; 
	ItemInfo (String n){
		super(Info.ITEM, n);
		parent = null;
	}
	
	ItemInfo (SubtypeInfo p, String n){
		super(Info.ITEM, n);
		parent = p;
	}
	
	public int getNumberOfPattern() {
		return getNumberOfPattern(false);
	}
	
	public int getNumberOfPattern(boolean force) {
		int total = 0;
		ArrayList<Info> list = getChildArrayList();
		if (getNumber() == -1 || force == true){
			for(int i = 0; i < list.size(); i++){
				SubtypeInfo temp = (SubtypeInfo)list.get(i);
				total = total + temp.getNumberOfPattern(force);
			}
			setNumber(total);
			return total;
		}else {
			return getNumber();
		}
	}
	
	public SubtypeInfo getParent(){
		return parent;
	}
	
	public void setParent(SubtypeInfo p){
		parent = p;
	}
	
	public String toString(){
		return toString_I(0);
	}
	
	public String toString_I(int level){
		String str = "";
		for(int i=0; i < level * 2; i++) str = str + " ";
		str = str + "ItemInfo[" + getName() + "](" + getNumberOfPattern(true) + ") {\n";
		for(int s=0; s < getChildArrayList().size(); s++){
			if (getChildArrayList().get(s).isSubtypeInfo()){
				SubtypeInfo temp = (SubtypeInfo)(getChildArrayList().get(s)); 
				str = str + temp.toString_S(level + 1);
			}
		}
		for(int i=0; i < level * 2; i++) str = str + " ";
		str = str + "}\n";
		return str;
	}
}
