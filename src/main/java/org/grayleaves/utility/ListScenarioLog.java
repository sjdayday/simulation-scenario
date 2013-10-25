/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.util.ArrayList;
import java.util.List;

public class ListScenarioLog<R, I> extends ScenarioLog<R, I> 
{


	private static final String METHOD = "utility.ListScenarioLog:  ";
	private static final String SPACE = " ";
	public ListScenarioLog()
	{
	}

	public ListScenarioLog(Scenario<R, I> scenario) throws ScenarioException
	{
		
		super(scenario);

	}
	@Override
	protected void buildFileName() throws ScenarioException
	{
	}
	@Override
	protected void setUpFile() throws ScenarioException
	{
		records = new ArrayList<String>();
	}
    @Override
    protected void logError(String record)
    {
    	records.add("ERROR "+METHOD+record+SPACE);
    }
    @Override
    protected void logInfo(String record)
    {
    	records.add("INFO "+METHOD+record+SPACE);
    }
    @Override
    public void close()
    {
    }
    @Override
    public List<String> getRecords()
    {
    	return records;
    }
}
