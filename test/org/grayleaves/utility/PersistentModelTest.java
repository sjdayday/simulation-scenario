package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.ModelException;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.Result;
import org.junit.Before;
import org.junit.Test;


public class PersistentModelTest
{
	private Model<String> model; 
	
	@Before
	public void setUp() throws Exception
	{
		model = new PersistentModel<String>(); 
		BasicConfigurator.resetConfiguration(); 
		TestingBean.resetForTesting(); 
	}
	@Test
	public void verifyDetectsClassNotFoundOrWrongType() throws Exception
	{
		model.setClazz("org.grayleaves.utility.InvalidClass"); 
		try
		{
			model.run(); 
			fail("should throw CNFE");
		}
		catch (ModelException e)
		{
			assertTrue(e.getMessage().startsWith("PersistentModel.buildModel Model Exception instantiating org.grayleaves.utility.InvalidClass: java.lang.ClassNotFoundException"));
		}
		model.setClazz("org.grayleaves.utility.Scenario");
		try
		{
			model.run(); 
			fail("should throw instantiation exception for interface");
		}
		catch (ModelException e)
		{
			assertTrue(e.getMessage().startsWith("PersistentModel.buildModel Model Exception instantiating org.grayleaves.utility.Scenario: java.lang.InstantiationException"));
		}
		model.setClazz("org.grayleaves.utility.SimpleScenario");
		try
		{
			model.run(); 
			fail("should throw class cast exception -- class is not a Model");
		}
		catch (ModelException e)
		{
//			assertEquals("fred", e.getMessage()); 
			assertTrue(e.getMessage().startsWith("PersistentModel.buildModel Model Exception instantiating org.grayleaves.utility.SimpleScenario: java.lang.ClassCastException"));
		}
	}
	@Test
	public void verifyInvokesDomainModel() throws Exception
	{
		model.setClazz("org.grayleaves.utility.TestingModel"); 
		model.setInput(new TestingInput(17)); 
		Result<String> result = model.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 3, boolean true\n"+
				"output:  20", result.toString()); 
	}
}
