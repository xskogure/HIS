package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class Range {
	double min;
	double max;
	
	Range(double a, double b){
		min = a;
		max = b;
	}
	
	public boolean isContains(double n){
		if (min <= n && n < max){
			return true;
		}else{
			return false;
		}
	}
	public double value(){
		return max - min;
	}
	
	public String toString(){
		String str;
		str = "range(" + min + "," + max + ")";
		return str;
	}
}
