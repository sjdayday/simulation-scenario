package org.grayleaves.component;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class ComponentHarnessTest
{
	private TestingComponent component;
	private ComponentAdapter<TestingComponent, Boolean, Boolean, Boolean> adapter;
	private ComponentHarness<TestingComponent, Boolean, Boolean, Boolean> harness;
	@Before
	public void setUp()
	{
		component = new TestingComponent(); 
		adapter = new TestingComponentAdapter<TestingComponent, Boolean, Boolean, Boolean>(component); 
		harness = null;
	}
	@Test
	public void verifyCanCreateComponentAdapterInComponentHarness() throws Exception
	{
		try 
		{
			harness = new ComponentHarness<TestingComponent, Boolean, Boolean, Boolean>(null);
			fail("Shouldn't accept null"); 
		}
		catch (NullComponentAdapterException e)
		{
			assertEquals("ComponentHarness constructor requires a non-null ComponentAdapter.", e.getMessage()); 
		}
	}
	//TODO ComponentHarness tests:
	//  ComponentAdapter gets inputs and outputs
	//  run the componentAdapter n times with given inputs & expected outputs
	//  compare actual outputs against expected outputs, counting errors:  componentFeedback 
	//  render component state at intervals
	//  
	@Test
	public void verifyComponentHarnessProvidesInputsToAdapterUnderTest() throws Exception
	{
		harness = new ComponentHarness<TestingComponent, Boolean, Boolean, Boolean>(adapter);
		ComponentInput<Boolean> input = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		harness.input(input); 
		ComponentOutput<Boolean> output = harness.process(); 
		ComponentOutput<Boolean> expected = new ArrayComponentOutput<Boolean>(new Boolean[]{false,true,false,true}); 
		assertEquals(expected, output); 
	}
}
