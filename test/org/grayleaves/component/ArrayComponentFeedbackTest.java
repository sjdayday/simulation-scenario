/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class ArrayComponentFeedbackTest
{
	private ComponentFeedback<Boolean, Boolean> feedback;
	@Before
	public void setUp()
	{
		feedback = new ArrayComponentFeedback<Boolean, Boolean>();
		feedback.setFeedback(new Boolean[]{false, false, false, false}); 
	}
	
	@Test
	public void verifyFeedbackIdentifiesMismatchedOutput() throws Exception
	{
		ComponentOutput<Boolean> output = new ArrayComponentOutput<Boolean>(new Boolean[]{false, false, false, false});
		checkArrays("no differences", new Integer[]{}, feedback.getMismatches(output).keySet().toArray(new Integer[]{}));
		output = new ArrayComponentOutput<Boolean>(new Boolean[]{true, false, true, false});
		checkArrays("two differences", new Integer[]{0,2}, feedback.getMismatches(output).keySet().toArray(new Integer[]{}));
		output = new ArrayComponentOutput<Boolean>(new Boolean[]{true, false, true});
		try 
		{
			feedback.getMismatches(output);
			fail("should throw");
		}
		catch (ArrayLengthMismatchException e)
		{
			assertEquals("ArrayComponentFeedback.mismatched:  expected array of length 4, but was length 3", e.getMessage()); 
		}
		output = new ArrayComponentOutput<Boolean>(new Boolean[]{true, false, true, false, true});
		try 
		{
			feedback.getMismatches(output);
			fail("should throw");
		}
		catch (ArrayLengthMismatchException e)
		{
			assertEquals("ArrayComponentFeedback.mismatched:  expected array of length 4, but was length 5", e.getMessage()); 
		}
	}
	@Test
	public void verifyFeedbackProvidedForMismatches() throws Exception
	{
		ComponentOutput<Boolean> output = new ArrayComponentOutput<Boolean>(new Boolean[]{false, true, false, true});
		Map<Integer, Boolean> mismatches = feedback.getMismatches(output); 
		Set<Entry<Integer, Boolean>> set = mismatches.entrySet(); 
		Iterator<Entry<Integer, Boolean>> it = set.iterator(); 
		Entry<Integer, Boolean> entry = it.next(); 
		assertEquals(new Integer(1), entry.getKey()); 
		assertEquals(false, entry.getValue()); 
		entry = it.next();
		assertEquals(new Integer(3), entry.getKey()); 
		assertEquals(false, entry.getValue()); 
		
	}
	private void checkArrays(String comment, Integer[] expected, Integer[] actual)
	{
		assertEquals(comment+"mismatched array sizes", expected.length, actual.length); 
		for (int i = 0; i < expected.length; i++)
		{
			assertEquals(comment+"array element mismatch: "+i, expected[i], actual[i]); 
		}
	}
}
