/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

		package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ScenarioSetReplicator<R, I>
{
	//	private static Logger logger = Logger.getLogger(ScenarioSetReplicator.class);
	private static final String ID_CLOSE = ") ";
	private static final String ID = "(id ";
	private static final String COMPARISON_DETAILS = " comparison details:  ";
	private static final String SUMMARY_HEADER = "org.grayleaves.utility.ScenarioSetReplicator comparison summary:  ";
	private static final String COMPARED = "Compared ";
	private static final String BASELINE = "Baseline ";
	private static final String SCENARIO_SET = "scenario set ";
	private static final String IS_NOT_COMPARABLE = "is not comparable";
	private static final String FOLLOWING_PARAMETER_POINTS = " for the following parameter points";
	private static final String NOT_COMPARABLE_ANY_POINTS = IS_NOT_COMPARABLE+" for any parameter points in ";
	private static final String NOT_COMPARABLE_FOLLOWING_POINTS = IS_NOT_COMPARABLE+FOLLOWING_PARAMETER_POINTS;
	private static final String COMPARABLE = "is comparable for all parameter points in ";
	private static final String NL = "\n";
	private static final String PARTIALLY_COMPARABLE = "is comparable for some but not all parameter points in ";
	private static final String MATCHING_POINTS = "Compared scenario set is comparable for the following parameter points in the Baseline scenario set:  \n";
	private static final String BASELINE_POINTS_ONLY = COMPARED+SCENARIO_SET+NOT_COMPARABLE_FOLLOWING_POINTS+"; they exist in the Baseline scenario set but do not exist in the Compared scenario set:  \n";
	private static final String COMPARED_POINTS_ONLY = COMPARED+SCENARIO_SET+NOT_COMPARABLE_FOLLOWING_POINTS+"; they exist in the Compared scenario set but do not exist in the Baseline scenario set:  \n";
	
	private Session session;
	private Transaction tx;
	private Scenario<R, I> baselineScenario;
	private Scenario<R, I> testScenario;
	private List<ScenarioAnalyzer<R, I>> scenarioAnalyzers;
	private List<ScenarioAnalyzer<R, I>> clonedScenarioAnalyzers;
	private StringBuffer scenarioSetSummaryBuffer;
	private StringBuffer scenarioSetSummaryOverlapBuffer;
	
	public ScenarioSetReplicator()
	{
		scenarioAnalyzers = new ArrayList<ScenarioAnalyzer<R, I>>();
	}
	private void cloneScenarioAnalyzers()
	{
		clonedScenarioAnalyzers = new ArrayList<ScenarioAnalyzer<R, I>>();
		for (ScenarioAnalyzer<R, I> scenarioAnalyzer : scenarioAnalyzers)
		{
			clonedScenarioAnalyzers.add(scenarioAnalyzer.newCopy()); 
		}
	}
	public Scenario<R, I> getBaselineScenario(int baselineScenarioId)
	{
		if (baselineScenario == null)
		{
			baselineScenario = getScenario(baselineScenarioId); 
		}
		return baselineScenario;
	}
	public Scenario<R, I> getTestScenario(int testScenarioId)
	{
		if (testScenario == null)
		{
			testScenario = getScenario(testScenarioId); 
		}
		return testScenario;
	}
	@SuppressWarnings("unchecked")
	private Scenario<R, I> getScenario(int scenarioId)
	{
		Scenario<R, I> scenario = null; 
		tx = getTx(); 
		scenario = (Scenario<R, I>) session.get(Scenario.class, scenarioId); 
		tx.commit();
		return scenario;  
	}
	private Transaction getTx()
	{
		session = HibernateUtil.getSessionFactory().getCurrentSession(); 
		return session.beginTransaction();
	}
	@SuppressWarnings("unchecked")
	public void addScenarioAnalyzer(ScenarioAnalyzerEnum analyzerEnum)
	{
		addScenarioAnalyzer(analyzerEnum.getAnalyzer()); 
	}
	public List<ScenarioAnalyzer<R, I>> getScenarioAnalyzers()
	{
		return scenarioAnalyzers;
	}
	public void addScenarioAnalyzer(ScenarioAnalyzer<R, I> analyzer)
	{
		scenarioAnalyzers.add(analyzer); 
	}
	/**
	 * Compare single Scenarios
	 * @param baselineScenario
	 * @param testScenario
	 * @return
	 */
	public ComparisonResult compare(Scenario<R, I> baselineScenario, Scenario<R, I> testScenario)
	{
		ComparisonResult overallResult = new ComparisonResult(); 
		StringBuffer sb = new StringBuffer(); 
		ComparisonResult result = null; 
		boolean match = true; 
		for (ScenarioAnalyzer<R, I> analyzer : getClonedScenarioAnalyzers())
		{
			sb.append(analyzer.getClass().getName());
			sb.append(COMPARISON_DETAILS); 
			result = analyzer.compare(baselineScenario, testScenario); 
			sb.append(result.getDetails()+"\n");
			if (!result.isMatch()) match = false; 
		}
		overallResult.setDetails(sb.toString());
		overallResult.setMatch(match); 
		cloneScenarioAnalyzers();		
		return overallResult;
	}
	private List<ScenarioAnalyzer<R, I>> getClonedScenarioAnalyzers()
	{
		cloneScenarioAnalyzers();
		return clonedScenarioAnalyzers;
	}
	public ScenarioSetComparisonResult compare(ScenarioSet<R, I> baselineScenarioSet, ScenarioSet<R, I> comparedScenarioSet)
	{
		ScenarioSetComparisonResult scenarioSetComparisonResult = new ScenarioSetComparisonResult();
		scenarioSetSummaryBuffer = new StringBuffer(); 
		scenarioSetSummaryOverlapBuffer = new StringBuffer(); 
		int comparedScenarioId = -1; 
		ParameterSpaceMapper<R, I> mapper = new ParameterSpaceMapper<R, I>(baselineScenarioSet, comparedScenarioSet); 
		buildSummary(mapper, baselineScenarioSet.getId(), comparedScenarioSet.getId());
		for (Scenario<R, I> baselineScenario : baselineScenarioSet.getScenarios())
		{
			comparedScenarioId = mapper.getMatchingScenarioId(baselineScenario.getParameterPoint()); 
			if (comparedScenarioId != -1)
			{
				scenarioSetComparisonResult.addComparisonResult(compare(baselineScenario, comparedScenarioSet.getScenario(comparedScenarioId)));  
			}
		}
		scenarioSetComparisonResult.setSummary(scenarioSetSummaryBuffer.toString()); 
		scenarioSetComparisonResult.setOverlap(mapper.getOverlap()); 
		scenarioSetComparisonResult.setOverlapDetails(scenarioSetSummaryOverlapBuffer.toString()); 
		return scenarioSetComparisonResult;
	}
	private void buildSummary(ParameterSpaceMapper<R, I> mapper, int baselineId, int compareId)
	{
		scenarioSetSummaryBuffer.append(SUMMARY_HEADER); 
		switch (mapper.getOverlap())
		{
		case COMPARED_IS_DISJOINT_WITH_BASELINE: 
		{
			buildSummaryText(NOT_COMPARABLE_ANY_POINTS, baselineId, compareId); 
			buildOverlapDetailsComparedOnly(mapper);
			buildOverlapDetailsBaselineOnly(mapper); 

		}
		break;
		case COMPARED_MATCHES_BASELINE: 
		{
			buildSummaryText(COMPARABLE, baselineId, compareId);		
			buildOverlapDetailsMatching(mapper);
		}
		break;
		case COMPARED_PARTIALLY_OVERLAPS_BASELINE: 
		{
			buildSummaryText(PARTIALLY_COMPARABLE, baselineId, compareId);		
			buildOverlapDetailsMatching(mapper);
			buildOverlapDetailsComparedOnly(mapper);
			buildOverlapDetailsBaselineOnly(mapper); 
		}
		break;
		default:
			break;
		}
	}
	private void buildOverlapDetailsMatching(ParameterSpaceMapper<R, I> mapper)
	{
		scenarioSetSummaryOverlapBuffer.append(MATCHING_POINTS);
		appendParameterPointList(mapper.getParameterPointsMatching());
	}
	private void buildOverlapDetailsBaselineOnly(ParameterSpaceMapper<R, I> mapper)
	{
		scenarioSetSummaryOverlapBuffer.append(BASELINE_POINTS_ONLY); 
		appendParameterPointList(mapper.getParameterPointsBaselineOnly());
	}
	private void buildOverlapDetailsComparedOnly(ParameterSpaceMapper<R, I> mapper)
	{
		scenarioSetSummaryOverlapBuffer.append(COMPARED_POINTS_ONLY);
		appendParameterPointList(mapper.getParameterPointsComparedOnly());
	}
	public void appendParameterPointList(List<ParameterPoint> list)
	{
		for (ParameterPoint parameterPoint : list)
		{
			scenarioSetSummaryOverlapBuffer.append(" ");
			scenarioSetSummaryOverlapBuffer.append(parameterPoint.verboseToString());
			scenarioSetSummaryOverlapBuffer.append("\n");
		}
	}
	public void buildSummaryText(String comparison, int baselineId, int compareId)
	{
		scenarioSetSummaryBuffer.append(COMPARED+SCENARIO_SET); 
		scenarioSetSummaryBuffer.append(ID);
		scenarioSetSummaryBuffer.append(compareId);
		scenarioSetSummaryBuffer.append(ID_CLOSE);
		scenarioSetSummaryBuffer.append(comparison);
		scenarioSetSummaryBuffer.append(BASELINE+SCENARIO_SET);
		scenarioSetSummaryBuffer.append(ID);
		scenarioSetSummaryBuffer.append(baselineId);
		scenarioSetSummaryBuffer.append(ID_CLOSE);
		scenarioSetSummaryBuffer.append(NL);
	}
}
