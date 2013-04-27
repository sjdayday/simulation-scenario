package org.grayleaves.component;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.grayleaves.utility.Pattern;

public class TestingComponentAdapter<C, I, O, F> implements ComponentAdapter<C, I, O, F>
{

	private TestingComponent component;
	private ComponentOutput<O> output;
	private boolean feedbackConfigured;
	public TestingComponentAdapter(TestingComponent testingComponent)
	{
		component = testingComponent;
		feedbackConfigured = false; 
	}
	@Override
	public void input(ComponentInput<I> inputs)
	{
		
		I[] arrayOfSomething = inputs.getInput(); 
		try 
		{
			component.input((Boolean[]) arrayOfSomething);  
		}
		catch (ClassCastException e)
		{
			throw new RuntimeException("TestingComponentAdapter.input:  Expected Boolean[], received\n"+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public ComponentOutput<O> process()
	{
		output = new ArrayComponentOutput<O>(((O[])component.process()));
		return output;
	}
	@Override
	public void feedback(ComponentFeedback<F, O> feedback)
	{
		try
		{
			Map<Integer, F> mismatches = feedback.getMismatches(output);
			Set<Entry<Integer, F>> set = mismatches.entrySet(); 
			for (Entry<Integer, F> entry : set)
			{
				component.feedback(entry.getKey(), (Boolean) entry.getValue()); 
			}
		} 
		catch (ArrayLengthMismatchException e)
		{
			//TODO log this 
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public C getComponent()
	{
		return (C) component;
	}
	@Override
	public void configureFeedbackPoints(ComponentFeedback<F, O> feedback, Pattern pattern)
	{
		feedbackConfigured = true; 
	}
	@Override
	public boolean feedbackConfigured()
	{
		return feedbackConfigured;
	}

}
