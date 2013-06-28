/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("rawtypes")
public class ResourceManager
{

	private static List<Listener> LISTENERS;
	private static Map<ScenarioSet, ResourceStatistics> SCENARIO_SETS;
	private static SimpleDateFormat FORMATTER;
	public static int SCENARIO_SET_DELAY_THRESHOLD = 1000;
	public static int SCENARIO_SET_DELAY_MILLISECONDS = 200;
	private static String RUNNING = " running next scenario after ";

	static
	{
		init(); 
	}

	public static void init()
	{
		LISTENERS = new ArrayList<Listener>(); 
		SCENARIO_SETS = new HashMap<ScenarioSet, ResourceStatistics>();
		FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
	}
	
	public static void resetForTesting()
	{
		init();
		SCENARIO_SET_DELAY_THRESHOLD = 1000;
		SCENARIO_SET_DELAY_MILLISECONDS = 200;
	}

	public static void addListener(Listener listener)
	{
		LISTENERS.add(listener);
	}

	public static List<Listener> getListeners()
	{
		return LISTENERS;
	}

	public static void echoEventForTesting(Event event)
	{
		postListeners(event);
	}
	private static void postListeners(Event event)
	{
		for (Listener listener : LISTENERS)
		{
			listener.post(event);
		}
	}

	public static void removeListener(Listener listener)
	{
		LISTENERS.remove(listener);
	}

	public static void registerScenarioSet(ScenarioSet scenarioSet)
	{
		if (scenarioSet != null)
		{
			SCENARIO_SETS.put(scenarioSet, new ResourceStatistics());
			postListeners(new LogEvent(FORMATTER.format(MockClock.getCalendar().getTime())+" ScenarioSet "+scenarioSet.getName()+" registered."));
		}
	}

	public static void run(ScenarioSet scenarioSet)
	{
		ResourceStatistics stats = SCENARIO_SETS.get(scenarioSet);
		if ((scenarioSet != null) && (stats != null ))
		{
			boolean shouldDelay = stats.scenarioCount >= SCENARIO_SET_DELAY_THRESHOLD; 
			String delay = null;
			if (shouldDelay) 
			{
				try
				{
					Thread.sleep(SCENARIO_SET_DELAY_MILLISECONDS);
					delay = "delay of "+SCENARIO_SET_DELAY_MILLISECONDS+" milliseconds";
				}
				catch (InterruptedException e)
				{
					postListeners(new LogEvent(FORMATTER.format(MockClock.getCalendar().getTime())+" ScenarioSet "+scenarioSet.getName()+RUNNING+delay+", but was interrupted: "+Constants.CRLF+e.getMessage()));
				} 
			}
			else 
			{
				delay = "no delay"; 
			}
			postListeners(new LogEvent(FORMATTER.format(MockClock.getCalendar().getTime())+" ScenarioSet "+scenarioSet.getName()+RUNNING+delay));
			stats.scenarioCount++; 
		}
			
	}
}
