package jp.ac.shizuoka.inf.cs.kogure.HIS;

public class RecognitionRate {
	int nbest;
	double[] observationProb;
	Range[]   recogRate;
	
	RecognitionRate(int n, double[] prob){
		nbest = n;
		observationProb = new double[n];
		recogRate       = new Range[n+1];
		double totalProg = 0.0;
		for(int i = 0; i < n; i++){
			observationProb[i] = prob[i];
			recogRate[i] = new Range(totalProg, totalProg + prob[i]);
			totalProg = totalProg + prob[i];
		}
		recogRate[n] = new Range(totalProg, 1.0);
	}

	RecognitionRate(double[] prob){
		nbest = prob.length;
		observationProb = new double[nbest];
		recogRate       = new Range[nbest+1];
		double totalProg = 0.0;
		for(int i = 0; i < nbest; i++){
			observationProb[i] = prob[i];
			recogRate[i] = new Range(totalProg, totalProg + prob[i]);
			totalProg = totalProg + prob[i];
		}
		recogRate[nbest] = new Range(totalProg, 1.0);
	}
}
