package org.grayleaves.component;

import java.util.HashMap;
import java.util.Map;

import org.grayleaves.utility.Pattern;

public class ArrayComponentFeedback<F, O> implements ComponentFeedback<F, O>
{

	private F[] expected;
	private Map<Integer, F> mismatchMap;

	public ArrayComponentFeedback()
	{
	}
	@Override
	public Map<Integer, F> getMismatches(ComponentOutput<O> output) throws ArrayLengthMismatchException
	{
		mismatchMap = new HashMap<Integer, F>();
		if (expected.length != output.getOutput().length) 
			throw new ArrayLengthMismatchException("ArrayComponentFeedback.mismatched:  expected array of length "+expected.length+", but was length "+output.getOutput().length); 
		else 
		{
			for (int i = 0; i < expected.length; i++)
			{
				if (!expected[i].equals(output.getOutput()[i])) mismatchMap.put(i, expected[i]); 
				//TODO consider "else mismatchMap.put(i, null);" to express "no mismatch"
			}
		}
		return mismatchMap;
		
	}
	public void configureFeedbackPoints(Pattern pattern)
	{
	}
	@Override
	public void setFeedback(F[] expected)
	{
		this.expected = expected; 
	}


}
