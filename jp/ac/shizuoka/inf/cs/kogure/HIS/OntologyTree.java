package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * オントロージを保存するクラス
 * @author kogure
 *
 */
public class OntologyTree implements Cloneable {
	/** OntologyBase 情報 */
	OntologyBase base;
	/** ルートノード */
	ItemONode rootNode;
	/** itemONode の情報を保持する ArrayList */
	ArrayList<ONode>      itemONodeArrayList;
	/** itemONode の情報を保持する HashMap */
	HashMap<String,ONode> itemONodeHashMap;
	/** SubtypeONode の情報を保持する ArrayList */
	ArrayList<SubtypeONode>      subtypeONodeArrayList;
	/** SubtypeONode の情報を保持する HashMap */
	HashMap<String,SubtypeONode> subtypeONodeHashMap;
	/** atomONode の情報を保持する ArrayList */
	ArrayList<ONode>      atomONodeArrayList;
	/** atomONode の情報を保持する  HashMap */
	HashMap<String,ONode> atomONodeHashMap;
	/** itemONode と atomONode の情報を保持する ArrayList */
	ArrayList<ONode>      oNodeArrayList;
	/** itemONode と atomONode の情報を保持する  HashMap */
	HashMap<String,ONode> oNodeHashMap;
	
	static final int MATCH        = 0;
	static final int DENY_MATCH   = 1;
	static final int NO_MATCH     = 2;
	static final int OTHER_MAATCH = 3;

	OntologyTree(OntologyBase b){
		itemONodeArrayList    = new ArrayList<ONode>();
		itemONodeHashMap      = new HashMap<String,ONode>();
		subtypeONodeArrayList = new ArrayList<SubtypeONode>();
		subtypeONodeHashMap   = new HashMap<String,SubtypeONode>();
		atomONodeArrayList    = new ArrayList<ONode>();
		atomONodeHashMap      = new HashMap<String,ONode>();
		oNodeArrayList        = new ArrayList<ONode>();
		oNodeHashMap          = new HashMap<String,ONode>();
		base = b;
	}
	
	OntologyTree(OntologyBase b, Action a){
		base = b;
		initialize(a);
	}
	/**
	 * 
	 * @param a 初期化の際に用いるアクション
	 */
	public void initialize(Action a){
		ONode current = new ItemONode(base.getRootNode());
		rootNode = (ItemONode)current;

		Stack<ONode> stack = new Stack<ONode>();
		setItem((ItemONode)current);
		stack.push(current);
		
		HashMap<ItemInfo,SubtypeInfo> itemList = new HashMap<ItemInfo,SubtypeInfo>();
		HashMap<AtomInfo,DataInfo> atomList = new HashMap<AtomInfo,DataInfo>();
		Info actionItem;
		for (int i = 0; i < a.itemArrayList.size(); i++){
			actionItem = a.itemArrayList.get(i);
			if (actionItem.isSubtypeInfo()){
				SubtypeInfo st = (SubtypeInfo)actionItem;
				ItemInfo    it = (ItemInfo)st.getParent();
				itemList.put(it, st);
			}else if (actionItem.isDataInfo()){
				DataInfo da = (DataInfo)actionItem;
				AtomInfo at = (AtomInfo)da.getParent();
				atomList.put(at, da);
			}
		}
		

		while(!stack.isEmpty()) {
			current = stack.pop();
			if (current.isItemONode()){
				ItemONode newit = (ItemONode)current;
				ItemInfo it = (ItemInfo)current.baseInfo;
				if (itemList.containsKey(it)){
					SubtypeInfo st = itemList.get(it);
					SubtypeONode newst = new SubtypeONode(st);
					Info tempit;
					for (int i = 0; i < st.getChildArrayList().size(); i++){
						tempit = st.getChildArrayList().get(i);
						if (tempit.isItemInfo()){
							ItemONode itembuf = new ItemONode(tempit);
							setItem(itembuf);
							newst.setChild(itembuf);
							stack.push(itembuf);
						}else if (tempit.isAtomInfo()){
							AtomONode atombuf = new AtomONode(tempit);
							setAtom(atombuf);
							newst.setChild(atombuf);
							stack.push(atombuf);
						}
					}
					newit.setChild(newst);
		    	}
		    } else if (current.isAtomONode()){
		    	AtomONode newat = (AtomONode)current;
		    	AtomInfo at = (AtomInfo)current.baseInfo;
		    	if (atomList.containsKey(at)){
		    		DataInfo da = atomList.get(at);
		    		newat.setData(da);
		    	}
		    }
		}
	}
	
	@Override
	public Object clone() {	//throwsを無くす
		try {
			OntologyTree clone = (OntologyTree)super.clone();
			clone.itemONodeArrayList    = new ArrayList<ONode>();
			clone.itemONodeHashMap      = new HashMap<String,ONode>();
			clone.subtypeONodeArrayList = new ArrayList<SubtypeONode>();
			clone.subtypeONodeHashMap   = new HashMap<String,SubtypeONode>();
			clone.oNodeArrayList        = new ArrayList<ONode>();
			clone.oNodeHashMap          = new HashMap<String,ONode>();
			clone.atomONodeArrayList    = new ArrayList<ONode>();
			clone.atomONodeHashMap      = new HashMap<String,ONode>();
			
			clone.rootNode = (ItemONode)cloneNode(clone, this.rootNode, null);
			clone.setItem(clone.rootNode);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	private ONode cloneNode(OntologyTree newot, ONode cn, SubtypeONode pn){
		if (cn.isItemONode()){
			ItemONode in = (ItemONode)cn;
			ItemONode newNode = new ItemONode(pn, cn.baseInfo);
			if (in.child != null){
				SubtypeONode sn = new SubtypeONode(newNode, in.child.baseInfo);
				for(int i = 0; i < in.child.childArrayList.size(); i++){
					ONode ton = in.child.childArrayList.get(i);
					if (ton.isItemONode()){
						ItemONode newItemNode = (ItemONode)cloneNode(newot, ton, sn);
						sn.setChild(newItemNode);
						newot.setItem(newItemNode);
					}else if (ton.isAtomONode()){
						AtomONode newItemNode = (AtomONode)cloneNode(newot, ton, sn);
						sn.setChild(newItemNode);
						newot.setAtom(newItemNode);
					}
				}
				newNode.setChild(sn);
				newot.setSubtype(sn);
			}else if (in.isExistDenyData()){
				for(Info iTemp: in.denyArrayList){
					newNode.setDenyData(iTemp);
				}
			}
			return newNode;
		}else if (cn.isAtomONode()){
			AtomONode at = (AtomONode)cn;
			AtomONode newNode = new AtomONode(pn, cn.baseInfo);
			newNode.setData(at.dataInfo);
			newot.setAtom(newNode);
			if (at.isExistDenyData()){
				for(Info iTemp: at.denyArrayList){
					newNode.setDenyData(iTemp);
				}
			}
			return newNode;
		}else {
			System.err.println("DONT clone: " + cn);
			return null;
		}
	}
	
	/**
	 * オントロジと ユーザゴールの &lt;ItemInfo, SubtypeInfo&gt; もしくは &lt;AtomInfo, DataInfo&gt; を比較する
	 * @param goalOntology 比較するユーザゴール
	 * @param matchItemArrayList ユーザゴールと完全一致する Item,Subtype or Atom,Data のリスト 
	 * @param notExistItemArrayList ユーザゴールにあるのに，このオントロジに無い Item, Atom のリスト 
	 * @param differentDataArrayList Item or Atom は一致するが対応する Subtype or Data が一致しないリスト 
	 * @param dataEmptyItemArrayList ユーザゴールにある Item or Atom の中で，対応する Subtye or Data が空のリスト
	 * @param onlyExistItemArrayList ユーザゴルに存在しない Item, Atom のリスト 
	 */
	public void setInconsistencyItemList(OntologyTree goal,
			ArrayList<ONode> matchItemArrayList,
			ArrayList<ONode> notExistItemArrayList,
			ArrayList<ONode> differentDataArrayList,
			ArrayList<ONode> dataEmptyItemArrayList,
			ArrayList<ONode> onlyExistItemArrayList){
		for (int i = 0; i < goal.oNodeArrayList.size(); i++){
			ONode goalMatchIt = goal.oNodeArrayList.get(i);
			if (oNodeHashMap.containsKey(goalMatchIt.getName())){
				ONode thisMatchIt =oNodeHashMap.get(goalMatchIt.getName()); 
				if (thisMatchIt.isExistData()){
					if (thisMatchIt.dataInfo.getName().equals(goalMatchIt.dataInfo.getName())){
						matchItemArrayList.add(thisMatchIt);
					}else{
						differentDataArrayList.add(goalMatchIt);
					}
				}else {
					dataEmptyItemArrayList.add(goalMatchIt);
				}
			}else {
				notExistItemArrayList.add(goalMatchIt);
			}
		}
		
		for (int i = 0; i < oNodeArrayList.size(); i++){
			ONode thisMatchIt = oNodeArrayList.get(i);
			if (! goal.oNodeHashMap.containsKey(thisMatchIt.getName())){
				onlyExistItemArrayList.add(thisMatchIt);
			}
		}
		int a = 0;
	}
	
	public void checkMatchingUserAction(Action a, int[] number){
		for (Info iv : a.itemArrayList){
			checkMatchingUserActionSub(iv, number);
		}
	}
	
	public void checkMatchingUserActionSub(Info info, int[] number){
		if (info.isSubtypeInfo()){
			SubtypeInfo subtypeTemp = (SubtypeInfo)info;
			ItemInfo    itemTemp    = subtypeTemp.getParent();
			if (! itemONodeHashMap.containsKey(itemTemp.getName())){
				number[OntologyTree.NO_MATCH]++; return;
			}
			ONode temp = itemONodeHashMap.get(itemTemp.getName());
			if (temp.isExistData() ){
				if (temp.isExistData(subtypeTemp)){
					number[OntologyTree.MATCH]++; return;
				}else{
					number[OntologyTree.OTHER_MAATCH]++; return;
				}
			}else if (temp.isExistDenyData(subtypeTemp)){
				number[OntologyTree.DENY_MATCH]++; return;
			}
		}else if (info.isDataInfo()){
			DataInfo dataTemp = (DataInfo)info;
			AtomInfo atomTemp = dataTemp.getParent();
			if (! atomONodeHashMap.containsKey(atomTemp.getName())){
				number[OntologyTree.NO_MATCH]++; return;
			}
			ONode temp = atomONodeHashMap.get(atomTemp.getName());
			if (temp.isExistData() ){
				if (temp.isExistData(dataTemp)){
					number[OntologyTree.MATCH]++; return;
				}else{
					number[OntologyTree.OTHER_MAATCH]++; return;
				}
			}else if (temp.isExistDenyData(dataTemp)){
				number[OntologyTree.DENY_MATCH]++; return;
			}
		}
	}
	
	public void setItem(ItemONode n){
		itemONodeArrayList.add(n);
		itemONodeHashMap.put(n.getName(), n);
		oNodeArrayList.add(n);
		oNodeHashMap.put(n.getName(), n);
	}
	public void setAtom(AtomONode n){
		atomONodeArrayList.add(n);
		atomONodeHashMap.put(n.getName(), n);
		oNodeArrayList.add(n);
		oNodeHashMap.put(n.getName(), n);
	}
	public void setSubtype(SubtypeONode n){
		subtypeONodeArrayList.add(n);
		subtypeONodeHashMap.put(n.baseInfo.getName(), n);
	}
	
	public String toString(){
		String str = "";
		str = "OntologyTree\n";
		str = str + rootNode.toString();
		return str;
	}
}
