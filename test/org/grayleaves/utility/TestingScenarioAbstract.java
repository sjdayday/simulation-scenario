package org.grayleaves.utility;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.grayleaves.utility.ComparisonResult;
import org.grayleaves.utility.Input;
import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.ListScenarioLog;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.OutputFileBuilder;
import org.grayleaves.utility.OutputFileBuilderException;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioAnalyzer;
import org.grayleaves.utility.ScenarioException;
import org.grayleaves.utility.ScenarioLog;
import org.grayleaves.utility.SimpleScenario;
import org.grayleaves.utility.UnsupportedParameterException;
import org.junit.Before;

public class TestingScenarioAbstract
{

	protected Model<String> model;
	protected ParameterIterator iterator;
	protected ScenarioLog<String, TestingInput> scenarioLog;
	protected Scenario<String, TestingInput> baselineScenario;
	protected Scenario<String, TestingInput> testScenario;
	protected ScenarioAnalyzer<String, TestingInput> analyzer;
	protected ComparisonResult comparison;
	@Before
	public void setUp() throws Exception
	{
		TestingBean.resetForTesting();
		MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
//		buildScenarioLog(scenarioLog, 4, "Some data"); 
		baselineScenario = buildScenario(5, 4, "Some data");
		testScenario = buildScenario(6, 4, "Some data");
		scenarioLog = baselineScenario.getLog(); 
	}
	private Model<String> buildModel() {
		return new TestingModel<String>();
	}
	protected ScenarioLog<String, TestingInput> buildScenarioLog(ScenarioLog<String, TestingInput> log, int count, String data)
	{
		log.setRecordCount(count); 
		log.setCustomData(data); 
		return log; 
	}

	protected Scenario<String, TestingInput> buildScenario(int id, int count, String data) throws Exception
	{
		return buildScenarioWithInput(id, count, data, 17); 
	}
	protected Scenario<String, TestingInput> buildScenarioWithInput(int id,
			int count, String data, int testInput) throws InvalidStaticParameterException,
			UnsupportedParameterException, ScenarioException, OutputFileBuilderException
	{
		OutputFileBuilder builder = new OutputFileBuilder("scenario_root");

		Scenario<String, TestingInput> scenario = new SimpleScenario<String, TestingInput>(); 
		scenario.setName("Testing scenario");
		scenario.setId(id);
		scenario.setOutputFileBuilder(builder.cloneWithId(scenario.getId()));
		scenario.setModel(buildModel()); 
		scenario.setCalendar(MockClock.getCalendar()); 
		Input input = new TestingInput(testInput); 
		if (testInput == 17) input.setFilename("testingInput.xml");
		scenario.setInput(input); 
		ScenarioTest test = new ScenarioTest();
		iterator = test.buildParameterIterator(); 
		ParameterPoint point = iterator.next();
		scenario.setParameterPoint(point);
		ScenarioLog<String, TestingInput> log = new ListScenarioLog<String, TestingInput>(scenario); 
		buildScenarioLog(log, count, data);
		scenario.setLog(log); 
		return scenario;
	}


}
