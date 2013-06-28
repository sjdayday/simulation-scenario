/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.util.List;
import java.util.NoSuchElementException;

public class ParameterIterator 
{


	public static final String PARAMETER_LIST_CANNOT_BE_EMPTY = "ParameterIterator.next: Parameter list cannot be empty.";
	public static final String PARAMETER_LIST_SIZE_CANNOT_BE_CHANGED = "ParameterIterator.next: Parameter list size cannot be modified after ParameterIterator has been created.";
	public static final String PARAMETER_LIST_COMPLETELY_TRAVERSED = "ParameterIterator.buildNextPoint: Parameter list completely traversed; no further ParameterPoints exist.";
	private List<Parameter<?>> parameters;
	private int size;
	private int current;
	private ParameterValue<?>[] parameterValues;
	private boolean initialized;
	private boolean atTop = false;
	private boolean[] last;  
	
	public ParameterIterator(List<Parameter<?>> parameters)
	{
		this.parameters = parameters;
		size = parameters.size(); 
		if (size == 0) throw new IllegalArgumentException(PARAMETER_LIST_CANNOT_BE_EMPTY); 
		initialized = false;
		resetParameters();
		trackLastValues(); 
	}
	private void resetParameters()
	{
		for (Parameter<?> parameter : parameters)
		{
			parameter.reset(); 
		}
	}
	private void trackLastValues()
	{
		last = new boolean[size];
		for (int i = 0; i < last.length; i++)
		{
			last[i] = false; 
		}
	}
	public boolean hasNext()
	{
		boolean done = true; 
		for (int i = 0; i < last.length; i++)
		{
			done = done && last[i];
		}
		return !done; 
	}
	public ParameterPoint next()
	{
		verifyState();
		if (initialized) buildNextPoint();  
		else  setUpParameterValueArray(); 
		return new ParameterPoint(copyParameterValuesArray(), parameters);
	}
	private ParameterValue<?>[] copyParameterValuesArray()
	{
		ParameterValue<?>[] parameterValuesCopy = new ParameterValue<?>[size]; 
		for (int i = 0; i < size; i++)
		{
			parameterValuesCopy[i] = parameterValues[i];
		}
		return parameterValuesCopy;
	}
	private void buildNextPoint()
	{
		if (currentParameter().hasNext()) setNextParameterValue();
		else 
		{
			bubbleUpUntilTop();  
			if (atTop)
			{
				if (currentParameter().hasNext())	
				{
					rippleDownResetToFirstValue(); 
				}
				else throw new NoSuchElementException(PARAMETER_LIST_COMPLETELY_TRAVERSED); 
			}
		}
	}
	private Parameter<?> currentParameter()
	{
		return parameters.get(current);
	}
	private void setNextParameterValue()
	{
		parameterValues[current] = currentParameter().nextParameterValue();
		last[current] = !currentParameter().hasNext(); 
	}
	private void bubbleUpUntilTop()
	{
		last[current] = true;
		if	(current > 0) 
		{
			current--;
			if (currentParameter().hasNext())	
			{
				rippleDownResetToFirstValue(); 
			}
			else bubbleUpUntilTop(); 
		}
		else atTop  = true; 
	}
	private void rippleDownResetToFirstValue()
	{
		setNextParameterValue();
		if (current < size-1)
		{
			current++;
			rippleDownResetToFirstValue(); 
		}
	}
	private void setUpParameterValueArray()
	{
		parameterValues = new ParameterValue<?>[size];
		for (int i = 0; i < size; i++)
		{
			current = i;
			if (hasNext()) setNextParameterValue();
		}
		initialized = true; 
	}
	private void verifyState()
	{
		if (size != parameters.size()) throw new IllegalStateException(PARAMETER_LIST_SIZE_CANNOT_BE_CHANGED+"; was "+size+", is now "+parameters.size()+".");
	}
}
