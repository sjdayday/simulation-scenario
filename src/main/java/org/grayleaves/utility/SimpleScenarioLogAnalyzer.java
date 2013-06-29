/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;

public class SimpleScenarioLogAnalyzer<R, I> extends AbstractScenarioAnalyzer<R, I> 
{
//	private static Logger logger = Logger.getLogger(SimpleScenarioLogAnalyzer.class);
	private static final String LOG_FILE = "Log file";
	private static final String LOG_FILES = LOG_FILE+"s";
	private static final String DOES_NOT_EXIST = "does not exist or could not be read.";
	private static final String EXISTS = "exists.";
	private static final String RECORD_COUNT = "Record count";
	private static final String RECORD_COUNTS = RECORD_COUNT+"s";
	private static final String IS_NULL = "is null.";
	private static final String MAY_EXIST = "may exist.";
	private static final String NO_DIFFERENCES = "no differences";
	private static final String LOG_RECORD = "Log record";
	private static final String LOG_RECORDS = LOG_RECORD+"s";
	private static final String DETAILS_BELOW = "more details available.";
	private static final String DIFFERS_FROM = "differs from";
	private static final String RECORD = "record ";
	private List<String> baselineRecords; 
	private List<String> testRecords; 
	
	@Override
	protected void checkMatches() 
	{
		matchScenarioLogs();
	}

	@Override
	protected void appendDetails() 
	{
		details.append(match(RECORD_COUNTS, (new Integer(this.testScenario.getLog().getRecordCount())).toString()));
		details.append(match(LOG_RECORDS, NO_DIFFERENCES));

	}
	private boolean matchScenarioLogs()
	{
		if (eitherScenarioLogFileIsNull())
		{
			match = false;
			return match; 
		}
		testRecords = getScenarioLogRecords(testScenario);
		boolean testOK = (testRecords != null); 
		baselineRecords = getScenarioLogRecords(baselineScenario);
		boolean baselineOK = (baselineRecords != null);
		
		if (testOK && baselineOK)
		{
			matchRecordCounts(); 
			matchRecords(); 
		}
		else
		{
			mismatches.add(mismatch(LOG_FILE, LOG_FILES, (testOK ? EXISTS : DOES_NOT_EXIST), (baselineOK ? EXISTS : DOES_NOT_EXIST)));
			match = false;
		}
		return match; 
	}


	private boolean eitherScenarioLogFileIsNull() 
	{
		boolean isNull = false; 
		if ((testScenario.getLog() == null) || (baselineScenario.getLog() == null))
		{
			mismatches.add(mismatch(LOG_FILE, LOG_FILES, ((testScenario.getLog() == null) ? IS_NULL : MAY_EXIST), ((baselineScenario.getLog() == null) ? IS_NULL : MAY_EXIST)));
			isNull = true; 
		}
		
		return isNull;
	}

	private boolean matchRecords() 
	{
		
		diff_match_patch dmp = new diff_match_patch(new StandardBreakScorer()); 
		LinkedList<Diff> diffs = null; 
		StringBuffer sb = new StringBuffer(); 
		DiffFormatter formatter = new DiffFormatter(); 
		for (int i = 0; i < baselineRecords.size(); i++)
		{
			diffs = dmp.diff_main(baselineRecords.get(i), testRecords.get(i));
			if (!((diffs.size() == 1) && (diffs.get(0).operation.equals(Operation.EQUAL))))
			{
				match = false;
				sb.append(RECORD+(i+1)+COLON);
				sb.append(formatter.format(diffs)); 
			}
		}
		if (match)
		{
			matches.add(match(LOG_RECORDS, NO_DIFFERENCES));
		}
		else
		{
			mismatches.add(mismatch(LOG_RECORD, LOG_RECORDS, DIFFERS_FROM, DETAILS_BELOW));	
		}
		result.setMoreDetails(sb.toString());
		return match; 
	}

	private boolean matchRecordCounts()  
	{	
		if ((baselineRecords.size() != testRecords.size()))
		{
			mismatches.add(mismatch(RECORD_COUNT, RECORD_COUNTS, ((Integer) testRecords.size()).toString(), ((Integer) baselineRecords.size()).toString()));
			match = false;
		}
		else
		{
			matches.add(match(RECORD_COUNTS, ((Integer) testRecords.size()).toString()));
		}
		return match; 
	}
	private List<String> getScenarioLogRecords(Scenario<R, I> scenario) 
	{
		List<String> records = new ArrayList<String>();  
		try
		{
			List<String> rawRecords = scenario.getLog().getRecords();
			for (int i = 1; i < scenario.getLog().getRecordCount()+1; i++) 
			{
				records.add(rawRecords.get(i));
			}
		}
		catch (ScenarioException e)
		{
			result.setMoreDetails(e.getMessage());
			records = null; 
		}
		return records;
	}

}
