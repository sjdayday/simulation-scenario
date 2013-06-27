/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.grayleaves.utility.ComparisonResult;
import org.grayleaves.utility.MinimalScenarioLogAnalyzer;
import org.grayleaves.utility.ParameterSpaceMapperEnum;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioAnalyzer;
import org.grayleaves.utility.ScenarioAnalyzerEnum;
import org.grayleaves.utility.ScenarioSet;
import org.grayleaves.utility.ScenarioSetComparisonResult;
import org.grayleaves.utility.ScenarioSetReplicator;
import org.grayleaves.utility.ScenarioSetupAnalyzer;
import org.grayleaves.utility.SimpleScenarioLogAnalyzer;
import org.junit.Before;
import org.junit.Test;


public class ScenarioSetReplicatorTest extends TestingScenarioAbstract
{
	private ScenarioSetReplicator<String, TestingInput> replicator;
//	private OutputFileBuilder builder;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		replicator = new ScenarioSetReplicator<String, TestingInput>(); 
//		builder = new OutputFileBuilder("scenario_root"); 
	}
	@Test
	public void verifyAnalyzersCanBeAdded() throws Exception
	{
		addThreeScenarioAnalyzers();
		assertTrue(replicator.getScenarioAnalyzers().get(0) instanceof MinimalScenarioLogAnalyzer); 
		assertTrue(replicator.getScenarioAnalyzers().get(1) instanceof SimpleScenarioLogAnalyzer);
		assertTrue(replicator.getScenarioAnalyzers().get(2) instanceof ScenarioSetupAnalyzer);
	}

	@SuppressWarnings("unchecked")
	public void addThreeScenarioAnalyzers()
	{
		replicator.addScenarioAnalyzer(ScenarioAnalyzerEnum.MINIMAL_SCENARIO_LOG_ANALYZER); 
		replicator.addScenarioAnalyzer(ScenarioAnalyzerEnum.SIMPLE_SCENARIO_LOG_ANALYZER.getAnalyzer());
		replicator.addScenarioAnalyzer(ScenarioAnalyzerEnum.SCENARIO_SETUP_ANALYZER);
	}
	@Test
	public void verifySingleAnalyzerInvokedToPerformComparison() throws Exception
	{
		ComparisonResult result = null; 
		ScenarioAnalyzer<String, TestingInput> good = new GoodScenarioAnalyzer<String, TestingInput>();
		replicator.addScenarioAnalyzer(good);
		result = replicator.compare(baselineScenario, testScenario);  // scenario1, scenario2
		assertTrue(result.isMatch()); 
		assertEquals("org.grayleaves.utility.ScenarioSetReplicatorTest$GoodScenarioAnalyzer comparison details:  it's all good\n", result.getDetails()); 
	}
	@Test
	public void verifyOneNegativeAnalyzerResultFromMultipleAnalyzerMeansComparisonReturnsNoMatch() throws Exception
	{
		 
		ScenarioAnalyzer<String, TestingInput> good = new GoodScenarioAnalyzer<String, TestingInput>();
		ScenarioAnalyzer<String, TestingInput> bad = new BadScenarioAnalyzer<String, TestingInput>();
		replicator.addScenarioAnalyzer(good);
		replicator.addScenarioAnalyzer(bad);
		ComparisonResult result = replicator.compare(baselineScenario, testScenario);
		assertTrue(!result.isMatch()); 
		assertEquals("org.grayleaves.utility.ScenarioSetReplicatorTest$GoodScenarioAnalyzer comparison details:  it's all good\n"+
				"org.grayleaves.utility.ScenarioSetReplicatorTest$BadScenarioAnalyzer comparison details:  no match\n", result.getDetails()); 
	}
	@Test
	public void verifySameAnalyzerTypesUsedAcrossSetOfScenariosAndIdenticalScenarioSetsAreCompletelyComparable() throws Exception
	{
		ScenarioSetTest test = new ScenarioSetTest(); 
		test.setUp(); // WithoutScenarioSet(new Integer[]{40, 50});  // builds scenario set ?
		ScenarioSet<String, TestingInput> baselineScenarioSet = test.buildScenarioSet(1);  // builds scenario set
		baselineScenarioSet.run(); 
		test.setUp();
		ScenarioSet<String, TestingInput> comparedScenarioSet = test.buildScenarioSet(2); 
		comparedScenarioSet.run(); 
		addThreeScenarioAnalyzers();
		ScenarioSetComparisonResult result = replicator.compare(baselineScenarioSet, comparedScenarioSet);
		assertEquals("org.grayleaves.utility.ScenarioSetReplicator comparison summary:  "+
				"Compared scenario set (id 2) is comparable for all parameter points in Baseline scenario set (id 1) \n", result.getSummary()); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_MATCHES_BASELINE, result.getOverlap()); 
		assertEquals("Compared scenario set is comparable for the following parameter points in the Baseline scenario set:  \n"+
				" String Public Name=fred, Integer Public Name=40\n" +
				" String Public Name=fred, Integer Public Name=50\n" +
				" String Public Name=sam, Integer Public Name=40\n" +
				" String Public Name=sam, Integer Public Name=50\n" +
				"",result.getOverlapDetails()); 

		List<ComparisonResult> results = result.getComparisonResults(); 
		assertEquals(4, results.size()); 
		for (int i = 0; i < 4; i++)
		{
			assertTrue(results.get(i).isMatch());  
			assertEquals("org.grayleaves.utility.MinimalScenarioLogAnalyzer comparison details:  MinimalScenarioLogAnalyzer.compare:  "+
					"Test scenario (id "+(i+1)+") replicates Baseline scenario (id "+(i+1)+").\n"+
					"Record counts match:  4\n"+
					"Custom data match:  some custom data="+((i % 2 == 0) ? 57 : 67)+", another=true, last=random string\n\n"+
					"org.grayleaves.utility.SimpleScenarioLogAnalyzer comparison details:  SimpleScenarioLogAnalyzer.compare:  "+
					"Test scenario (id "+(i+1)+") replicates Baseline scenario (id "+(i+1)+").\n"+
					"Record counts match:  4\n"+
					"Log records match:  no differences\n\n"+
					"org.grayleaves.utility.ScenarioSetupAnalyzer comparison details:  ScenarioSetupAnalyzer.compare:  "+
					"Test scenario (id "+(i+1)+") replicates Baseline scenario (id "+(i+1)+").\n"+
					"Models match:  org.grayleaves.utility.TestingModel\n"+
					"Inputs match:  testingInput.xml\n"+
					"Parameter points match:  "+baselineScenarioSet.getScenario(i+1).getParameterPoint().toString()+"\n\n", results.get(i).getDetails());
		}
	}
	@Test
	public void verifyIdentifiesDisjointParameterSpaces() throws Exception
	{
//		ByteArrayOutputStream os = null; 
		ScenarioSetTest test = new ScenarioSetTest(); 
		test.setUp();
		ScenarioSet<String, TestingInput> baselineScenarioSet = test.buildScenarioSet(1);
		baselineScenarioSet.run(); 
		test.setUp(new Integer[] {60, 70}); 
		ScenarioSet<String, TestingInput> comparedScenarioSet = test.buildScenarioSet(2); 
		comparedScenarioSet.run(); 
		replicator.addScenarioAnalyzer(ScenarioAnalyzerEnum.SCENARIO_SETUP_ANALYZER);
		ScenarioSetComparisonResult result = replicator.compare(baselineScenarioSet, comparedScenarioSet);
		assertEquals("org.grayleaves.utility.ScenarioSetReplicator comparison summary:  "+
				"Compared scenario set (id 2) is not comparable for any parameter points in Baseline scenario set (id 1) \n", result.getSummary()); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_IS_DISJOINT_WITH_BASELINE, result.getOverlap());
		assertEquals(
				"Compared scenario set is not comparable for the following parameter points; they exist in the Compared scenario set but do not exist in the Baseline scenario set:  \n" +
				" String Public Name=fred, Integer Public Name=60\n" +
				" String Public Name=fred, Integer Public Name=70\n" +
				" String Public Name=sam, Integer Public Name=60\n" +
				" String Public Name=sam, Integer Public Name=70\n" +
				"Compared scenario set is not comparable for the following parameter points; they exist in the Baseline scenario set but do not exist in the Compared scenario set:  \n" +
				" String Public Name=fred, Integer Public Name=40\n" +
				" String Public Name=fred, Integer Public Name=50\n" +
				" String Public Name=sam, Integer Public Name=40\n" +
				" String Public Name=sam, Integer Public Name=50\n" +
				"",result.getOverlapDetails()); 
		List<ComparisonResult> results = result.getComparisonResults();  
		assertEquals(0, results.size()); 
	}
	@Test
	public void verifyReplicatorComparesPartiallyOverlappingParameterSpaces() throws Exception
	{
		ScenarioSetTest test = new ScenarioSetTest(); 
		test.setUp();
		ScenarioSet<String, TestingInput> baselineScenarioSet = test.buildScenarioSet(1);
		baselineScenarioSet.run(); 
		test.setUp(new Integer[] {50, 60}); 
		ScenarioSet<String, TestingInput> comparedScenarioSet = test.buildScenarioSet(2); 
		comparedScenarioSet.run(); 
		replicator.addScenarioAnalyzer(ScenarioAnalyzerEnum.SCENARIO_SETUP_ANALYZER);
		ScenarioSetComparisonResult result = replicator.compare(baselineScenarioSet, comparedScenarioSet);
		assertEquals("org.grayleaves.utility.ScenarioSetReplicator comparison summary:  "+
				"Compared scenario set (id 2) is comparable for some but not all parameter points in Baseline scenario set (id 1) \n", result.getSummary());
		assertEquals(ParameterSpaceMapperEnum.COMPARED_PARTIALLY_OVERLAPS_BASELINE, result.getOverlap());
		assertEquals("Compared scenario set is comparable for the following parameter points in the Baseline scenario set:  \n"+
				" String Public Name=fred, Integer Public Name=50\n" +
				" String Public Name=sam, Integer Public Name=50\n" +
				"Compared scenario set is not comparable for the following parameter points; they exist in the Compared scenario set but do not exist in the Baseline scenario set:  \n" +
				" String Public Name=fred, Integer Public Name=60\n" +
				" String Public Name=sam, Integer Public Name=60\n" +
				"Compared scenario set is not comparable for the following parameter points; they exist in the Baseline scenario set but do not exist in the Compared scenario set:  \n" +
				" String Public Name=fred, Integer Public Name=40\n" +
				" String Public Name=sam, Integer Public Name=40\n",result.getOverlapDetails()); 
		List<ComparisonResult> results = result.getComparisonResults();  
		assertEquals(2, results.size()); 
	}
	private class GoodScenarioAnalyzer<R, I> implements ScenarioAnalyzer<R, I> 
	{
		@Override
		public ComparisonResult compare(Scenario<R, I> scenario1, Scenario<R, I> scenario2)
		{
			return new ComparisonResult(true, "it's all good");
		}
		@Override
		public ScenarioAnalyzer<R, I> newCopy()
		{
			return new GoodScenarioAnalyzer<R, I>(); 
		}
	}
	private class BadScenarioAnalyzer<R, I> implements ScenarioAnalyzer<R, I>  
	{
		@Override
		public ComparisonResult compare(Scenario<R, I> scenario1, Scenario<R, I> scenario2)
		{
			return new ComparisonResult(false, "no match");
		}
		@Override
		public ScenarioAnalyzer<R, I> newCopy()
		{
			return new BadScenarioAnalyzer<R, I>(); 
		}
	}

}
