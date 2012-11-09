package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;

public class SubtypeONode {
	ItemONode parent;
	int number;
	SubtypeInfo baseInfo;
	ArrayList<ONode> childArrayList;
	HashMap<String,ONode> childHashMap;
	
	SubtypeONode (SubtypeInfo b){
		parent = null;
		baseInfo = b;
		childArrayList = new ArrayList<ONode>();
		childHashMap   = new HashMap<String,ONode>();
	}
	
	SubtypeONode (ItemONode p, SubtypeInfo b){
		parent = p;
		baseInfo = b;
		childArrayList = new ArrayList<ONode>();
		childHashMap   = new HashMap<String,ONode>();
	}
	
	public void setChild(ONode on){
		childArrayList.add(on);
		childHashMap.put(on.getName(), on);
	}
	
	public String toString(){
		return toString_S(0);
	}
	
	public int getNumberOfPattern() {
		return getNumberOfPattern(false);
	}
	
	public int getNumberOfPattern(boolean force) {
		int total = 1;
		if (number == -1 || force == true){
			ONode od;
			for(int i = 0; i < childArrayList.size(); i++){
				od = childArrayList.get(i); 
				if (od.isItemONode()){
					ItemONode temp = (ItemONode)od;
					total = total * temp.getNumberOfPattern(force);
				}else if (od.isAtomONode()){
					AtomONode temp = (AtomONode)od;
					total = total * temp.getNumberOfPattern(force);
				}
			}
			number = total;
			return total;
		}else {
			return number;
		}
		
	}
	
	public String toString_S(int level){
		String str = "";
		String patternLabel;
		for(int i = 0; i < level*2; i++) str = str + " ";
	    patternLabel = "(" + getNumberOfPattern(true) + ")";
	    str = str + "SubtypeONode[" + baseInfo.getName() + "]" + patternLabel + "{\n";
	    ONode od;
	    for(int i = 0; i<childArrayList.size(); i++){
	    	od = childArrayList.get(i);
	    	if (od.isItemONode()) {
	    		ItemONode temp = (ItemONode)od;
	    		str = str + temp.toString_I(level+1);
	    	}else if (od.isAtomONode()) {
	    		AtomONode temp = (AtomONode)od;
	    		str = str + temp.toString_A(level+1);
	    	}
	    }
	    for(int i = 0; i < level*2; i++) str = str + " ";
	    str = str + "}\n";
	    return str;
	}

	public String toString_S_simple(){
		String str = "";
		String patternLabel;
	    patternLabel = "(" + getNumberOfPattern(true) + ")";
	    str = str + baseInfo.getName() + patternLabel + "{";
	    ONode od;
	    for(int i = 0; i<childArrayList.size(); i++){
	    	od = childArrayList.get(i);
	    	if (od.isItemONode()) {
	    		ItemONode temp = (ItemONode)od;
	    		str = str + temp.toString_I_simple();
	    	}else if (od.isAtomONode()) {
	    		AtomONode temp = (AtomONode)od;
	    		str = str + temp.toString_A_simple();
	    	}
	    }
	    str = str + "}";
	    return str;
	}
}
