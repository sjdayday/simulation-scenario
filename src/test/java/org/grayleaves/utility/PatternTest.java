/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.grayleaves.utility.Pattern;
import org.junit.Test;

public class PatternTest
{

	private Pattern pattern;
	private boolean[] input;

	@Test
	public void verifyNullBooleanInputGeneratesIndefiniteFalsePattern() throws Exception
	{
		input = null; 
		pattern = new Pattern(input);
		for (int i = 0; i < 10; i++)
		{
			assertFalse("next "+i,pattern.nextBoolean()); 
		}
	}
	@Test
	public void verifyBooleanPatternRepeatsIndefinitely() throws Exception
	{
		input = new boolean[]{true, false, false};  
		pattern = new Pattern(input);
		checkBooleanPatternRepeatsIndefinitely();
	}
	@Test
	public void verifyBooleanInputHandledSameAs_boolean() throws Exception
	{
		pattern = new Pattern(new Boolean[]{true,false,false}); 
		checkBooleanPatternRepeatsIndefinitely();
	}
	private void checkBooleanPatternRepeatsIndefinitely()
	{
		for (int i = 0; i < 12; i++)
		{
			assertTrue("next true "+i,pattern.nextBoolean());
			assertFalse("next false "+i,pattern.nextBoolean()); 
			assertFalse("next false "+i,pattern.nextBoolean());
		}
		assertTrue(pattern.nextBoolean());
	}
	@Test
	public void verifyPatternReturnsBooleanArrayOfRequestedLength() throws Exception
	{
		pattern = new Pattern(new Boolean[]{true,false,false}); 
		Boolean[] output = pattern.toArray(8); 
		assertEquals(8, output.length); 
		assertTrue("ensure we don't lose our place, even if we're not at the beginning",pattern.nextBoolean()); 
		assertTrue(output[0]);
		assertFalse(output[1]);
		assertFalse(output[2]);
		assertTrue(output[3]);
		assertFalse(output[4]);
		assertFalse(output[5]);
		assertTrue(output[6]);
		assertFalse(output[7]);
		assertFalse("back where we were...", pattern.nextBoolean());
		assertFalse( pattern.nextBoolean());
	}
	@Test
	public void verifyPatternEquals() throws Exception
	{
		pattern = new Pattern(new Boolean[]{true,false,false}); 
		Pattern patternMatch = new Pattern(new Boolean[]{true,false,false});
		assertEquals(pattern, patternMatch);
		Pattern patternMismatch = new Pattern(new Boolean[]{true});
		assertTrue(!pattern.equals(patternMismatch));
		patternMismatch = null;
		assertTrue(!pattern.equals(patternMismatch));
		patternMismatch = new Pattern(new Boolean[]{});
		assertTrue(!pattern.equals(patternMismatch));
		patternMismatch = new Pattern(new Boolean[]{true, false, true});
		assertTrue(!pattern.equals(patternMismatch));
		assertTrue("random other class returns false",!pattern.equals(new StringBuffer()));
	}
	@Test
	public void verifyPatternToString() throws Exception
	{
		pattern = new Pattern(new Boolean[]{true,false,false}); 
		assertEquals("true, false, false", pattern.toString()); 
	}
}
