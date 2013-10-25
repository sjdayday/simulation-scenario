package org.grayleaves.utility;

import java.util.List;

public interface FitnessTracker
{

	public List<ParameterPoint> getBestParameterPoints();

	public void setCurrentParameterPoint(ParameterPoint point);

}
