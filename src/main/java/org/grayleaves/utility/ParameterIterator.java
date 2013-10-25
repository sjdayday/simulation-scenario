package org.grayleaves.utility;

public interface ParameterIterator
{

	public static final String PARAMETER_LIST_CANNOT_BE_EMPTY = "ParameterIterator.next: Parameter list cannot be empty.";
	public static final String PARAMETER_LIST_SIZE_CANNOT_BE_CHANGED = "ParameterIterator.next: Parameter list size cannot be modified after ParameterIterator has been created.";
	public static final String PARAMETER_LIST_COMPLETELY_TRAVERSED = "ParameterIterator.buildNextPoint: Parameter list completely traversed; no further ParameterPoints exist.";

	public boolean hasNext();

	public ParameterPoint next();
	
	//TODO move:  ideally should be set in constructor rather than added to the interface.
	public void setFitnessTracker(FitnessTracker fitnessTracker);

}