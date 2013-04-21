package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScenarioAnalyzer<R, I> implements ScenarioAnalyzer<R, I>, Cloneable
{

	protected static final String BASELINE_SCENARIO = "Baseline scenario ";
	protected static final String NL = "\n";
	protected String compareMethod = this.getClass().getSimpleName()+".compare:  ";
	protected static final String ID_LEFT = "(id ";
	protected static final String ID_RIGHT = ")";
	protected static final String TEST_SCENARIO = "Test scenario ";
	protected static final String PERIOD_NL = ".\n";
	protected static final String NOT_REPLICATED = " does not replicate ";
	private static final String NULL_SCENARIO = "one scenario is null.";
	protected static final String REASON = "Reason:  ";
	private static final String REPLICATES = " replicates ";
	private static final String MATCH = " match";
	private static final String DONT_MATCH = " do not match";
	protected static final String COLON = ":  ";
	private static final String SPACE = " ";
	private static final String SPACES = "  ";
	protected boolean match = true;
	protected Scenario<R, I> baselineScenario;
	protected Scenario<R, I> testScenario;
	protected ComparisonResult result;
	protected StringBuffer details;
	protected List<String> matches;
	protected List<String> mismatches;

	@Override
	public ComparisonResult compare(Scenario<R, I> baselineScenario, Scenario<R, I> testScenario)
	{
		this.testScenario = testScenario;
		this.baselineScenario = baselineScenario; 
		details = new StringBuffer(); 
		result = new ComparisonResult(); 
		try
		{
			validateScenarios();		
			buildComparisonLists(); 
			checkMatches(); 
			checkMatch(); 
		} 
		catch (AnalyzerException e)
		{
			match = false; 
			details.append(e.getMessage()); 
		}
		result.setMatch(match); 
		result.setDetails(details.toString()); 
		return result;
	}
	protected abstract void checkMatches(); 
	protected abstract void appendDetails();
	protected void validateScenarios() throws AnalyzerException
	{
		boolean isNull = false; 
		String baseId = "";  
		String testId = "";
		if (this.baselineScenario == null)
		{
			baseId = "null"; 
			isNull = true;
		}
		else
		{
			baseId = ""+this.baselineScenario.getId(); 
		}
		if (this.testScenario == null)
		{
			testId = "null"; 
			isNull = true;
		}
		else
		{
			testId = ""+this.testScenario.getId();
		}
		if (isNull) throw new AnalyzerException(compareMethod+TEST_SCENARIO+ID_LEFT+testId+ID_RIGHT+NOT_REPLICATED+BASELINE_SCENARIO+
				ID_LEFT+baseId+ID_RIGHT+PERIOD_NL+REASON+NULL_SCENARIO+NL);
	}
	protected void buildComparisonLists()
	{
		matches = new ArrayList<String>(); 
		mismatches = new ArrayList<String>();
		// parent iterates through an enum, which returns method name, singular, plural 
	}
	protected void appendTestScenario(StringBuffer sb)
	{
		sb.append(TEST_SCENARIO+ID_LEFT);
		sb.append(this.testScenario.getId());
		sb.append(ID_RIGHT);
	}
	protected void checkMatch()
	{
		if (match)
		{
			header(REPLICATES);
			appendDetails();
		}
		else 
		{
			header(NOT_REPLICATED);
			for (String mismatch : mismatches)
			{
				details.append(mismatch);
			}
			if (matches.size() > 0)
			{
				details.append(NL);
				for (String match : matches)
				{
					details.append(match);
				}
			}
		}
	}
	protected void appendBaselineScenario(StringBuffer sb)
	{
		sb.append(BASELINE_SCENARIO+ID_LEFT);
		sb.append(this.baselineScenario.getId());
		sb.append(ID_RIGHT);
	}
	protected void header(String outcome)
	{
		details.append(compareMethod);
		appendTestScenario(details);
		details.append(outcome);
		appendBaselineScenario(details);
		details.append(PERIOD_NL);
	}
	protected String match(String plural, String testData)
	{
		StringBuffer details = new StringBuffer();
		details.append(plural+MATCH+COLON);
		details.append(testData+NL);
		return details.toString();
	}
	protected String mismatch(String singular, String plural, String testData,
			String baselineData)
	{
		StringBuffer details = new StringBuffer(); 
		details.append(REASON+plural+DONT_MATCH+PERIOD_NL);
		appendTestScenario(details);
		details.append(SPACE+singular+COLON);
		details.append(testData+SPACES);
		appendBaselineScenario(details);
		details.append(SPACE+singular+COLON);
		details.append(baselineData+NL);
		return details.toString(); 
	}
	@SuppressWarnings("unchecked")
	@Override
	public ScenarioAnalyzer<R, I> newCopy()
	{
		ScenarioAnalyzer<R, I> analyzer = null; 
		try
		{
			analyzer = (ScenarioAnalyzer<R, I>) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return analyzer; 
	}
}
