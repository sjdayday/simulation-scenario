/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


public class ArrayComponentInputTest
{
	private ComponentInput<Boolean> inputs;
	private ComponentInput<Boolean> expected;
	@Test
	public void verifyEqualsDetectsMismatchedTypesAlthoughClassCastExceptionNotThrown() throws Exception
	{
		inputs = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		ComponentInput<Integer> differentType = new ArrayComponentInput<Integer>(new Integer[]{1,2,3,4});
		assertFalse(inputs.equals(differentType));  
	}
	@Test
	public void verifyEquals() throws Exception
	{
		inputs = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false}); 
		expected = new ArrayComponentInput<Boolean>(new Boolean[]{true,false,true,false});
		assertEquals(expected, inputs);
		expected = new ArrayComponentInput<Boolean>(new Boolean[]{false,false,true,false});
		assertFalse("one array value differs", inputs.equals(expected));
		expected = new ArrayComponentInput<Boolean>(new Boolean[]{true, false,true});
		assertFalse("array length differs", inputs.equals(expected));
		assertFalse("compare to null", inputs.equals(null));
	}
	@Test
	public void verifyEqualsForOtherTypes() throws Exception
	{
		ComponentInput<Integer> inputInteger = new ArrayComponentInput<Integer>(new Integer[]{1, 2, 3}); 
		ComponentInput<Integer> expectedInteger = new ArrayComponentInput<Integer>(new Integer[]{1, 2, 3});
		assertEquals(expectedInteger, inputInteger);
		expectedInteger = new ArrayComponentInput<Integer>(new Integer[]{1, 2, 4});
		assertFalse("one array value differs", expectedInteger.equals(inputInteger));
		ComponentInput<String> inputString = new ArrayComponentInput<String>(new String[]{"one", "two", "three"}); 
		ComponentInput<String> expectedString = new ArrayComponentInput<String>(new String[]{"one", "two", "three"});
		assertEquals(expectedString, inputString);
		expectedString = new ArrayComponentInput<String>(new String[]{"one", "two", "four"});
		assertFalse("one array value differs", expectedString.equals(inputString));
	}
	
}
