/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

import org.grayleaves.utility.Input;

public interface ComponentInput<I> extends Input 
{
	public I[] getInput(); 
}
