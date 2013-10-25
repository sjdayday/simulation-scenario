package org.grayleaves.utility;

import java.util.Arrays;

public class DoubleParameterShrinker<D>
{

	private Parameter<D> parameter;
	private double target;
	private Double previousValue;
	private Double nextValue;
	private double lowerBound;
	private double upperBound;
	private int shrinkFactor;
	private double range;
	

	public DoubleParameterShrinker(Parameter<D> parameter, double target)
	{
		validate(parameter); 
		this.parameter = parameter; 
		this.target = target; 
	}

	private void validate(Parameter<D> parameter)
	{
		if ((parameter == null) || (parameter.getParameterUpdater() == null)) 
			throw new IllegalArgumentException("DoubleParameterShrinker: input parameter was null or had a null ParameterUpdater.");
		if (!(parameter.getClass().equals(ArrayParameter.class))) 
			throw new IllegalArgumentException("DoubleParameterShrinker currently supports ArrayParameters."); 
		Class<D> clazz = parameter.getParameterUpdater().getParameterClass(); 
		if (clazz == null)
			throw new IllegalArgumentException("DoubleParameterShrinker currently only supports type Double.  Unable to determine type of input Parameter type, as ParameterClass of the ParameterUpdater was null.");
		if (!((clazz.equals(Double.class)) || (clazz.equals(double.class)))) 
			throw new IllegalArgumentException("DoubleParameterShrinker currently only supports type Double.");
	}

	@SuppressWarnings("unchecked")
	public Parameter<Double> shrink(int shrinkFactor)
	{
		if (((ArrayParameter<D>) parameter).getValues().length == 1) return (Parameter<Double>) parameter; 
		this.shrinkFactor = shrinkFactor; 
		findPreviousAndNextValuesForTarget(); 
		buildLowerAndUpperBoundsInCentralHalfOfPreviousNextRange();
		Double[] smallValues = buildNewValuesWithinSmallBounds();  
		return new ArrayParameter<Double>(parameter.getName(), (ParameterUpdater<Double>) parameter.getParameterUpdater(), smallValues);
	}


	protected void findPreviousAndNextValuesForTarget()
	{
		Double[] oldValues = buildSortedDoubleArrayFromOldParameter();
		Double targetValue = null; 
		nextValue = null; 
		previousValue = oldValues[0]; 
		for (int i = 0; i < oldValues.length; i++)
		{
			targetValue = oldValues[i];
			if (targetValue == target)
			{
				setLowerAndUpperBounds(i, oldValues); 
				break;
			}
			previousValue = targetValue; 
		}
	}

	protected Double[] buildSortedDoubleArrayFromOldParameter()
	{
		@SuppressWarnings("rawtypes")
		Object[] oldValues = ((ArrayParameter) parameter).getValues();
		Double[] oldDoubles = new Double[oldValues.length]; 
		for (int i = 0; i < oldDoubles.length; i++)
		{
			oldDoubles[i] = (Double) oldValues[i]; 
		}
		Arrays.sort(oldDoubles); 
		return oldDoubles;
	}
	private void buildLowerAndUpperBoundsInCentralHalfOfPreviousNextRange()
	{
		range = (nextValue - previousValue)/2;
		if (previousValue < target)
		{
			if (nextValue > target ) lowerBound = target - (range/2);
			else lowerBound = target - range;  
		}
		else lowerBound = target; 
		if (nextValue > target)
		{
			if (previousValue < target ) upperBound = target + (range/2);
			else upperBound = target + range;  
		}
		else upperBound = target; 
	}
	private Double[] buildNewValuesWithinSmallBounds()
	{
		int half = shrinkFactor / 2; 
		int size = ((lowerBound < target) && (upperBound > target)) ? shrinkFactor : half ;  
		Double[] values = new Double[size+1]; 
		double increment = range / size; 
		for (int i = 0; i < size+1; i++)
		{
			values[i] = lowerBound + (increment*i); 
		}
		if (size == shrinkFactor) values[half] = target; // let's make it exactly where we started
		return values;
	}

	private void setLowerAndUpperBounds(int i, Double[] oldValues)
	{
		if (i+1 < oldValues.length) nextValue = oldValues[i+1]; 
		else nextValue = target; 
		if (i == 0) previousValue = target;
	}


}

