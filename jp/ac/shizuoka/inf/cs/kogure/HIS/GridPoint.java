package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.HashMap;

public class GridPoint {
	BeliefPoint beliefPoint;
	HashMap<ActionEnum, Integer> number;
	HashMap<ActionEnum, Double>  valueFunction;
	ActionEnum optimizedAction;
	
	GridPoint(BeliefPoint bp){
		beliefPoint = bp;
		number = new HashMap<ActionEnum, Integer>();
		valueFunction = new HashMap<ActionEnum, Double>();
	}
	
	public String toString(){
		String str="";
		str = str + beliefPoint.first + "," +
					beliefPoint.second + "," +
					beliefPoint.hypothesisStatus + "," +
					beliefPoint.partitionStatus + "," +
					beliefPoint.lastSystemAction + "," +
					optimizedAction;
		return str;
	}
}
