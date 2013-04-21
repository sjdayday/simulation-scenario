package org.grayleaves.utility;

import java.util.List;

public interface Listener
{

	public List<Event> getEvents();

	public void post(Event event);

}
