package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author kogure
 * 
 */
public class ONode {
	/** ItemInfo �� AtomInfo ���i�[�����iI:??? �� P:??? �Ȃǂ� = �̍��ɗ�����́j */
	Info baseInfo;
	/** SubtypeInfo �� DataInfo ���i�[�����(S:??? �� ��̒l �Ȃǂ� = �̉E���ɗ������) */  
	Info dataInfo;
	/** I �Ȃ� ItemInfo, A �Ȃ� AtomInfo */      
	char type;            
	/** �ے�l�n�b�V���iHashMap�j */
	HashMap<String,Info> denyHashMap;
	/** �ے�l���X�g(ArrayList) */
	ArrayList<Info>      denyArrayList;  
	/** ���̃m�[�h�̉��ɑ��݂�����p�^�[���� */
	int number;
	SubtypeONode parent;
	
	static final char ITEM    = 'I';
	static final char ATOM    = 'A';

	/**
	 * @param b ���̃m�[�h���g�̑Ή����� OntologyBase �� ItemInfo or AtomInfo
	 */
	ONode(Info b){
		parent = null;
		baseInfo  = b;
		dataInfo = null;
		type = b.getType();
		denyHashMap   = new HashMap<String,Info>();
		denyArrayList = new ArrayList<Info>();
	}
	
	/**
	 * @param p ���̃m�[�h�̐e�m�[�h (SubtypeONode)
	 * @param b ���̃m�[�h���g�̑Ή����� OntologyBase �� ItemInfo or AtomInfo
	 */
	ONode(SubtypeONode p, Info b){
		parent = p;
		baseInfo  = b;
		dataInfo = null;
		type = b.getType();
		denyHashMap   = new HashMap<String,Info>();
		denyArrayList = new ArrayList<Info>();
	}
	
	/**
	 * @param p ���̃m�[�h�̐e�m�[�h (SubtypeONode)
	 * @param b ���̃m�[�h���g�̑Ή����� OntologyBase �� ItemInfo or AtomInfo
	 * @param d ���̃m�[�h�̎q���ɑΉ����� OntologyBase ��  SubtypeInfo �� DataInfo
	 */
	ONode(SubtypeONode p, Info b, Info d){
		parent = p;
		baseInfo  = b;
		dataInfo = d;
		type = b.getType();
		denyHashMap   = new HashMap<String,Info>();
		denyArrayList = new ArrayList<Info>();
	}
	
	public int getNumber(){
		return number;
	}

	public void setNumber(int num){
		number = num;
	}
	
	String getName() {
		return baseInfo.getName();
	}

	void setData(Info info){
		dataInfo = info;
	}
	
	void setDenyData(Info info){
		denyArrayList.add(info);
		denyHashMap.put(info.getName(), info);
	}
	
	void deleteDenyData(Info info){
		denyHashMap.remove(info);
		denyArrayList.remove(info);
	}
	
	int getNumberOfPattern(){
		return number;
	}
	
	public String toString(){
		return toString_ONODE(0);
	}
	
	public String toString_simple(){
		if (isItemONode()) {ItemONode temp = (ItemONode)this; return temp.toString_I_simple(); }
		if (isAtomONode()) {AtomONode temp = (AtomONode)this; return temp.toString_A_simple(); }
		return "";
	}
	
	public String toString_ONODE(int level){
		if (isItemONode()) {ItemONode temp = (ItemONode)this; return temp.toString_I(level); }
		if (isAtomONode()) {AtomONode temp = (AtomONode)this; return temp.toString_A(level); }
		return "";
	}

	boolean isExistData(Info i){
		if (dataInfo.getName() == i.getName()) return true; else return false;
	}
	
	boolean isExistData(){
		if (dataInfo != null) return true; else return false;
	}
	
	boolean isExistDenyData(Info i){
		if (denyHashMap.containsKey(i.getName())) return true; else return false;
	}
	
	boolean isExistDenyData(){
		if (denyArrayList.size()>0) return true; else return false;
	}
	
	boolean isItemONode(){
		if (type == ITEM ) return true; else return false;
	}
	
	boolean isAtomONode(){
		if (type == ATOM ) return true; else return false;
	}
}
