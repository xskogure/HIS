package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class AtomInfo extends Info{
	private SubtypeInfo parent;

	AtomInfo (SubtypeInfo p, String n){
		super(Info.ATOM, n);
		parent = p;
	}
	
	AtomInfo (String n){
		super(Info.ATOM, n);
		parent = null;
	}
	
	public int getNumberOfPattern() {
		return getNumberOfPattern(false);
	}
	
	public int getNumberOfPattern(boolean force) {
		if (getNumber() == -1 || force == true){
			setNumber(getChildArrayList().size());
		}
		return getNumber();
	}
	
	public SubtypeInfo getParent(){
		return parent;
	}
	
	public void setParent(SubtypeInfo p){
		parent = p;
	}

	public String toString(){
		return toString_A(0);
	}
	
	public String toString_A(int level){
		String str  = "";
		for(int i = 0; i < level * 2; i++) str = str + " ";
		str = str + "Atom[" + getName() + "](" + getNumberOfPattern(true) + ") {\n";
		for(int d = 0; d < getChildArrayList().size(); d++){
			for (int i = 0; i < (level + 1) * 2; i++) str = str + " ";
		    str = str + getChildArrayList().get(d).getName() + "\n";
		}
		for(int i = 0; i < level * 2; i++) str = str + " "; 
		str = str + "}\n";
		return str;
	}
}
