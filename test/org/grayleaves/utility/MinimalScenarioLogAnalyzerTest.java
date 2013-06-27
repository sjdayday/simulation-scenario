/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */


package org.grayleaves.utility;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.grayleaves.utility.MinimalScenarioLogAnalyzer;
import org.junit.Test;


public class MinimalScenarioLogAnalyzerTest extends TestingScenarioAbstract
{
//	private ScenarioLog<String, TestingInput> log;
	//TODO hibernate: determine how to retrieve templated classes (e.g., Scenario<R, I> 
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		analyzer = new MinimalScenarioLogAnalyzer<String, TestingInput>();
//		log = new ListScenarioLog<String, TestingInput>(); 
	}
	
    @Test
	public void verifyRecordCountsMatch() throws Exception
	{
    	comparison = analyzer.compare(baselineScenario, testScenario); 
		assertEquals("MinimalScenarioLogAnalyzer.compare:  Test scenario (id 6) replicates Baseline scenario (id 5).\n" +
				"Record counts match:  4\n"+
				"Custom data match:  Some data\n", comparison.getDetails());
		assertTrue(comparison.isMatch()); 
	}
	@Test
	public void verifyResultDoesntMatchIfOneScenarioIsNull() throws Exception
	{
		comparison = analyzer.compare(null, testScenario); 
		assertEquals("MinimalScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id null).\n"+
				"Reason:  one scenario is null.\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
	@Test
	public void verifyResultIdentifiesReasonForMismatch() throws Exception
	{
		testScenario = buildScenario(6, 5, "Some different data");
		comparison = analyzer.compare(baselineScenario, testScenario); 
//		System.out.println(comparison.getDetails());
		assertEquals("MinimalScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n"+
				"Reason:  Record counts do not match.\n" +
				"Test scenario (id 6) Record count:  5  Baseline scenario (id 5) Record count:  4\n" +
				"Reason:  Custom data do not match.\n" +
				"Test scenario (id 6) Custom data:  Some different data  Baseline scenario (id 5) Custom data:  Some data\n", 
				comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
	@Test
	public void verifyResultIdentifiesBothMatchesAndMismatches() throws Exception
	{
		testScenario = buildScenario(6, 4, "Some different data");
		comparison = analyzer.compare(baselineScenario, testScenario); 
		assertEquals("MinimalScenarioLogAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n"+
				"Reason:  Custom data do not match.\n" +
				"Test scenario (id 6) Custom data:  Some different data  Baseline scenario (id 5) Custom data:  Some data\n"+
				"\n"+
				"Record counts match:  4\n",
				comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
}
