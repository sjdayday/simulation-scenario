/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

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
