package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class BeliefPointAndSelectedAction {
	BeliefPoint beliefPoint;
	Action      selectedAction;
	
	public BeliefPointAndSelectedAction(BeliefPoint bp, Action a) {
		beliefPoint = bp;
		selectedAction = a;
	}
}
