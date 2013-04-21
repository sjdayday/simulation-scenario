package org.grayleaves.utility;



public class LogEvent implements Event
{


	private String logEvent;

	public LogEvent(String logEvent)
	{
		this.logEvent = logEvent;
	}
	@Override
	public String toString()
	{
		return logEvent;
	}
}
