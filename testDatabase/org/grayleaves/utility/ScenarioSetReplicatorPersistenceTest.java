/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class ScenarioSetReplicatorPersistenceTest extends AbstractHistoryTest
{
	private ScenarioAnalyzer<String, PersistentInput> analyzer;
	private Scenario<String, PersistentInput> scenario;
	private Scenario<String, PersistentInput> scenario1; 
	private Scenario<String, PersistentInput> scenario2;
	private ParameterIterator iterator;
	private String filename;
	private int scenarioId1;
	private int scenarioId2;
	private ScenarioSetReplicator<String, PersistentInput> replicator;
	@SuppressWarnings("unused")
	private int modelId;
	private ScenarioTest test;
	private ScenarioResult<String> result; 
	private ParameterSpace baselineSpace;
	private ParameterSpace comparedSpace;
	private OutputFileBuilder builder;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		cleanUpFile("baselineSpace.xml");
		cleanUpFile("comparedSpace.xml");
		TestingFileBuilder.cleanUpDirectory("scenario_root");
		test = new ScenarioTest();
		iterator = test.buildParameterIterator(); 
		buildPersistentInput();
		buildScenario("1");
		scenarioId1 = scenario.getId(); 
		checkScenario(scenario); 
		buildScenario("2");
		scenarioId2 = scenario.getId();
		checkScenario(scenario);
		replicator = new ScenarioSetReplicator<String, PersistentInput>(); 
		// spaces have to be persisted: AbstractHistoryTest 
		baselineSpace = ParameterSpaceMapperTest.buildParameterSpace(new Integer[] {6, 7});
		comparedSpace = ParameterSpaceMapperTest.buildParameterSpace(new Integer[] {5, 6});
	}
	//TODO hibernate: determine how to retrieve templated classes (e.g., Scenario<R, I> 
	//TODO iterate through ScenarioAnalyzers
 	@Test
	public void verifyReplicatorRetrievesTwoScenariosSuccessfully() throws Exception
	{
		scenario1 = replicator.getBaselineScenario(scenarioId1); 
		scenario2 = replicator.getTestScenario(scenarioId2);
		assertEquals(scenarioId1, scenario1.getId());
		assertEquals(scenarioId2, scenario2.getId());
	}
//	@Test
	public void verifyRecordCountsMatch() throws Exception
	{
		//FIXME not sure why commented...
		analyzer = new MinimalScenarioLogAnalyzer<String, PersistentInput>(); 
		ComparisonResult comparison = analyzer.compare(scenario1, scenario2); 
		assertEquals("MinimalScenarioLogAnalyzer.compare:  Scenario Id "+scenarioId1+" and Scenario Id "+scenarioId2+" return equal results:\n"+
				"Record counts match: 3  Custom data match: .", comparison.getDetails());
	}
 	@Test
	public void verifyTwoScenarioSetParameterSpacesMatchIfTheirParameterSpacesCoverSamePointsButInDifferentOrder() throws Exception
	{
		buildComparedSpaceWithSameParametersInDifferentOrder(); 
		ScenarioSet<String, PersistentInput> baselineScenarioSet = buildScenarioSet("baselineScenarioSet", buildPersistentParameterSpace("baselineSpace.xml", baselineSpace)); 
		ScenarioSet<String, PersistentInput> comparedScenarioSet = buildScenarioSet("comparedScenarioSet", buildPersistentParameterSpace("comparedSpace.xml", comparedSpace));
		ParameterSpaceMapper<String, PersistentInput> mapper = new ParameterSpaceMapper<String, PersistentInput>(baselineScenarioSet, comparedScenarioSet); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_MATCHES_BASELINE, mapper.getOverlap());
	}
    @Test
	public void verifyTwoScenarioSetParameterSpacesOverlap() throws Exception
	{
		ScenarioSet<String, PersistentInput> baselineScenarioSet = buildScenarioSet("baselineScenarioSet", buildPersistentParameterSpace("baselineSpace.xml", baselineSpace)); 
		ScenarioSet<String, PersistentInput> comparedScenarioSet = buildScenarioSet("comparedScenarioSet", buildPersistentParameterSpace("comparedSpace.xml", comparedSpace));
		ParameterSpaceMapper<String, PersistentInput> mapper = new ParameterSpaceMapper<String, PersistentInput>(baselineScenarioSet, comparedScenarioSet); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_PARTIALLY_OVERLAPS_BASELINE, mapper.getOverlap());
	}
	public void buildComparedSpaceWithSameParametersInDifferentOrder()
	throws InvalidStaticParameterException, InvalidPropertiesException
	{
		comparedSpace = new ParameterSpace(); 
		comparedSpace.addParameter(ParameterSpaceMapperTest.getStringParameter()); 
		comparedSpace.addParameter(ParameterSpaceMapperTest.getIntParameter(new Integer[] {6, 7}));
	}
	public void buildIteratorWithSameParametersInDifferentOrder()
			throws InvalidStaticParameterException,
			UnsupportedParameterException
	{
		List<Parameter<?>> list = test.buildParameterList();
		Parameter<?> parm = list.get(0);
		list.remove(0);
		list.add(parm); 
		iterator = new ParameterIterator(list);
	}
	public ScenarioSet<String, PersistentInput> buildScenarioSet(String name, ParameterSpace space) throws ScenarioException
	{
		tx = getTx(); 
		ScenarioSet<String, PersistentInput> scenarioSet = new ScenarioSet<String, PersistentInput>(false); 
		scenarioSet.setName(name); 
		scenarioSet.setModel(model);
		scenarioSet.setInput(persistentInput);
		scenarioSet.setParameterSpace(space); 
		scenarioSet.setCalendar(MockClock.getCalendar()); 
		scenarioSet.buildParameterSpace(); 
		session.save(scenarioSet.getParameterSpace());
		tx.commit(); 
		tx = getTx(); 
		scenarioSet.buildScenarios(); 
		session.save(scenarioSet); 
		tx.commit(); 
		tx=getTx();
		scenarioSet.runScenarios(); 
		session.update(scenarioSet); 
		tx.commit(); 
//		int id = scenarioSet.getId(); 
		System.out.println("scenarioSet id: "+scenarioSet.getId());
//		tx=getTx();
//		ScenarioSet<String, PersistentInput> set = (ScenarioSet) session.get(ScenarioSet.class, id); 
//		assertEquals("test scenario set", set.getName());
		return scenarioSet; 
	}
	private void checkScenario(Scenario<String, PersistentInput> newScenario) throws Exception
	{
		assertEquals("test 2", newScenario.getName()); 
		assertEquals("Test 1", newScenario.getModel().getName()); 
		filename = newScenario.getLog().getFilename();
		assertEquals(builder.getRootDirectoryName()+Constants.SLASH+"logs"+Constants.SLASH+"Scenario_"+newScenario.getId()+"_2005_10_15__12_00_14PM_test 2.log", filename ); 
		assertEquals(4, newScenario.getLog().getRecordCount()); 
		assertEquals("some custom data=23, another=true, last=random string", newScenario.getLog().getCustomData());
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string tom, int 6, boolean true\n"+
				"output:  23", result.getResult().toString()); 
		File file = new File(filename);
		if (file.exists()) file.delete();
	}
	private void buildScenario(String which) throws Exception, ScenarioException
	{
		builder = new OutputFileBuilder("scenario_root"); 
		createModel();  // different model for each scenario.  if moved to setUp, one model used for both scenarios
		tx = getTx();
		scenario = new SimpleScenario<String, PersistentInput>(); 
		scenario.setName("test 2"); 
		scenario.setCalendar(MockClock.getCalendar()); 
		scenario.setModel(model); 
		Input persistentInput = new PersistentInput(); 
		persistentInput.setFilename(TestingFileBuilder.buildPersistentInput());
		session.save(persistentInput);
		scenario.setInput(persistentInput);
		scenario.setParameterPoint(iterator.next());  //TODO persist this 
		scenario.setOutputFileBuilder(builder); 
		session.save(scenario); 
		System.out.println("ScenarioSetReplicatorPersistenceTest.buildScenario: id "+scenario.getId()); 
		result = scenario.run(); 
		tx.commit();
	}

}
