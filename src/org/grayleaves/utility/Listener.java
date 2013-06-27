/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.List;

public interface Listener
{

	public List<Event> getEvents();

	public void post(Event event);

}
