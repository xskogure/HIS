package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class State {
	Partition       partition;
	Action          userAction;
	DialogueHistory dialogueHistory;
	
	State(Partition p, Action a, DialogueHistory d){
		partition       = p;
		userAction      = a;
		dialogueHistory = d;
	}
}
