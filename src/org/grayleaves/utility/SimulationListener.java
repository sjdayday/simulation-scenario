package org.grayleaves.utility;


import java.util.ArrayList;
import java.util.List;

public class SimulationListener implements Listener
{


	private List<Event> events;
	
	public SimulationListener()
	{
		events = new ArrayList<Event>(); 
	}
	@Override
	public List<Event> getEvents()
	{
		return events;
	}
	@Override
	public void post(Event event)
	{
		events.add(event);
	}
	
}
