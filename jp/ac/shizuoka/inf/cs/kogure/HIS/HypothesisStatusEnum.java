package jp.ac.shizuoka.inf.cs.kogure.HIS;

public enum HypothesisStatusEnum {
	INITIAL("initial"),
	SUPPORTED("supported"),
	OFFERED("offered"),
	ACCEPTED("accepted"),
	REJECTED("rejected"),
	NOTFOUND("notfound"),
	;
	private String name;
	private HypothesisStatusEnum(String str){
		name = str;
	}
	
	public static HypothesisStatusEnum getTest(String n) {
		for (HypothesisStatusEnum test : HypothesisStatusEnum.values()) {
			if (test.name.equals(n)) {
				return test;
			}
		}
		return null;
	}


	public String toString(){
		return name;
	}
}
