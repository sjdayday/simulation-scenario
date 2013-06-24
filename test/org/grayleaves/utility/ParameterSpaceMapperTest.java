package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.InvalidPropertiesException;
import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.ParameterSpaceMapper;
import org.grayleaves.utility.ParameterSpaceMapperEnum;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioSet;
import org.grayleaves.utility.SimpleScenario;
import org.grayleaves.utility.StaticParameterUpdater;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ParameterSpaceMapperTest
{
	private ParameterSpace space;
	private ParameterSpace comparedSpace;
	private ParameterSpaceMapper<String, TestingInput> mapper;
	private int scenarioId;
	private ScenarioSet<String, TestingInput> scenarioSet;
	private ScenarioSet<String, TestingInput> comparedScenarioSet; 
	@BeforeClass
	public static void setUpLog4J() throws Exception
	{
	}
	@Before
	public void setUp() throws Exception
	{
		space = ParameterSpaceTest.buildParameterSpace();  
		comparedSpace = ParameterSpaceTest.buildParameterSpace();
		scenarioId = 0; 
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	@Test
	public void verifyArgumentsCantBeNull() throws Exception
	{
		try
		{
			mapper = new ParameterSpaceMapper<String, TestingInput>(space, null);
			fail("can't be null"); 
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("ParameterSpaceMapper.constructor:  Neither argument can be null.", e.getMessage()); 
		}
		try
		{
			mapper = new ParameterSpaceMapper<String, TestingInput>(new ScenarioSet<String, TestingInput>(), null);
			fail("scenarioSet can't be null"); 
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("ParameterSpaceMapper.constructor:  Neither argument can be null.", e.getMessage()); 
		}
	}
	@Test
	public void verifyMapsAllPointsOfSameParameterSpaceComparedToItself() throws Exception
	{
		mapper = new ParameterSpaceMapper<String, TestingInput>(space, comparedSpace); 
		assertEquals("ParameterPoint\tPresent in Space\tPresent in Compared Space\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=0\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=10\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n", mapper.printMapping()); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_MATCHES_BASELINE, mapper.getOverlap()); 
	}
	@Test
	public void verifyMappingIsUnionOfDifferentParameterSpaces() throws Exception
	{
		comparedSpace.getParameters().remove(2);
		comparedSpace.addParameter(new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 20, 40, 10, 20));
		mapper = new ParameterSpaceMapper<String, TestingInput>(space, comparedSpace); 
		assertEquals("ParameterPoint\tPresent in Space\tPresent in Compared Space\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=0\tYes\tNo\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=10\tYes\tNo\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=20\tYes\tYes\n" +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=30\tYes\tYes\n" +
				"Integer Name=6, String Public Name=fred, Int2 Public Name=40\tNo\tYes\n"  +
				"Integer Name=6, String Public Name=sam, Int2 Public Name=40\tNo\tYes\n"  +
				"Integer Name=7, String Public Name=fred, Int2 Public Name=40\tNo\tYes\n"  +
				"Integer Name=7, String Public Name=sam, Int2 Public Name=40\tNo\tYes\n"  +
				"Integer Name=8, String Public Name=fred, Int2 Public Name=40\tNo\tYes\n"  +
				"Integer Name=8, String Public Name=sam, Int2 Public Name=40\tNo\tYes\n", mapper.printMapping()); 
		assertEquals(ParameterSpaceMapperEnum.COMPARED_PARTIALLY_OVERLAPS_BASELINE, mapper.getOverlap());
		// use case for intersection of two spaces?  
	}
	@Test
	public void verifyDisjointParameterSpaces() throws Exception
	{
		comparedSpace.getParameters().remove(2);
		mapper = new ParameterSpaceMapper<String, TestingInput>(space, comparedSpace); 
		assertEquals("removing a parameter assures no parameterpoint will match",ParameterSpaceMapperEnum.COMPARED_IS_DISJOINT_WITH_BASELINE, mapper.getOverlap());

	}
	@Test
	public void verifySmallerComparedSpaceIsSubsetOfBaseline() throws Exception
	{
		comparedSpace.getParameters().remove(2);
		comparedSpace.addParameter(new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 20, 10, 20));
		mapper = new ParameterSpaceMapper<String, TestingInput>(space, comparedSpace); 
		assertEquals("compared range parameter goes to 20, not 30, thus subset",ParameterSpaceMapperEnum.COMPARED_IS_SUBSET_OF_BASELINE, mapper.getOverlap());
	}
	@Test
	public void verifySmallerBaselineSpaceIsSubsetOfComparedSpace() throws Exception
	{
		space.getParameters().remove(2);
		space.addParameter(new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 20, 10, 20));
		mapper = new ParameterSpaceMapper<String, TestingInput>(space, comparedSpace); 
		assertEquals("compared range parameter goes to 20, not 30, thus subset",ParameterSpaceMapperEnum.BASELINE_IS_SUBSET_OF_COMPARED, mapper.getOverlap());
	}
	@Test
	public void verifyMappingForScenarioSets() throws Exception
	{
		buildScenarioSets(); 
		mapper = new ParameterSpaceMapper<String, TestingInput>(scenarioSet, comparedScenarioSet); 
		assertEquals("ParameterPoint\tPresent in Space\tScenarioId\tPresent in Compared Space\tCompared ScenarioId\n" +
				"Integer Name=6, String Public Name=fred\tYes\t1\tYes\t7\n" +
				"Integer Name=6, String Public Name=sam\tYes\t2\tYes\t8\n" +
				"Integer Name=7, String Public Name=fred\tYes\t3\tNo\t-1\n" +
				"Integer Name=7, String Public Name=sam\tYes\t4\tNo\t-1\n" +
				"Integer Name=5, String Public Name=fred\tNo\t-1\tYes\t5\n" +
				"Integer Name=5, String Public Name=sam\tNo\t-1\tYes\t6\n", mapper.printMapping()); 
	}
	@Test
	public void verifyParameterPointListsRetrievedForMatchingOrBaselineOnlyOrComparedOnly() throws Exception
	{
		buildScenarioSets(); 
		mapper = new ParameterSpaceMapper<String, TestingInput>(scenarioSet, comparedScenarioSet); 
		List<ParameterPoint> both = mapper.getParameterPointsMatching(); 
		assertEquals(2, both.size());
		assertEquals("Integer Name=6, String Public Name=fred",both.get(0).verboseToString()); 
		assertEquals("Integer Name=6, String Public Name=sam",both.get(1).verboseToString());
		List<ParameterPoint> baseline = mapper.getParameterPointsBaselineOnly(); 
		assertEquals(2, baseline.size());
		assertEquals("Integer Name=7, String Public Name=fred",baseline.get(0).verboseToString()); 
		assertEquals("Integer Name=7, String Public Name=sam",baseline.get(1).verboseToString());
		List<ParameterPoint> compared = mapper.getParameterPointsComparedOnly(); 
		assertEquals(2, compared.size()); 
		assertEquals("Integer Name=5, String Public Name=fred",compared.get(0).verboseToString()); 
		assertEquals("Integer Name=5, String Public Name=sam",compared.get(1).verboseToString());

	}
	@Test
	public void verifyCanRetrieveMatchingScenario() throws Exception
	{
		buildScenarioSets();
		mapper = new ParameterSpaceMapper<String, TestingInput>(scenarioSet, comparedScenarioSet);
		ParameterPoint point = scenarioSet.getScenarios().get(1).getParameterPoint(); 
		assertEquals("point:  Integer Name=6, String Public Name=sam should find matching scenario",8, mapper.getMatchingScenarioId(point)); 
		point = scenarioSet.getScenarios().get(2).getParameterPoint();
		assertEquals("point:  Integer Name=7, String Public Name=fred should not find matching scenario",-1, mapper.getMatchingScenarioId(point));
//		assertEquals("two points removed during mapping",2,comparedScenarioSet.getScenarios().size());  comparedSingleParameterPoints, not ScenarioSets...
		point = comparedScenarioSet.getScenarios().get(0).getParameterPoint();
		try
		{
			mapper.getMatchingScenarioId(point);
			fail("point:  Integer Name=5, String Public Name=fred -- logic error; this point not in the source scenarioSet (taken from elsewhere, the comparedScenarioSet, in this case -- should throw");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("ParameterSpaceMapper.getMatchingScenarioId:  Probable logic error.   Parameter point:  Integer Name=5, String Public Name=fred not in the source ScenarioSet or ParameterSpace.", e.getMessage());
		}
	}
	public void buildScenarioSets() throws InvalidPropertiesException,
			InvalidStaticParameterException
	{
		space = buildParameterSpace(new Integer[]{6, 7});
		scenarioSet = buildScenarioSet(space); 

		comparedSpace = buildParameterSpace(new Integer[]{5, 6});
		comparedScenarioSet = buildScenarioSet(comparedSpace);
	}
	public ScenarioSet<String, TestingInput>  buildScenarioSet(ParameterSpace space) throws InvalidPropertiesException,
			InvalidStaticParameterException
	{
		ScenarioSet<String, TestingInput> scenarioSet = new ScenarioSet<String, TestingInput>(); 
		List<Scenario<String, TestingInput>> scenarioList = new ArrayList<Scenario<String, TestingInput>>();
		Scenario<String, TestingInput> scenario = null; 
		ParameterIterator iterator = space.iterator(); 
		while (iterator.hasNext())
		{
			scenario = new SimpleScenario<String, TestingInput>();
			scenario.setParameterPoint(iterator.next());
			scenario.setId(++scenarioId);
			scenarioList.add(scenario); 
		}
		scenarioSet.setScenarios(scenarioList); 
		return scenarioSet; 
	}
	public static ParameterSpace buildParameterSpace(Integer[] ints) throws InvalidPropertiesException,
			InvalidStaticParameterException
	{
		ParameterSpace space = new ParameterSpace(); 
		space.loadProperties("org/grayleaves/utility/testing.properties"); 
		space.addParameter(getIntParameter(ints));
		space.addParameter(getStringParameter());
		return space; 
	}
	public static Parameter<?> getIntParameter(Integer[] ints)
			throws InvalidPropertiesException, InvalidStaticParameterException
	{
//		return new ArrayParameter<Integer>("Integer Name", new PropertiesParameterUpdater<Integer>(Integer.class, "integer.property"), ints);
		return new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean"), ints);
	}
	public static Parameter<?> getStringParameter()
			throws InvalidStaticParameterException
	{
		return new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"), new String[]{"fred", "sam"});
	}

//	space.addParameter(new ArrayParameter<Integer>("String Name", new PropertiesParameterUpdater<Integer>(Integer.class, "integer.property"), new Integer[]{6, 7, 8}));
//	space.addParameter(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"), new String[]{"fred", "sam"}));

}
