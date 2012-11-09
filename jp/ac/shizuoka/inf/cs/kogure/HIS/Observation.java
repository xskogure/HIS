package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class Observation {
	double prob;
	Action action;
	boolean isCorrect;
	
	public String toString(){
		String str;
		if (isCorrect) str = "*"; else str = " ";
		str = str + prob + ": " + action;
		return str;
	}
}
