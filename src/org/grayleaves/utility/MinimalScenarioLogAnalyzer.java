package org.grayleaves.utility;



public class MinimalScenarioLogAnalyzer<R, I> extends AbstractScenarioAnalyzer<R, I> 
{
//	private static Logger logger = Logger.getLogger(MinimalScenarioLogAnalyzer.class);
	private static final String CUSTOM_DATA = "Custom data";
	private static final String RECORD_COUNT = "Record count";
	private static final String RECORD_COUNTS = RECORD_COUNT+"s";
	protected void checkMatches()
	{
		matchRecordCount();
		matchCustomData();
		
	}
	protected void appendDetails()
	{
		details.append(match(RECORD_COUNTS, (new Integer(this.testScenario.getLog().getRecordCount())).toString()));
		details.append(match(CUSTOM_DATA, this.testScenario.getLog().getCustomData()));
	}
	private boolean matchCustomData()
	{
		if (!(baselineScenario.getLog().getCustomData().equals(testScenario.getLog().getCustomData())))
		{
			mismatches.add(mismatch(CUSTOM_DATA, CUSTOM_DATA, this.testScenario.getLog().getCustomData(), this.baselineScenario.getLog().getCustomData()));
			match = false;
		}
		else
		{
			matches.add(match(CUSTOM_DATA, this.testScenario.getLog().getCustomData()));
		}
		return match; 
	}
	private boolean matchRecordCount()
	{
		if (!(baselineScenario.getLog().getRecordCount() == testScenario.getLog().getRecordCount())) 
		{
			mismatches.add(mismatch(RECORD_COUNT, RECORD_COUNTS, (new Integer(this.testScenario.getLog().getRecordCount())).toString(), (new Integer(this.baselineScenario.getLog().getRecordCount())).toString()));
			match = false;
		}
		else
		{
			matches.add(match(RECORD_COUNTS, (new Integer(this.testScenario.getLog().getRecordCount())).toString()));
		}
		return match; 
	}
}
