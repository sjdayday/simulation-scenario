/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class TestingComponentTest
{
	private TestingComponent component;

	@Before
	public void setup() throws Exception
	{
		component = new TestingComponent();
	}
	@Test
	public void verifySimpleTestingBehavior() throws Exception
	{
		 component.input(new Boolean[]{true,false,true,false,true,true}); 
		 Boolean[] results = component.process(); 
		 ComponentOutput<Boolean> output = new ArrayComponentOutput<Boolean>(results); 
		 assertEquals(new ArrayComponentOutput<Boolean>(new Boolean[]{false,true,false,true,false,false}), output);
	}
}
