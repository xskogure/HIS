package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;

public class GridPointSet {
	ArrayList<GridPoint> gridPointArrayList;
	static final double[] PARAM = {1,1,1,1,1};
	
	GridPointSet(){
		gridPointArrayList = new ArrayList<GridPoint>();
	}
	
	public void addGridPoint(BeliefPoint bp, ActionEnum a, double R){
		GridPoint gTemp = new GridPoint(bp);
		gTemp.number.put(a, 1);
		gTemp.valueFunction.put(a, R);
		gridPointArrayList.add(gTemp);
	}
	
	public void updateGridPoint(GridPoint gp, ActionEnum a, double R){
		if (gp.number.containsKey(a)){
			int    nTemp = gp.number.get(a);
			double rTemp = gp.valueFunction.get(a);
			gp.number.put(a, nTemp + 1);
			gp.valueFunction.put(a, (nTemp * rTemp + R) / (nTemp + 1)); 
			
		}else{
			gp.number.put(a, 1);
			gp.valueFunction.put(a, R);
		}
	}
	
	public GridPoint getNearestGridPoint(double[] d, BeliefPoint bp){
		double currentDistance;
		double minDistance = 0.0;
		GridPoint result = null;
		for (int i = 0; i < gridPointArrayList.size(); i++){
			GridPoint   gTemp = gridPointArrayList.get(i);
			BeliefPoint bTemp = gTemp.beliefPoint;
			currentDistance = 
					PARAM[0] * Math.pow(bTemp.first  - bp.first , 2.0) +
					PARAM[1] * Math.pow(bTemp.second - bp.second, 2.0);
			
			if (Double.isNaN(currentDistance)){
				currentDistance = 0;
			}
			currentDistance = currentDistance + 
					PARAM[2] * (bTemp.hypothesisStatus == bp.hypothesisStatus ? 0 : 1) +
					PARAM[3] * (bTemp.partitionStatus  == bp.partitionStatus  ? 0 : 1) +
					PARAM[4] * (bTemp.lastSystemAction == bp.lastSystemAction ? 0 : 1);
			if (i == 0 || currentDistance < minDistance) {
				minDistance = currentDistance;
				result = gTemp;
			}
		}
		d[0] = minDistance;
		return result;
	}
}
