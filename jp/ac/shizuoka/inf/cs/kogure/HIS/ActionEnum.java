package jp.ac.shizuoka.inf.cs.kogure.HIS;

public enum ActionEnum {
	NONE("none"),
	HELLO("hello"),
	INFORM("inform"),
	NEGATE("negate"),
	REQUEST("request"),
	CONFIRM("confirm"),
	DENY("deny"),
	AFFIRM("affirm"),
	BYE("bye"),
	;
//	CONFREQ,
//	SELECT,
	
	private String name;
	private ActionEnum(String n){
		name = n;
	}

	public static ActionEnum getTest(String n) {
		for (ActionEnum test : ActionEnum.values()) {
			if (test.name.equals(n)) {
				return test;
			}
		}
		return null;
	}

	@Override
	public String toString(){
		return name;
	}
}
