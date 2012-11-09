package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class AtomONode extends ONode {
	AtomONode(Info b){
		super(b);
	}
	
	AtomONode(SubtypeONode p, Info b){
		super(p, b);
	}
	
	AtomONode(SubtypeONode p, Info b, Info v){
		super(p, b, v);
	}
	
	public String toString(){
		return toString_A(0);
	}
	
	public int getNumberOfPattern(){
		return getNumberOfPattern(false);
	}
	
	public int getNumberOfPattern(boolean force){
		if (number == -1 || force){
			if (dataInfo != null) return 1;
			else {
				if (isExistDenyData()) {
					return baseInfo.getNumberOfPattern()-denyArrayList.size();
				}else{
					return baseInfo.getNumberOfPattern();
				}
			}
		}else{
			return number;
		}
	}
	
	public String toString_A(int level){
		String str  = "";
		for(int i = 0; i < level * 2; i++) str = str + " ";
		str = str + "AtomONode[" + getName() + "](" + getNumberOfPattern(true) + ") ";
		if (dataInfo != null) {
			str = str + dataInfo.getName();
		}else if (denyArrayList.size()>0){
			for (int i = 0; i<denyArrayList.size(); i++){
				str = str + "! " + denyArrayList.get(i).getName() + " ";
			}
		}
		str = str + "\n";
		return str;
	}

	public String toString_A_simple(){
		String str  = "";
		str = str + getName() + "(" + getNumberOfPattern(true) + ") ";
		if (dataInfo != null) {
			str = str + "{" + dataInfo.getName() + "}";
		}else if (denyArrayList.size()>0){
			str = str + "{";
			for (int i = 0; i<denyArrayList.size(); i++){
				if (i > 0) str = str + ",";
				str = str + "!" + denyArrayList.get(i).getName();
			}
			str = str + "}";
		}
		return str;
	}
}
