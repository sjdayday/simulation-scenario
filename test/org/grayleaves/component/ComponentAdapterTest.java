package org.grayleaves.component;

import static org.junit.Assert.*;

import org.junit.Test;
import org.grayleaves.utility.Pattern;


public class ComponentAdapterTest
{
	private ComponentAdapter<TestingComponent, Boolean, Boolean, Boolean> adapter;

	@Test
	public void verifyComponentUnderTestIsTheSameAsThatPassedIn() throws Exception
	{
		TestingComponent component = new TestingComponent();
		adapter = new TestingComponentAdapter<TestingComponent, Boolean, Boolean, Boolean>(component);
		assertSame(component, adapter.getComponent()); 
		assertTrue(adapter.getComponent() instanceof TestingComponent); 
	}
	
	@Test
	public void verifyComponentAdapterCreatesInputsToAndTransformsOutputsFromTestingComponent() throws Exception
	{
		adapter = new TestingComponentAdapter<TestingComponent, Boolean, Boolean, Boolean>(new TestingComponent()); 
		ComponentInput<Boolean> inputs = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		adapter.input(inputs);
		ComponentInput<Boolean> expected = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false});
		assertEquals(expected, inputs); 
		ComponentOutput<Boolean> results = adapter.process(); 
		ComponentOutput<Boolean> expectedResults = new ArrayComponentOutput<Boolean>(new Boolean[]{false,true,false,true});
		assertEquals(expectedResults, results); 
	}
	@Test
	public void verifyTypicalComponentAdapterHandlesIncorrectClassBeingPassedIn() throws Exception
	{
		ComponentAdapter<TestingComponent, Integer, Integer, Integer> adapter = new TestingComponentAdapter<TestingComponent, Integer, Integer, Integer>(new TestingComponent());
		ComponentInput<Integer> inputs = new ArrayComponentInput<Integer>(new Integer[]{1,2,3,4});
		try 
		{
			adapter.input(inputs);
		}
		catch (RuntimeException e )
		{
			assertTrue(e.getMessage().startsWith("TestingComponentAdapter.input:  Expected Boolean[], received"));
		}
	}
	@Test
	public void verifyFeedbackInsertsIntoComponentUnderTestFollowingPattern() throws Exception
	{
		adapter = new TestingComponentAdapter<TestingComponent, Boolean, Boolean, Boolean>(new TestingComponent()); 
		ComponentInput<Boolean> inputs = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		adapter.input(inputs);
//		ComponentOutput<Boolean> output = 
			adapter.process(); 
		ComponentFeedback<Boolean, Boolean> feedback = new ArrayComponentFeedback<Boolean, Boolean>();
		feedback.setFeedback(new Boolean[]{false, false, false, false});
		assertTrue(!adapter.feedbackConfigured());
		adapter.configureFeedbackPoints(feedback, new Pattern(new Boolean[]{true, false}));
		assertTrue(adapter.feedbackConfigured()); 
	}
	@Test
	public void verifyFeedbackCollectedAndPresentedToTheAdapter() throws Exception
	{
		adapter = new TestingComponentAdapter<TestingComponent, Boolean, Boolean, Boolean>(new TestingComponent()); 
		ComponentInput<Boolean> inputs = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		adapter.input(inputs);
		ComponentOutput<Boolean> output = adapter.process(); 
		ComponentOutput<Boolean> expectedResults = new ArrayComponentOutput<Boolean>(new Boolean[]{false,true,false,true});
		assertEquals(expectedResults, output); 
		ComponentFeedback<Boolean, Boolean> feedback = new ArrayComponentFeedback<Boolean, Boolean>();
		feedback.setFeedback(new Boolean[]{false, false, false, false});
		adapter.feedback(feedback);
		output = adapter.process(); 
		expectedResults = new ArrayComponentOutput<Boolean>(new Boolean[]{false,false ,false,false});
		assertEquals(expectedResults, output); 
	}
}
