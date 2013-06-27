/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.junit.Test;


public class GenericTest
{

	@Test
	public void verifyGetType() throws Exception
	{
		GenericFactory<String, TestingInput> factory = new GenericFactory<String, TestingInput>();
		assertTrue(factory.makeScenario(StringBuffer.class) instanceof StringBuffer);
	}
	
	
	private class GenericFactory<R, I>
	{
		public <S> S makeScenario(Class<S> c) throws Exception
		{
			return c.newInstance(); 
		}
	}
	
	
}
