package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.io.BufferedReader;

import org.apache.log4j.BasicConfigurator;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.ModelException;
import org.grayleaves.utility.Result;
import org.junit.Before;
import org.junit.Test;

public class ModelTest
{
	private static final String SPACE = " ";
	private Model<String> model;
	
	@Before
	public void setUp() throws Exception
	{
		TestingBean.resetForTesting();
		BasicConfigurator.resetConfiguration(); 
		model = new TestingModel<String>(); 
	}
//	@Test
	public void verifyTwoModelsAreTheSame() throws Exception
	{
		//TODO verify class, jar (libs?)
	}
	@Test
	public void verifyModelRunsAndGeneratesScenarioResult() throws Exception
	{
		model.setInput(new TestingInput(17)); 
		Result<String> result = model.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 3, boolean true\n"+
				"output:  20", result.toString()); 
	}
	@Test
	public void verifyModelThrowsExceptionUponError() throws Exception
	{
		model.setInput(new TestingInput(-3));
		try
		{
			@SuppressWarnings("unused")
			Result<String> result = model.run();
			fail();
		}
		catch (ModelException e)
		{
			assertEquals("TestingModel.run: negative input is invalid.", e.getMessage());
		}
		BufferedReader br = model.getLog(); 
		br.readLine();
		assertEquals("ERROR utility.TestingModel:  TestingModel.run: negative input is invalid."+SPACE, br.readLine());
	}
	@Test
	public void verifyModelWillLogItsActivity() throws Exception
	{
		model.run(); 
		BufferedReader br = model.getLog(); 
		assertEquals("INFO utility.TestingModel:  Testing Model initializing"+SPACE, br.readLine());
		assertEquals("INFO utility.TestingModel:  First output row written."+SPACE, br.readLine());
		assertEquals("INFO utility.TestingModel:  Second output row written."+SPACE, br.readLine());
		assertEquals("INFO utility.TestingModel:  Last output row written."+SPACE, br.readLine());
		assertNull(br.readLine()); 
	}
}
