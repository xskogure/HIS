package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * @author kogure
 * @param char     type   SubtypeInfo ��, ItemInfo ��, AtomInfo �����i�[����� 
 * @param String   name   ���� Info �̖��O�CI:�ړI�n�^�C�v�Ȃǂ�������Ƃ��ē���
 * @param short    number ���� Info �̉��Ɋi�[����Ă���S�[���̌� (P:���{�ݖ� �Ȃǂ̉��̎��ۂ̃f�[�^)
 * @param HashMap<String,Info>  child List  ���� Info �̉��ɓ����Ă���qInfo �̃��X�g���n�b�V���ŕۑ�
 * 
 */
public class Info {
	private char      type;
	private String    name;
	private int       number = -1;
	private HashMap<String,Info> childHashMap;
	private ArrayList<Info>      childArrayList;
	
	static final char ITEM    = 'I';
	static final char SUBTYPE = 'S';
	static final char ATOM    = 'A';
	static final char DATA    = 'D';
	
	Info(char c, String n){
		type = c;
		name = n;
		childHashMap   = new HashMap<String,Info>();
		childArrayList = new ArrayList<Info>();
	}
	
	public int getNumber(){
		return number;
	}
	
	public void setNumber(int num){
		number = num;
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String str) {
		name = str;
	}
	
	void setChild(Info info){
		childArrayList.add(info);
		childHashMap.put(info.name, info);
	}
	
	Info getChild(String n){
		return childHashMap.get(n);
	}
	
	Info deleteChild(String n){
		int index = -1;
		Info temp = null;
		if (childHashMap.containsKey(n)){
			temp = childHashMap.remove(n);
		}
		index = childArrayList.indexOf(temp);
		if (index != -1){
			childArrayList.remove(index);
		}
		return temp;
	}
	
	HashMap<String, Info> getChildHashMap(){
		return childHashMap;
	}
	
	ArrayList<Info> getChildArrayList(){
		return childArrayList;
	}
	
	int getNumberOfPattern(){
		return number;
	}
	
	char getType(){
		return type;
	}
	
	public String toString(){
		return toString_INFO(0);
	}
	
	public String toString_INFO(int level){
		if (isItemInfo()   ) {ItemInfo    temp = (ItemInfo)this;    return temp.toString_I(level); }
		if (isSubtypeInfo()) {SubtypeInfo temp = (SubtypeInfo)this; return temp.toString_S(level); }
		if (isAtomInfo()   ) {AtomInfo    temp = (AtomInfo)this;    return temp.toString_A(level); }
		if (isDataInfo()   ) {DataInfo    temp = (DataInfo)this;    return temp.getName(); }
		return "";
	}
	
	
	boolean isSubtypeInfo(){
		if (type == SUBTYPE ) return true; else return false;
	}
	
	boolean isItemInfo(){
		if (type == ITEM ) return true; else return false;
	}
	
	boolean isAtomInfo(){
		if (type == ATOM ) return true; else return false;
	}
	
	boolean isDataInfo(){
		if (type == DATA ) return true; else return false;
	}
}
