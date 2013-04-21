package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.grayleaves.utility.ScenarioLog;
import org.grayleaves.utility.SimpleScenarioLogAnalyzer;
import org.junit.Test;


public class SimpleScenarioLogAnalyzerTest extends TestingScenarioAbstract
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		analyzer = new SimpleScenarioLogAnalyzer<String, TestingInput>();
		TestingFileBuilder.buildPersistentInput(); 
	}
	@Test
	public void verifyComparisonFailsIfLogFileNotFound() throws Exception
	{
		ScenarioLog<String, TestingInput> log = new ScenarioLog<String, TestingInput>();
		log.setFilename("invalidFile.txt"); 
		testScenario.setLog(log); 
		baselineScenario = buildScenario(5, 0, "Some data");
		baselineScenario.run(); 
    	comparison = analyzer.compare(baselineScenario, testScenario); 
		assertEquals("SimpleScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n"+
				"Reason:  Log files do not match.\n" +
				"Test scenario (id 6) Log file:  does not exist or could not be read.  Baseline scenario (id 5) Log file:  exists.\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
		assertTrue(comparison.getMoreDetails().startsWith("ScenarioLog.buildRecordsFromLog")); 
//		testScenario.setLog(scenarioLog);
//		baselineScenario.setLog(log); 
//		comparison = analyzer.compare(baselineScenario, testScenario); 
//		assertEquals("SimpleScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n"+
//				"Reason:  Log files do not match.\n" +
//				"Test scenario (id 6) Log file:  exists.  Baseline scenario (id 5) Log file:  does not exist or could not be read.\n", comparison.getDetails());
//		assertTrue(!comparison.isMatch()); 
		
	}
	@Test
	public void verifyComparisonFailsGracefullyIfLogIsNull() throws Exception
	{
		testScenario.setLog(null); 
    	comparison = analyzer.compare(baselineScenario, testScenario); 
		assertEquals("SimpleScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n"+
				"Reason:  Log files do not match.\n" +
				"Test scenario (id 6) Log file:  is null.  Baseline scenario (id 5) Log file:  may exist.\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
	@Test
	public void verifyComparisonSucceedsIfRecordsMatch() throws Exception
	{
		baselineScenario = buildScenario(5, 0, "Some data");
		testScenario = buildScenario(6, 0, "Some data");
		testScenario.run(); 
		baselineScenario.run(); 
		assertEquals(4, testScenario.getLog().getRecordCount());
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("SimpleScenarioLogAnalyzer.compare:  Test scenario (id 6) replicates Baseline scenario (id 5).\n" +
				"Record counts match:  4\n"+
				"Log records match:  no differences\n", comparison.getDetails());
	}
	@Test
	public void verifyComparisonFailsIfRecordsDontMatch() throws Exception
	{
		//FIXME input is file, not int
		baselineScenario = buildScenarioWithInput(5, 0, "Some data", 17);
		testScenario = buildScenarioWithInput(6, 0, "Some data", 18);
		testScenario.run(); 
		baselineScenario.run(); 
		assertEquals(4, testScenario.getLog().getRecordCount());
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("SimpleScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n" +
				"Reason:  Log records do not match.\n" +
				"Test scenario (id 6) Log record:  differs from  Baseline scenario (id 5) Log record:  more details available.\n\n" +
				"Record counts match:  4\n", comparison.getDetails());
		assertEquals("record 2:  expected:  INFO utility.ListScenarioLog:  TestingModel from input 1[7] \n" +
				"encountered:  INFO utility.ListScenarioLog:  TestingModel from input 1[8] \n" +
				"record 4:  expected:  INFO utility.ListScenarioLog:  output:  2[3] \n"+
				"encountered:  INFO utility.ListScenarioLog:  output:  2[4] \n",comparison.getMoreDetails());
//				"Log records do not match:  \n", comparison.getDetails());/
//		assertEquals("INFO utility.ScenarioLog:  TestingModel from input 17 ",scenario.getLog().getRecords().get(1)); 
//		assertEquals("INFO utility.ScenarioLog:  parameters string tom, int 6, boolean true ",scenario.getLog().getRecords().get(2));
//		assertEquals("INFO utility.ScenarioLog:  output:  23 ",scenario.getLog().getRecords().get(3));
//		assertEquals("INFO utility.ScenarioLog:  Scenario: Testing scenario   Scenario Id: 4   Record count: 3 ",scenario.getLog().getRecords().get(4));

	}
}
