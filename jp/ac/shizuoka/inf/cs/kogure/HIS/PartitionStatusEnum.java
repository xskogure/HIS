package jp.ac.shizuoka.inf.cs.kogure.HIS;

public enum PartitionStatusEnum {
	INITIAL("initial"),
	GENERIC("generic"),
	HUGEGROUP("hugegroup"),
	SMALLGROUP("smallgroup"),
	UNIQUE("unique"),
	UNKNOWN("unknown"),
	;
	private String name;
	private PartitionStatusEnum(String s){
		name = s;
	}
	
	public static PartitionStatusEnum getTest(String n) {
		for (PartitionStatusEnum test : PartitionStatusEnum.values()) {
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
