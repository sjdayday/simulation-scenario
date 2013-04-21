package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.grayleaves.utility.Listener;
import org.grayleaves.utility.LogEvent;
import org.grayleaves.utility.SimulationListener;
import org.junit.Test;


public class ModelListenerTest
{
	private Listener listener;

	@Test
	public void verifyModelListenerReceivesUpdates() throws Exception
	{	
		listener = new SimulationListener(); 
		assertEquals(0, listener.getEvents().size());
		listener.post(new LogEvent("log 1")); 
		listener.post(new LogEvent("log 2")); 
		assertEquals(2, listener.getEvents().size());
		assertEquals("log 1", listener.getEvents().get(0).toString());
		assertEquals("log 2", listener.getEvents().get(1).toString());
		
	}
}
