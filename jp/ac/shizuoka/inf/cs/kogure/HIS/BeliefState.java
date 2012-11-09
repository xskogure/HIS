package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class BeliefState {
	State state;
	Double prob;
	
	BeliefState(State s){
		state = s;
		prob = 0.0;
	}
	
	public String toString(){
		return "" + prob;
	}
}
