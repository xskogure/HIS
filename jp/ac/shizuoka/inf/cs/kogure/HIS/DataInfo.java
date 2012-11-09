package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class DataInfo extends Info {
	private AtomInfo parent;
	
	DataInfo(AtomInfo p, String str){
		super(Info.DATA, str);
		parent = p;
	}
	
	DataInfo(String str){
		super(Info.DATA, str);
		parent = null;
	}
	
	AtomInfo getParent(){
		return parent;
	}
	
	public String toString(){
		return toString_D(0);
	}
	
	public String toString_D(int level){
		String str = "";
		for(int i = 0; i < level*2; i++) str = str + " ";
	    str = str + getName() ;
	    return str;
	}
}
