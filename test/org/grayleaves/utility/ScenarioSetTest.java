package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.Constants;
import org.grayleaves.utility.Input;
import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.NameValuePair;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioException;
import org.grayleaves.utility.ScenarioResult;
import org.grayleaves.utility.ScenarioSet;
import org.grayleaves.utility.SimpleScenario;
import org.grayleaves.utility.StaticParameterUpdater;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ScenarioSetTest 
{

	public ScenarioSet<String, TestingInput> scenarioSet;
	private ParameterSpace parameterSpace;
	private Model<String> model;
	private Input input;
	@BeforeClass
	public static void setUpLog4J() throws Exception
	{
	}
	
	@Before
	public void setUp() throws Exception
	{
		TestingFileBuilder.buildPersistentInput(); 
		URL url = this.getClass().getClassLoader().getResource("log4j.properties");
		if (url == null) System.out.println("log4j not found");
		setUp(new Integer[]{40, 50});
	}
	@After
	public void tearDown() throws Exception
	{
		TestingFileBuilder.cleanUpDirectory("ScenarioSet_0");
		TestingFileBuilder.cleanUpDirectory("ScenarioSet_1");
		TestingFileBuilder.cleanUpDirectory("ScenarioSet_2");
		TestingFileBuilder.cleanUpDirectory("ScenarioSet_4");
		TestingFileBuilder.cleanUpDirectory("ScenarioSet_5");
	}
	public void setUp(Integer[] ints) throws Exception
	{
		setUpWithoutScenarioSet(ints);
		scenarioSet = buildScenarioSet(); 
	}
	public void setUpWithoutScenarioSet(Integer[] ints)
			throws InvalidStaticParameterException
	{
		TestingBean.resetForTesting(); 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		model = new TestingModel<String>(); 
		parameterSpace = buildSmallParameterSpace(ints);
		input = new TestingInput(3); 
		input.setFilename("testingInput.xml"); 
		MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
	}
	public static ParameterSpace buildSmallParameterSpace() throws InvalidStaticParameterException
	{
		return buildSmallParameterSpace(new Integer[]{40, 50}); 
	}
	public static ParameterSpace buildSmallParameterSpace(Integer[] ints)
	throws InvalidStaticParameterException
	{
		ParameterSpace parameterSpace = new ParameterSpace(); 
		addSmallParameters(parameterSpace, ints);
		return parameterSpace; 
	}
	public static void addSmallParameters(ParameterSpace parameterSpace) throws InvalidStaticParameterException
	{
		addSmallParameters(parameterSpace, new Integer[]{40, 50});
	}
	public static void addSmallParameters(ParameterSpace parameterSpace, Integer[] ints)
	throws InvalidStaticParameterException
	{
		parameterSpace.addParameter(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"), new String[]{"fred", "sam"}));
		parameterSpace.addParameter(new ArrayParameter<Integer>("Integer Public Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), ints));
	}
	@Test
	public void verifyRunsModelWithAllPointsFromParameterSpace() throws Exception
	{
		List<ScenarioResult<String>> results = scenarioSet.getScenarioResults(); 
		assertEquals(0, results.size());
		scenarioSet.run(); 
		results = scenarioSet.getScenarioResults(); 
		assertEquals(4, results.size());
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 40, boolean true\n"+
				"output:  57", results.get(0).getResult().toString()); 
		assertEquals("some custom data=57, another=true, last=random string", results.get(0).getResult().getSummaryData()); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 50, boolean true\n"+
				"output:  67", results.get(1).getResult().toString()); 
		assertEquals("some custom data=67, another=true, last=random string", results.get(1).getResult().getSummaryData()); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string sam, int 40, boolean true\n"+
				"output:  57", results.get(2).getResult().toString()); 
		assertEquals("some custom data=57, another=true, last=random string", results.get(2).getResult().getSummaryData()); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string sam, int 50, boolean true\n"+
				"output:  67", results.get(3).getResult().toString()); 
		assertEquals("some custom data=67, another=true, last=random string", results.get(3).getResult().getSummaryData()); 

	}
	@Test
	public void verifyScenarioSetValidatesRequiredInputsBeforeBatchRun() throws Exception
	{
		scenarioSet = new ScenarioSet<String, TestingInput>(); 
		tryBatchRunWithoutNecessaryInputsExpectingException("set parameter space required before batch run.");
		setUp(); 
		tryBatchRunWithoutNecessaryInputsExpectingException("build summary header required before batch run.");
	}
	private void tryBatchRunWithoutNecessaryInputsExpectingException(String exception)
	{
		try
		{
			scenarioSet.batchRun();
			fail("expecting scenario exception: "+exception);
		}
		catch (ScenarioException e)
		{
			assertEquals("ScenarioSet.validate:  "+exception, e.getMessage());
		}
	}
	@Test
	public void verifyScenarioSetRunsScenariosOneAtaTimeCreatingSummaryXls() throws Exception
	{
		scenarioSet.setId(5);
		File file = new File("ScenarioSet_5"+Constants.SLASH+"scenario_set_summary_5_test scenario set.csv");
		if (file.exists()) file.delete(); 
		scenarioSet.buildSummaryHeader(new NameValuePair<Integer>("some custom data", Integer.class), new NameValuePair<Boolean>("another", Boolean.class), new NameValuePair<String>("last", String.class)); 
		scenarioSet.batchRun(); 
		assertTrue(file.exists()); 
		assertEquals("\"scenario\",\"some custom data\",\"another\",\"last\",\"String Public Name\",\"Integer Public Name\""+
				Constants.CRLF+
				"1,57,true,\"random string\",\"fred\",40"+Constants.CRLF+ 
				"2,67,true,\"random string\",\"fred\",50"+Constants.CRLF+ 
				"3,57,true,\"random string\",\"sam\",40"+Constants.CRLF+ 
				"4,67,true,\"random string\",\"sam\",50"+Constants.CRLF, 
				TestingFileBuilder.getFileContents(file.getCanonicalPath())); 
		
	}
	@Test
	public void verifyCreatesSummarySpreadsheet() throws Exception
	{
		scenarioSet.setId(5);
		File file = new File("ScenarioSet_5"+Constants.SLASH+"scenario_set_summary_5_test scenario set.csv");
		if (file.exists()) file.delete(); 
		scenarioSet.run(); 
		scenarioSet.buildSummary(new NameValuePair<Integer>("some custom data", Integer.class), new NameValuePair<Boolean>("another", Boolean.class), new NameValuePair<String>("last", String.class)); 
		assertTrue(file.exists()); 
		assertEquals("\"scenario\",\"some custom data\",\"another\",\"last\",\"String Public Name\",\"Integer Public Name\""+
				Constants.CRLF+
				"1,57,true,\"random string\",\"fred\",40"+Constants.CRLF+ 
				"2,67,true,\"random string\",\"fred\",50"+Constants.CRLF+ 
				"3,57,true,\"random string\",\"sam\",40"+Constants.CRLF+ 
				"4,67,true,\"random string\",\"sam\",50"+Constants.CRLF, 
				TestingFileBuilder.getFileContents(file.getCanonicalPath())); 
//		assertEquals("MemoryNetworkModel.run:  Created trained MemoryNetwork file:  pajek_simulation_4_fred.net", result.getList().get(1)); 
	
	}
	@Test
	public void verifyScenarioCanBeRetrievedById() throws Exception
	{
		scenarioSet.run();
		Scenario<String, TestingInput> scenario = scenarioSet.getScenario(4);
		assertEquals("sam, 50", scenario.getParameterPoint().toString()); 
	}
	@Test
	public void verifyCannotRetrieveScenarioThatDoesntBelongToScenarioSet() throws Exception
	{
		scenarioSet.run();
		assertNull(scenarioSet.getScenario(5));
	}
	public ScenarioSet<String, TestingInput> buildScenarioSet()
	{
		ScenarioSet<String, TestingInput> scenarioSet = new MockHibernateScenarioSet<String, TestingInput>(true);		
		scenarioSet.setModel(model);
		scenarioSet.setInput(input);
		scenarioSet.setParameterSpace(parameterSpace); 
		scenarioSet.setCalendar(MockClock.getCalendar());
		scenarioSet.setName("test scenario set"); 
		return scenarioSet; 
	}
	public ScenarioSet<String, TestingInput> buildScenarioSet(int id)
	{
		ScenarioSet<String, TestingInput> scenarioSet = buildScenarioSet();
		scenarioSet.setId(id);
		try
		{
			scenarioSet.buildOutputFileBuilder();
		}
		catch (ScenarioException e)
		{
			fail("shouldnt throw");
		}
		return scenarioSet; 
	}
	@Test
	public void verifyScenarioSetThrowsExceptionWhenOneScenarioThrows() throws Exception
	{
		scenarioSet = new ExceptionScenarioSet<String, TestingInput>(true); 
		scenarioSet.setModel(model);
		scenarioSet.setInput(input);
		scenarioSet.setParameterSpace(parameterSpace); 
		scenarioSet.setCalendar(MockClock.getCalendar());
		
		try 
		{
			scenarioSet.run(); 
			fail("should throw");
		}
		catch (ScenarioException e)
		{
			assertEquals("ScenarioSet.runScenario:  ScenarioException encountered for Scenario 7:  Scenario threw an exception.", e.getMessage()); 
		}
	}
	private class ExceptionScenarioSet<R, I> extends ScenarioSet<R, I>
	{
		public ExceptionScenarioSet(boolean listLog)
		{
			super(listLog);
			try
			{
				buildOutputFileBuilder();
			}
			catch (ScenarioException e)
			{
				fail("shouldn't throw"); 
			}
		}
		@Override
		protected void buildScenarios() throws ScenarioException
		{
			scenarios.add(buildScenario());
		}
		@Override
		protected SimpleScenario<R, I> buildScenario()
		{
			return new SimpleScenario<R, I>()
			{
				// anonymous inner override
				@Override
				public ScenarioResult<R> run() throws ScenarioException
				{
					throw new ScenarioException("Scenario threw an exception."); 
				}
				@Override
				public int getId()
				{
					return 7;
				}
			};
		}
//		public List<HibernateRetrievable> getRetrievableScenarios()
//		{
//			retrievableScenarios = new ArrayList<HibernateRetrievable>(); 
//			for (Scenario<R, I> scenario : scenarios)
//			{
//				retrievableScenarios.add((HibernateRetrievable) scenario);
//			}
//			return retrievableScenarios; 
//		}
		
	}

	//TODO each scenario persisted as it is run.  retrieve the set of scenarios through hibernate, then retrieve its logs
	
	//TODO when finished, return to ScenarioSetReplicatorTest
}
