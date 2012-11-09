package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class ItemONode extends ONode {
	/** q‚Ì SubtypeONode ‚ğ¦‚· */
	SubtypeONode child;
	
	ItemONode(SubtypeONode p, Info b, SubtypeONode c){
		super(p, b);
		child = c;
	}
	
	ItemONode(SubtypeONode p, Info b){
		super(p, b);
	}
	
	ItemONode(Info b){
		super(b);
	}
	
	public String toString(){
		return toString_I(0);
	}
	
	/**
	 * @return ‚»‚Ìƒm[ƒh‚É‚Ô‚ç‰º‚ª‚Á‚Ä‚¢‚é{İ”‚ğŒvZ‚·‚é
	 * @see #getNumberOfPattern
	 */
	public int getNumberOfPattern(){
		return getNumberOfPattern(false);
	}

	public void setChild(SubtypeONode s){
		child = s;
		dataInfo = s.baseInfo;
	}
	
	/**
	 * @param force ÄŒvZ‚·‚éê‡‚Í true , •Û‘¶‚³‚ê‚Ä‚¢‚éŒÂ”‚ğ•Ô‚·”{‚Í false
	 * @return ‚»‚Ìƒm[ƒh‚É‚Ô‚ç‰º‚ª‚Á‚Ä‚¢‚é{İ”
	 */
	public int getNumberOfPattern(boolean force){
		if (number == -1 || force){
			if (child != null) return child.getNumberOfPattern(true);
			if (child == null && isExistDenyData()) {
				ItemInfo tit = (ItemInfo)baseInfo;
				int t = tit.getNumberOfPattern(true);
				SubtypeInfo tst;
				for (int i = 0; i < denyArrayList.size(); i++){
					tst = (SubtypeInfo)denyArrayList.get(i);
					t = t - tst.getNumberOfPattern(true);
				}
				return t;
			}else{
				ItemInfo tit = (ItemInfo)baseInfo;
				return tit.getNumberOfPattern(true);
			}
		}else{
			return number;
		}
	}

	public String toString_I(int level){
		String str = "";
		for(int i=0; i < level * 2; i++) str = str + " ";
		str = str + "ItemONode[" + getName() + "](" + getNumberOfPattern(true) + ") ";
		if (child != null) { 
			str = str + "{\n";
			SubtypeONode temp = child;
			str = str + temp.toString_S(level + 1);
			for(int i=0; i < level * 2; i++) str = str + " ";
			str = str + "}";
		}else if (denyArrayList.size()>0){
			str = str + "{\n";
			Info info;
			for(int j=0; j < denyArrayList.size(); j++){
				for(int i=0; i < (level+1) * 2; i++) str = str + " ";
				info = denyArrayList.get(j);
				str = str + "SubtypeONode[! " + info.getName() + "](" + info.getNumberOfPattern() + ")\n";
			}
			for(int i=0; i < level * 2; i++) str = str + " ";
			str = str + "}";
		}
		str = str + "\n";
		return str;
	}
	
	public String toString_I_simple(){
		String str = "";
		str = str + getName() + "(" + getNumberOfPattern(true) + ") ";
		if (child != null) { 
			str = str + "{";
			SubtypeONode temp = child;
			str = str + temp.toString_S_simple();
			str = str + "}";
		}else if (denyArrayList.size()>0){
			str = str + "{";
			Info info;
			for(int j=0; j < denyArrayList.size(); j++){
				if (j > 0) str = str + ",";
				info = denyArrayList.get(j);
				str = str + "! " + info.getName() + "(" + info.getNumberOfPattern() + ")";
			}
			str = str + "}";
		}
		return str;
	}
}
