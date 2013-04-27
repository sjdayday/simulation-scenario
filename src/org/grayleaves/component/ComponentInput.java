package org.grayleaves.component;

import org.grayleaves.utility.Input;

public interface ComponentInput<I> extends Input 
{
	public I[] getInput(); 
}
