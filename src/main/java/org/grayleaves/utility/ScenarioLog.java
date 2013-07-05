/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

public class ScenarioLog<R, I> 
{

	private static final String SPACES = "   ";
	private static final String LAYOUT = "%p %c{2}:  %m %n";
	private static final String DATE_FORMAT = "yyyy_MM_dd__hh_mm_ssaa";
	private String filename; 
	private boolean closed = false; 
	private transient FileAppender appender;
	private static Logger logger = Logger.getLogger(ScenarioLog.class);
	protected List<String> records;
	private Scenario<R, I> scenario;
	private int recordCount;
	private boolean hasTrailer = false;
	private String customData = "";
	//TODO either configure so log4j statements in Model log here, or convert this just to a file.   
	public ScenarioLog()
	{
	}
	public ScenarioLog(Scenario<R, I> scenario) throws ScenarioException
	{
		this.scenario = scenario; 
		buildFileName();
		configureAppender(); 
		logHeader();
		hasTrailer = false;
		recordCount = 0; 
	}
	private void logHeader()
	{
		StringBuffer sb = appendScenarioNameAndId(); 
		sb.append(SPACES+"Date/Time:  ");
		sb.append(buildDateTime()); 
		log(sb.toString());
		recordCount = 0; // ignore header
//		logger.info(sb.toString()); 
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
	protected void configureAppender() throws ScenarioException 
	{
		try
		{
			appender = new FileAppender(new EnhancedPatternLayout(LAYOUT), getFilename());
			appender.setName(this.toString());
		} 
		catch (IOException e)
		{
			throw new ScenarioException("ScenarioException:  IOException in ScenarioLog.configureAppender:  "+e.getMessage()); 
		}
		BasicConfigurator.resetConfiguration(); 
		logger.addAppender(appender); 
//		Logger.getRootLogger().setLevel(Level.DEBUG); 
		
		
//		throw new ScenarioException("name " + appender.getName()+
//				"file " +appender.getFile() +
//				"immediate? " + appender.getImmediateFlush() + " numb " + logger.getAllAppenders().hasMoreElements()
//				);
//		BasicConfigurator.configure(appender); 
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
		logger.info(record);
//		logger.info("one record logged");
//		Appender app = logger.getAppender(this.toString());
//		if (app == null) throw new RuntimeException("null app"); 
//		throw new RuntimeException("enabled for info " + logger.isEnabledFor(Priority.INFO) +
//				" level " + logger.getLevel().toString() +
//				" error handler " + app.getErrorHandler() +
//				"enabled for info " + logger.isInfoEnabled() 
//				"enabled for info " + logger.EnabledFor(Priority.INFO) +
//				
//				);
	}
	protected void logError(String record)
	{
		logger.error(record);
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
		appender.close(); 
		logger.removeAppender(appender); 
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
