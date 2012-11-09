package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;

public class SubtypeInfo extends Info {
	private ItemInfo parent;
	
	SubtypeInfo (String n){
		super(Info.SUBTYPE, n);
		parent = null;
	}
	
	SubtypeInfo (ItemInfo p, String n){
		super(Info.SUBTYPE, n);
		parent = p;
	}
	
	public String toString(){
		return toString_S(0);
	}
	
	public int getNumberOfPattern() {
		return getNumberOfPattern(false);
	}
	
	public int getNumberOfPattern(boolean force) {
		int total = 1;
		ArrayList<Info> list = getChildArrayList();
		if (getNumber() == -1 || force == true){
			for(int i = 0; i < list.size(); i++){
				if (list.get(i).isItemInfo()){
					ItemInfo temp = (ItemInfo)list.get(i);
					total = total * temp.getNumberOfPattern(force);
				}else if (list.get(i).isAtomInfo()){
					AtomInfo temp = (AtomInfo)list.get(i);
					total = total * temp.getNumberOfPattern(force);
				}
			}
			setNumber(total);
			return total;
		}else {
			return getNumber();
		}
		
	}
	
	public ItemInfo getParent(){
		return parent;
	}
	
	public void setParent(ItemInfo p){
		parent = p;
	}
	
	public String toString_S(int level){
		String str = "";
		String patternLabel;
		for(int i = 0; i < level*2; i++) str = str + " ";
	    patternLabel = "(" + getNumberOfPattern(true) + ")";
	    str = str + "SubtypeInfo: [" + getName() + "]" + patternLabel + "{\n";
	    for(int i = 0; i<getChildArrayList().size(); i++){
	    	if (getChildArrayList().get(i).isItemInfo()) {
	    		ItemInfo temp = (ItemInfo)(getChildArrayList().get(i)); 
	    		str = str + temp.toString_I(level+1);
	    	}else if (getChildArrayList().get(i).isAtomInfo()) {
	    		AtomInfo temp = (AtomInfo)(getChildArrayList().get(i));
	    		str = str + temp	.toString_A(level+1);
	    	}
	    	str = str + "\n";
	    }
	    for(int i = 0; i < level*2; i++) str = str + " ";
	    str = str + "}\n";
	    return str;
	}
}
