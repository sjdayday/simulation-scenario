/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import static org.junit.Assert.*;

import org.grayleaves.utility.Input;
import org.grayleaves.utility.PersistentInput;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.ScenarioSetupAnalyzer;
import org.junit.Before;
import org.junit.Test;

public class ScenarioSetupAnalyzerTest extends TestingScenarioAbstract
{
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		analyzer = new ScenarioSetupAnalyzer<String, TestingInput>(); 
	}
	@Test
	public void verifyTwoIdenticalScenariosShowAsReplicated() throws Exception
	{
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("ScenarioSetupAnalyzer.compare:  Test scenario (id 6) replicates Baseline scenario (id 5).\n" +
		"Models match:  org.grayleaves.utility.TestingModel\n"+
		"Inputs match:  testingInput.xml\n"+
		"Parameter points match:  6, tom, 0\n", comparison.getDetails());
		//TODO define matching for Models and Inputs
	}
	@Test
	public void verifyDifferentModelsShowAsMismatched() throws Exception
	{
		testScenario.setModel(new PersistentModel<String>()); 
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("ScenarioSetupAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n" +
		"Reason:  Models do not match.\n" +
		"Test scenario (id 6) Model:  org.grayleaves.utility.PersistentModel  Baseline scenario (id 5) Model:  org.grayleaves.utility.TestingModel\n" +
		"\n"+
		"Inputs match:  testingInput.xml\n"+
		"Parameter points match:  6, tom, 0\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
	@Test
	public void verifyDifferentInputsShowAsMismatched() throws Exception
	{
		//TODO determine if it matters that the analyzer is parameterized with a different Input type (TestingInput)
		Input input = new PersistentInput();
		input.setFilename("differentInput.xml"); 
		testScenario.setInput(input); 
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("ScenarioSetupAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n" +
		"Reason:  Inputs do not match.\n" +
		"Test scenario (id 6) Input:  differentInput.xml  Baseline scenario (id 5) Input:  testingInput.xml\n" +
		"\n"+
		"Models match:  org.grayleaves.utility.TestingModel\n"+
		"Parameter points match:  6, tom, 0\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
	@Test
	public void verifyDifferentParameterPointsShowAsMismatched() throws Exception
	{
		iterator = new ScenarioTest().buildParameterIterator(); 
		iterator.next(); 
		baselineScenario.setParameterPoint(iterator.next()); 
		comparison = analyzer.compare(baselineScenario, testScenario);
		assertEquals("ScenarioSetupAnalyzer.compare:  Test scenario (id 6) does not replicate Baseline scenario (id 5).\n" +
		"Reason:  Parameter points do not match.\n" +
		"Test scenario (id 6) Parameter point:  6, tom, 0  Baseline scenario (id 5) Parameter point:  6, tom, 10\n" +
		"\n"+
		"Models match:  org.grayleaves.utility.TestingModel\n"+
		"Inputs match:  testingInput.xml\n", comparison.getDetails());
		assertTrue(!comparison.isMatch()); 
	}
}