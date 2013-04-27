package org.grayleaves.component;

public class TestingComponent
{

	private Boolean[] input;

	public void input(Boolean[] booleans)
	{
		input = booleans; 
	}

	public Boolean[] process()
	{
		Boolean[] output = new Boolean[input.length]; 
		for (int i = 0; i < input.length; i++)
		{
			output[i] = !input[i];
		}
		return output;
	}
	public void feedback(Integer index, Boolean feedback)
	{
		input[index] = !feedback; 
	}
}
