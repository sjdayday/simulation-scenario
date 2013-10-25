/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class ScenarioTest
{
	protected List<Parameter<?>> list;
	protected ParameterIterator iterator;
	protected Scenario<String, TestingInput> scenario;
	protected Model<String> model; 
	@Before
	public void setUp() throws Exception
	{
		TestingFileBuilder.cleanUpDirectory("scenario_subdir");
		TestingBean.resetForTesting();
		scenario = new SimpleScenario<String, TestingInput>();
		buildParameterIterator();
        MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
        BasicConfigurator.resetConfiguration(); 
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ERROR);
        model = new TestingModel<String>(); 

	}
	public ParameterIterator buildParameterIterator()
			throws InvalidStaticParameterException,
			UnsupportedParameterException
	{
		list = buildParameterList();
		iterator = new SimpleParameterIterator(list);
		return iterator; 
	}
	public List<Parameter<?>> buildParameterList() throws InvalidStaticParameterException,
			UnsupportedParameterException
	{
		List<Parameter<?>> list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7, 8}));
		list.add(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"),
				new String[]{"tom", "sam"}));
		list.add(new RangeParameter<Integer>("Int2 Public Name", new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean"),
				0, 30, 10, 20));
		return list; 
	}
	@Test
	public void verifyScenarioIdentityAndDate() throws Exception
	{
		assertEquals("Default scenario name", scenario.getName()); 
		scenario.setName("Test scenario"); 
		assertEquals("Test scenario", scenario.getName());
		scenario.setId(8); 
		scenario.setCalendar(MockClock.getCalendar()); 
		assertEquals(8, scenario.getId());
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd__hh_mm_ssaa");
        assertEquals("2005_10_15__12_00_14PM",format.format(scenario.getCalendar().getTime()));
	}
	@Test
	public void verifyScenarioKnowsItsParameterPont() throws Exception
	{ 
		scenario.setParameterPoint(iterator.next());
		assertEquals("6, tom, 0", scenario.getParameterPoint().toString());
	}
	//TODO persist parameterpoint
	@Test
	public void verifySimpleScenarioPassesInputToAndReturnsResultsFromModel() throws Exception
	{
		buildScenario();
		ScenarioResult<String> result = scenario.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string tom, int 6, boolean true\n"+
				"output:  23", result.getResult().toString()); 
		scenario.getLog().close(); 
	}
	@Test
	public void verifyInputsAreValidated() 
	{
		try
		{
			scenario.run();
			fail("no model yet"); 
		}
		catch (ScenarioException e)
		{
			assertEquals("SimpleScenario.run: Scenario must have Model, Input, ParameterPoint, OutputFileBuilder before it can be run.", e.getMessage());
		}
		try
		{
			scenario.setModel(model); 
			scenario.run();
			fail("no Input, Parameter Point, OutputFileBuilder yet"); 
		}
		catch (ScenarioException e)
		{
			assertEquals("SimpleScenario.run: Scenario must have Input, ParameterPoint, OutputFileBuilder before it can be run.", e.getMessage());
		}
		try
		{
			scenario.setParameterPoint(iterator.next()); 
			scenario.run();
			fail("no Input, OutputFileBuilder yet"); 
		}
		catch (ScenarioException e)
		{
			assertEquals("SimpleScenario.run: Scenario must have Input, OutputFileBuilder before it can be run.", e.getMessage());
		}
		try
		{
			scenario.setOutputFileBuilder(new OutputFileBuilder("scenario_subdir")); 
			scenario.run();
			fail("no Input yet"); 
		}
		catch (ScenarioException e)
		{
			assertEquals("SimpleScenario.run: Scenario must have Input before it can be run.", e.getMessage());
		}
		catch (OutputFileBuilderException e)
		{
			fail("shouldnt throw here"); 
		}

	}
	@Test
	public void verifyScenarioExceptionThrownIfModelThrowsModelException() throws Exception
	{
		buildScenario();
		scenario.setModel(getExceptionModel());
		try 
		{
			scenario.run(); 
			fail("should throw");
		}
		catch (ScenarioException e)
		{
			assertEquals("SimpleScenario.run: ModelException: Sample ModelException thrown.", e.getMessage()); 
			assertEquals("ERROR utility.ScenarioLog:  SimpleScenario.run: ModelException: Sample ModelException thrown. ",scenario.getLog().getRecords().get(2));
		}
	}
	public TestingModel<String> getExceptionModel()
	{
		return new TestingModel<String>()
		{
			public Result<String> run() throws ModelException
			{
				throw new ModelException("Sample ModelException thrown."); 
			}
		};
	}
	@Test
	public void verifyScenarioLogFileSetUpWithHeader() throws Exception
	{
		buildScenario(); 
		((SimpleScenario<String, TestingInput>) scenario).buildLog(); 
		assertEquals("INFO utility.ScenarioLog:  Scenario: Testing scenario   Scenario Id: 4   Date/Time:  2005_10_15__12_00_14PM ",scenario.getLog().getRecords().get(0)); 
		scenario.getLog().close(); 
	}
	@Test
	public void verifyScenarioResultsAreLoggedInLogsDirectory() throws Exception
	{
		File file = new File("scenario_subdir"+Constants.SLASH+"logs"+Constants.SLASH+"Scenario_4_2005_10_15__12_00_14PM_Testing scenario.log");
		assertTrue(!file.exists()); 
		buildScenario(); 
		scenario.run(); 
		assertTrue(file.exists()); 
		
		assertEquals("data from object, not actual file","INFO utility.ScenarioLog:  Parameter point: Integer Name=6, String Public Name=tom, Int2 Public Name=0 ",scenario.getLog().getRecords().get(1)); 
		assertEquals("INFO utility.ScenarioLog:  TestingModel from input 17 ",scenario.getLog().getRecords().get(2)); 
		assertEquals("INFO utility.ScenarioLog:  parameters string tom, int 6, boolean true ",scenario.getLog().getRecords().get(3));
		assertEquals("INFO utility.ScenarioLog:  output:  23 ",scenario.getLog().getRecords().get(4));
		assertEquals("INFO utility.ScenarioLog:  Scenario: Testing scenario   Scenario Id: 4   Record count: 4   some custom data=23, another=true, last=random string ",scenario.getLog().getRecords().get(5));
	}
	@Test
	public void verifyModelReceivesOutputFileBuilder() throws Exception
	{
		assertNull(model.getOutputFileBuilder()); 
		buildScenario(); 
		scenario.run(); 
		assertEquals(4, model.getOutputFileBuilder().getId()); 
	}
	private void buildScenario() throws OutputFileBuilderException
	{
		OutputFileBuilder builder = new OutputFileBuilder("scenario_subdir");
		scenario.setName("Testing scenario");
		scenario.setId(4);
		scenario.setOutputFileBuilder(builder.cloneWithId(scenario.getId()));
		scenario.setModel(model); 
		scenario.setCalendar(MockClock.getCalendar()); 
		scenario.setInput(new TestingInput(17)); 
		scenario.setParameterPoint(iterator.next());
	}
}


