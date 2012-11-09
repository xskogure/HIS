package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;

public class UserActionSet extends ActionSet {
	/** ���[�U�S�[���̃��X�g */
	ArrayList<OntologyTree> userGoalArrayList;
	
	/** �S�[���Ɩ������Ȃ��I���g���W�ɂ���ăV�X�e���A�N�V���������ꂽ�ۂ̎��̃��[�U���b�̃^�C�v�ʐ��K�m�� */
	EnumMap<ActionEnum,EnumMap<ActionEnum,Range>> userActionProbEachSystemActionC;
	/** �S�[���Ɩ�������I���g���W�ɂ���ăV�X�e���A�N�V���������ꂽ�ۂ̎��̃��[�U���b�̃^�C�v�ʐ��K�m�� */
	EnumMap<ActionEnum,EnumMap<ActionEnum,Range>> userActionProbEachSystemActionW;


	UserActionSet(OntologyBase b){
		super(b);
		setAction(new Action(ActionEnum.NONE));
		initializeType1(b, ActionEnum.INFORM);
		initializeType1(b, ActionEnum.AFFIRM);
		initializeType2(b, ActionEnum.DENY);
		userGoalArrayList = new ArrayList<OntologyTree>();
		setUserGoal();
		userActionProbEachSystemActionC = new EnumMap<ActionEnum,EnumMap<ActionEnum,Range>>(ActionEnum.class); 
		userActionProbEachSystemActionW = new EnumMap<ActionEnum,EnumMap<ActionEnum,Range>>(ActionEnum.class);
		setUserActionProbEachSystemAction();
	}
	
	public void setUserActionProbEachSystemAction (){
		EnumMap<ActionEnum,Range> temp;

		// �S�[���Ɩ������Ȃ��FSystem Action �� Hello �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0   ));
		userActionProbEachSystemActionC.put(ActionEnum.HELLO, temp);
		
		// �S�[���Ɩ������Ȃ��FSystem Action �� Bye �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.001)); temp.put(ActionEnum.DENY, new Range(0.001, 0.002)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.003, 0.004)); temp.put(ActionEnum.BYE , new Range(0.004, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.BYE, temp);
		
		// �S�[���Ɩ������Ȃ��FSystem Action �� Request �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.REQUEST, temp);
		
		// �S�[���Ɩ������Ȃ��FSystem Action �� INFORM �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.997)); temp.put(ActionEnum.DENY, new Range(0.997, 0.998)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.998, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.INFORM, temp);
		
		// �S�[���Ɩ������Ȃ��FSystem Action �� CONFIRM �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.001)); temp.put(ActionEnum.DENY, new Range(0.001, 0.002)); 
		temp.put(ActionEnum.AFFIRM, new Range(0.002, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionC.put(ActionEnum.CONFIRM, temp);

		
		
		// �S�[���Ɩ�������FSystem Action �� Hello �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.HELLO, temp);
		
		// �S�[���Ɩ�������FSystem Action �� Bye �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.BYE, temp);
		
		// �S�[���Ɩ�������FSystem Action �� Request �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.REQUEST, temp);
		
		// �S�[���Ɩ�������FSystem Action �� INFORM �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.INFORM, temp);
		
		// �S�[���Ɩ�������FSystem Action �� CONFIRM �̍ۂ̎��� User Action �̑I���m�� 
		temp = new EnumMap<ActionEnum,Range>(ActionEnum.class);
		temp.put(ActionEnum.INFORM, new Range(0.0  , 0.499)); temp.put(ActionEnum.AFFIRM, new Range(0.499, 0.500)); 
		temp.put(ActionEnum.DENY, new Range(0.500, 0.999)); temp.put(ActionEnum.BYE , new Range(0.999, 1.0  ));
		userActionProbEachSystemActionW.put(ActionEnum.CONFIRM, temp);
	}
	
	/**
	 * 
	 * @param lsa           �O��̃V�X�e���A�N�V����
	 * @param isConsistency �S�[���Ɩ������Ă��Ȃ���� true, ������ false
	 * @return ���Ɏ��ׂ����[�U�A�N�V�����̃^�C�v
	 */
	public ActionEnum randomSelectUserActionType(Action lsa, boolean isConsistency){
		EnumMap<ActionEnum,Range> temp;
		if (isConsistency) {
			temp = userActionProbEachSystemActionC.get(lsa.type); 
		}else{
			temp = userActionProbEachSystemActionW.get(lsa.type); 
		}
		
		Iterator<ActionEnum> it = temp.keySet().iterator();
		ActionEnum obj = null;
		Range range;
		double r = Math.random();
		
		while (it.hasNext()) {	// ���̗v�f������Ȃ�u���b�N�������s
			obj = it.next();	// ���̗v�f�����o��
			range = temp.get(obj);
			if (range.isContains(r)){
				return obj;
			}
		}
		return obj;
	}
	
	public void setUserGoal(){
		ArrayList<Action> temp = actionArrayListforEachTypeHashMap.get(ActionEnum.INFORM);
		for(int i = 0;i < temp.size(); i++){
			OntologyTree ot = new OntologyTree(base);
			ot.initialize(temp.get(i));
			if (ot.rootNode.getNumberOfPattern(true) == 1){
				userGoalArrayList.add(ot);
			}
		}
	}
}
