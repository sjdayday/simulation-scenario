/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ScenarioLog<R, I> 
{

	private static final String SPACES = "   ";
	private static final String SPACE = " ";
	private static final String DATE_FORMAT = "yyyy_MM_dd__hh_mm_ssaa";
	private static final String INFO = "INFO";
	private static final String ERROR = "ERROR";
	private String filename; 
	private boolean closed = false; 
	protected List<String> records;
	private Scenario<R, I> scenario;
	private int recordCount;
	private boolean hasTrailer = false;
	private String customData = "";
	private BufferedWriter writer;
	public ScenarioLog()
	{
	}
	public ScenarioLog(Scenario<R, I> scenario) throws ScenarioException
	{
		this.scenario = scenario; 
		buildFileName();
		setUpFile(); 
		logHeader();
		hasTrailer = false;
		recordCount = 0; 
	}
	protected void setUpFile() throws ScenarioException
	{
		writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(getFilename()));
		} catch (IOException e)
		{
			throw new ScenarioException("ScenarioException:  IOException in ScenarioLog.setUpFile:  "+e.getMessage()); 
		} 
	}
	private void logHeader()
	{
		StringBuffer sb = appendScenarioNameAndId(); 
		sb.append(SPACES+"Date/Time:  ");
		sb.append(buildDateTime()); 
		log(sb.toString());
		recordCount = 0; // ignore header
	}
	private StringBuffer appendScenarioNameAndId()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("Scenario: "); 
		sb.append(scenario.getName());
		sb.append(SPACES+"Scenario Id: ");
		sb.append(scenario.getId());
		return sb;
	}
	protected void buildFileName() throws ScenarioException
	{
		String filename = null;
		try
		{
			File file = scenario.getOutputFileBuilder().buildFile("logs", buildDateTime()+"_"+scenario.getName(), "Scenario", "log");
			filename = file.getCanonicalPath();
		}
		catch (OutputFileBuilderException e)
		{
			throw new ScenarioException("ScenarioException.buildFileName:  OutputFileBuilderException while creating ScenarioLog filename: "+e.getMessage());
		}
		catch (IOException e)
		{
			throw new ScenarioException("ScenarioException.buildFileName:  IOException while creating ScenarioLog filename: "+e.getMessage());
		} 
		setFilename(filename);
	}
	private String buildDateTime()
	{
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(scenario.getCalendar().getTime());
	}
	public boolean log(String record)
	{
		return log(record, false);
	}
	public boolean error(String record)
	{
		return log(record, true);
	}
	private boolean log(String record, boolean error)
	{
		if (isClosed())  return false;
		else
		{
			if (error) logError(record);
			else logInfo(record);
			if (!getHasTrailer()) recordCount++; 
			return true; 
		}
	}
	protected void logInfo(String record)
	{
		write(writeLead(INFO, record));
	}
	protected void logError(String record)
	{
		write(writeLead(ERROR, record));
	}
	private String writeLead(String type, String record)
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(type); 
		sb.append(SPACE);
		sb.append("utility.ScenarioLog: ");
		sb.append(SPACE);
		sb.append(record); 
		sb.append(SPACE);
		return sb.toString();
	}
	private void write(String record) 
	{
		try
		{
			writer.write(record);
			writer.newLine();
			writer.flush(); 
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public List<String> getRecords() throws ScenarioException
	{
		buildRecordsFromLog(); 
		return records;
	}
	private void buildRecordsFromLog() throws ScenarioException
	{
		records = new ArrayList<String>(); 
		BufferedReader reader = null; 
		try
		{
			reader = new BufferedReader(new FileReader(new File(getFilename())));
			String line = reader.readLine(); 
			while (line != null)
			{
				records.add(line); 
				line = reader.readLine(); 
			}
			reader.close(); 
		} 
		catch (FileNotFoundException e)
		{
			throw new ScenarioException("ScenarioLog.buildRecordsFromLog File not found:  "+e.getMessage());
		} 
		catch (IOException e)
		{
			throw new ScenarioException("ScenarioLog.buildRecordsFromLog IO exception:  "+e.getMessage());
		} 
	}
	public void trailer()
	{
		trailer("");
	}
	private StringBuffer appendStandardTrailer()
	{
		StringBuffer sb = appendScenarioNameAndId(); 
		sb.append(SPACES+"Record count: ");
		sb.append(getRecordCount());
		return sb;
	}
	public void trailer(String customData)
	{
		hasTrailer = true; 
		setCustomData(customData);
		StringBuffer sb = appendStandardTrailer(); 
		if (!customData.equalsIgnoreCase("")) sb.append(SPACES+customData);
		log(sb.toString());
		close();
	}
	public void close()
	{
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		setClosed(true);
	}
	public String getFilename()
	{
		return filename;
	}
	public void setFilename(String fileName)
	{
		this.filename = fileName;
	}
	public boolean isClosed()
	{
		return closed;
	}
	public void setClosed(boolean closed)
	{
		this.closed = closed;
	}
	public int getRecordCount()
	{
		return recordCount;
	}
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
	}
	public boolean getHasTrailer()
	{
		return hasTrailer;
	}
	public void setHasTrailer(boolean hasTrailer)
	{
		this.hasTrailer = hasTrailer;
	}
	public String getCustomData()
	{
		return customData;
	}
	public void setCustomData(String customData)
	{
		this.customData = customData;
	}
}