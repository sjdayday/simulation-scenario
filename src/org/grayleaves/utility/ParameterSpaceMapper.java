package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps the {@link ParameterPoint ParameterPoints} of one {@link org.grayleaves.utility.ParameterSpace} 
 * to the equivalent ParameterPoints of another ParameterSpace.  
 * <p>
 * Given the {@link org.grayleaves.utility.Scenario Scenarios} in one {@link org.grayleaves.utility.ScenarioSet ScenarioSet}, 
 * can also be used to find the Scenarios in another ScenarioSet that were generated with the same {@link org.grayleaves.utility.ParameterPoint ParameterPoints}.
 * <p>
 * Initial implementation assumes the underlying Models and Parameters are the same; only the ParameterSpaces may be different.   
 * <p>
 * In the future, this would be extended to accommodate a Mapping that enables two different Models with different Parameters to be mapped to one another
 * @author stevedoubleday
 *
 */

public class ParameterSpaceMapper<R, I> 
{

	private static final String NOT_IN_THE_SOURCE_SCENARIO_SET_OR_PARAMETER_SPACE = " not in the source ScenarioSet or ParameterSpace.";
	private static final String PROBABLE_LOGIC_ERROR_PARAMETER_POINT = "Probable logic error.   Parameter point:  ";
	private static final String GET_MATCHING_SCENARIO_ID = "getMatchingScenarioId:  ";
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String PRINT_PARAMETER_POINT_HEADER = "ParameterPoint\tPresent in Space\tPresent in Compared Space\n";
	private static final String PRINT_SCENARIO_HEADER = "ParameterPoint\tPresent in Space\tScenarioId\tPresent in Compared Space\tCompared ScenarioId\n";
	private static final String NEITHER_ARGUMENT_CAN_BE_NULL = "Neither argument can be null.";
	private static final String CONSTRUCTOR = "constructor:  ";
	private static final String PARAMETER_SPACE_MAPPER = "ParameterSpaceMapper.";
	private static final char TAB = '\t';
	private static final char NL = '\n';
	private ParameterSpace comparedSpace;
	private ParameterSpace space;
	private List<ParameterPointMapping> mappings;
	private ScenarioSet<R, I> comparedScenarioSet;
	private ScenarioSet<R, I> scenarioSet;
	private List<SingleParameterPoint> sourceSingleParameterPoints;
	private List<SingleParameterPoint> comparedSingleParameterPoints;
	boolean scenarioFormat; 
	
	public ParameterSpaceMapper(ParameterSpace space,ParameterSpace comparedSpace)
	{
		scenarioFormat = false;
		this.space = space;
		this.comparedSpace = comparedSpace;
		verifyNotNull(); 
		buildParameterPointMappings();
	}
	public ParameterSpaceMapper(ScenarioSet<R, I> scenarioSet, ScenarioSet<R, I> comparedScenarioSet)
	{
		scenarioFormat = true; 
		this.scenarioSet = scenarioSet; 
		this.comparedScenarioSet = comparedScenarioSet; 
		verifyNotNull(); 
		buildScenarioSetMappings();
	}
	private void verifyNotNull()
	{
		if (scenarioFormat)
		{
			if ((scenarioSet == null) || (comparedScenarioSet == null))
				throw new IllegalArgumentException(PARAMETER_SPACE_MAPPER+CONSTRUCTOR+NEITHER_ARGUMENT_CAN_BE_NULL);
		}
		else
		{
			if ((space == null) || (comparedSpace == null))
				throw new IllegalArgumentException(PARAMETER_SPACE_MAPPER+CONSTRUCTOR+NEITHER_ARGUMENT_CAN_BE_NULL);
		}
	}
	private void buildParameterPointMappings()
	{
		sourceSingleParameterPoints = buildSingleParameterPointListsFromParameterSpace(space);
		comparedSingleParameterPoints = buildSingleParameterPointListsFromParameterSpace(comparedSpace);
		buildMappings(); 
	}
	private void buildScenarioSetMappings()
	{
		sourceSingleParameterPoints = buildSingleParameterPointListsFromScenarioSet(scenarioSet.getScenarios());
		comparedSingleParameterPoints = buildSingleParameterPointListsFromScenarioSet(comparedScenarioSet.getScenarios()); 
		buildMappings();
	}
	public void buildMappings()
	{
		mappings = new ArrayList<ParameterPointMapping>(); 
		for (SingleParameterPoint singleParameterPoint : sourceSingleParameterPoints)
		{
			mappings.add(buildSingleParameterPointMapping(singleParameterPoint)); 
		}
		addUnmatchedSingleParameterPointsToMappings();
	}
	private ParameterPointMapping buildSingleParameterPointMapping(SingleParameterPoint singleParameterPoint)
	{
		ParameterPointMapping mapping = null;
		boolean match = false; 
		int comparedScenarioId = -1; 
		for (int i = 0; i < comparedSingleParameterPoints.size(); i++)
		{
			if (singleParameterPoint.getParameterPoint().equals(comparedSingleParameterPoints.get(i).getParameterPoint()))
			{
				match = true; 
				comparedScenarioId = comparedSingleParameterPoints.get(i).getId();
				mapping = new ParameterPointMapping(singleParameterPoint.getParameterPoint(), true, singleParameterPoint.getId(), true, comparedScenarioId);
				comparedSingleParameterPoints.remove(i); 
				break;
			}
		}
		if (!match) mapping = new ParameterPointMapping(singleParameterPoint.getParameterPoint(), true, singleParameterPoint.getId(), false, -1);
		return mapping;
	}
	private void addUnmatchedSingleParameterPointsToMappings()
	{
		for (SingleParameterPoint singleParameterPoint : comparedSingleParameterPoints)
		{
			mappings.add(new ParameterPointMapping(singleParameterPoint.getParameterPoint(), false, -1, true, singleParameterPoint.getId()));
		}
	}
	private List<SingleParameterPoint> buildSingleParameterPointListsFromScenarioSet(List<Scenario<R, I>> scenarios)
	{
		List<SingleParameterPoint> singleParameterPoints = new ArrayList<SingleParameterPoint>(); 
		for (Scenario<R, I> scenario : scenarios)
		{
			singleParameterPoints.add(scenario);
		}
		return singleParameterPoints; 
	}
	public List<SingleParameterPoint> buildSingleParameterPointListsFromParameterSpace(ParameterSpace parameterSpace)
	{
		ParameterIterator iterator = parameterSpace.iterator(); 
		List<SingleParameterPoint> singleParameterPoints = new ArrayList<SingleParameterPoint>(); 
		while (iterator.hasNext())
		{
			singleParameterPoints.add(iterator.next());
		}
		return singleParameterPoints; 
	}
	public String printMapping()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(scenarioFormat ? PRINT_SCENARIO_HEADER : PRINT_PARAMETER_POINT_HEADER); 
		for (ParameterPointMapping mapping : mappings)
		{	
			sb.append(mapping.parameterPoint.verboseToString());
			sb.append(TAB); 
			sb.append(mapping.spaceHasPoint ? YES : NO); 
			sb.append(TAB); 
			if (scenarioFormat)
			{
				sb.append(mapping.scenarioId);
				sb.append(TAB); 	
			}
			sb.append(mapping.comparedSpaceHasPoint ? YES : NO);
			if (scenarioFormat)
			{
				sb.append(TAB); 
				sb.append(mapping.comparedScenarioId);
			}
			sb.append(NL); 	
		}
		return sb.toString();
	}
	public int getMatchingScenarioId(ParameterPoint point)
	{
		for (ParameterPointMapping mapping : mappings)
		{
			if (mapping.parameterPoint.equals(point) && (mapping.spaceHasPoint))
			{
				return mapping.comparedScenarioId;
			}
		}
		throw new IllegalArgumentException(PARAMETER_SPACE_MAPPER+GET_MATCHING_SCENARIO_ID+PROBABLE_LOGIC_ERROR_PARAMETER_POINT+point.verboseToString()+NOT_IN_THE_SOURCE_SCENARIO_SET_OR_PARAMETER_SPACE);
	}
	public ParameterSpaceMapperEnum getOverlap()
	{
		boolean match = true;
		boolean baselineSuperset = true;
		boolean comparedSuperset = true;
		boolean disjoint = true; 
		for (ParameterPointMapping mapping : mappings)
		{
			baselineSuperset = baselineSuperset && mapping.spaceHasPoint; 
			comparedSuperset = comparedSuperset && mapping.comparedSpaceHasPoint; 
			match = match && (baselineSuperset && comparedSuperset); 
			disjoint = disjoint && (!mapping.spaceHasPoint == mapping.comparedSpaceHasPoint );
		}
		if (match) return ParameterSpaceMapperEnum.COMPARED_MATCHES_BASELINE; 
		else if (baselineSuperset) return ParameterSpaceMapperEnum.COMPARED_IS_SUBSET_OF_BASELINE; 
		else if (comparedSuperset) return ParameterSpaceMapperEnum.BASELINE_IS_SUBSET_OF_COMPARED;
		else if (disjoint) return ParameterSpaceMapperEnum.COMPARED_IS_DISJOINT_WITH_BASELINE;
		else  return ParameterSpaceMapperEnum.COMPARED_PARTIALLY_OVERLAPS_BASELINE;
	}
	private class ParameterPointMapping
	{
		public ParameterPoint parameterPoint; 
		public boolean spaceHasPoint = false;
		public boolean comparedSpaceHasPoint = false;
		public int scenarioId = -1; 
		public int comparedScenarioId = -1;  
		
		public ParameterPointMapping(ParameterPoint point, boolean spacePoint, int scenarioId, boolean comparedSpacePoint, int comparedScenarioId )
		{
			this.parameterPoint = point; 
			this.spaceHasPoint = spacePoint; 
			this.comparedSpaceHasPoint = comparedSpacePoint; 
			this.scenarioId = scenarioId; 
			this.comparedScenarioId = comparedScenarioId;
		}
	}
	public List<ParameterPoint> getParameterPointsMatching()
	{
		List<ParameterPoint> list = new ArrayList<ParameterPoint>(); 
		for (ParameterPointMapping mapping : mappings)
		{
			if (mapping.spaceHasPoint && mapping.comparedSpaceHasPoint)
				list.add(mapping.parameterPoint); 
		}
		return list;
	}
	public List<ParameterPoint> getParameterPointsBaselineOnly()
	{
		List<ParameterPoint> list = new ArrayList<ParameterPoint>(); 
		for (ParameterPointMapping mapping : mappings)
		{
			if (mapping.spaceHasPoint && (!mapping.comparedSpaceHasPoint))
				list.add(mapping.parameterPoint); 
		}
		return list;
	}
	public List<ParameterPoint> getParameterPointsComparedOnly()
	{
		List<ParameterPoint> list = new ArrayList<ParameterPoint>(); 
		for (ParameterPointMapping mapping : mappings)
		{
			if ((!mapping.spaceHasPoint) && mapping.comparedSpaceHasPoint)
				list.add(mapping.parameterPoint); 
		}
		return list;
	}
}
