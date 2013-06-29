/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.StaticParameterUpdater;
import org.junit.Before;
import org.junit.Test;


public class ParameterIteratorTest
{
	private List<Parameter<?>> list;
	private ParameterIterator iterator; 
	
	@Before
	public void setUp() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7, 8}));
		list.add(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"),
				new String[]{"fred", "sam"}));
		list.add(new RangeParameter<Integer>("Int2 Public Name", new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean"),
				0, 30, 10, 20));
	}
	@Test
	public void verifyIteratorDetectsChangeInParameterListSize() throws Exception
	{
		iterator = new ParameterIterator(list); 
		try 
		{
			list.remove(2);
			iterator.next(); 
			fail("should throw can't modify");
		}
		catch (IllegalStateException e)
		{
			assertEquals(ParameterIterator.PARAMETER_LIST_SIZE_CANNOT_BE_CHANGED+"; was 3, is now 2.", e.getMessage()); 
		}
	}
	@Test
	public void verifyParameterIteratorCantBeCreatedWithEmptyList() throws Exception
	{
		try 
		{
			iterator = new ParameterIterator(new ArrayList<Parameter<?>>()); 
			fail("should throw empty list");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(ParameterIterator.PARAMETER_LIST_CANNOT_BE_EMPTY, e.getMessage()); 
		}
	}
	@Test
	public void verifyIteratorReturnsParameterPointsWhileUnderlyingParameterHasNext() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<String>("String Public Name", new TestingParameterUpdater<String>(), new String[]{"fred", "sam"}));
		iterator = new ParameterIterator(list);
		assertTrue(iterator.hasNext());
		assertEquals("fred", iterator.next().toString()); 
		assertTrue(iterator.hasNext());
		assertEquals("sam", iterator.next().toString()); 
		assertTrue(!iterator.hasNext());
		
	}
	@Test
	public void verifyParameterIteratorReturnsParameterPointsVaryingSlowFirstToQuicklyLast() throws Exception
	{		
		iterator = new ParameterIterator(list);
		assertTrue(iterator.hasNext());
		ParameterPoint point = iterator.next(); 
		assertEquals("6, fred, 0", point.toString()); 
		assertEquals("6, fred, 10", iterator.next().toString());
		assertEquals("6, fred, 20", iterator.next().toString());
		assertEquals("6, fred, 30", iterator.next().toString());
		assertEquals("6, sam, 0", iterator.next().toString());
		assertEquals("6, sam, 10", iterator.next().toString());
		assertEquals("6, sam, 20", iterator.next().toString());
		assertEquals("6, sam, 30", iterator.next().toString());
		assertTrue(iterator.hasNext());
		assertEquals("7, fred, 0", iterator.next().toString()); 
		assertEquals("7, fred, 10", iterator.next().toString());
		assertEquals("7, fred, 20", iterator.next().toString());
		assertEquals("7, fred, 30", iterator.next().toString());
		assertEquals("7, sam, 0", iterator.next().toString());
		assertEquals("7, sam, 10", iterator.next().toString());
		assertEquals("7, sam, 20", iterator.next().toString());
		assertEquals("7, sam, 30", iterator.next().toString());
		assertEquals("8, fred, 0", iterator.next().toString()); 
		assertEquals("8, fred, 10", iterator.next().toString());
		assertEquals("8, fred, 20", iterator.next().toString());
		assertEquals("8, fred, 30", iterator.next().toString());
		assertEquals("8, sam, 0", iterator.next().toString());
		assertEquals("8, sam, 10", iterator.next().toString());
		assertEquals("8, sam, 20", iterator.next().toString());
		assertTrue(iterator.hasNext());
		assertEquals("8, sam, 30", iterator.next().toString());
		assertTrue(!iterator.hasNext());
	}
	@Test
	public void verifyRebuildingParameterIteratorStartsOverFromBeginning() throws Exception
	{
		iterator = new ParameterIterator(list);
		assertTrue(iterator.hasNext());
		ParameterPoint point = iterator.next(); 
		assertEquals("6, fred, 0", point.toString()); 
		assertEquals("6, fred, 10", iterator.next().toString());
		assertEquals("6, fred, 20", iterator.next().toString());
		assertEquals("6, fred, 30", iterator.next().toString());
		assertEquals("6, sam, 0", iterator.next().toString());
		assertEquals("6, sam, 10", iterator.next().toString());
		assertEquals("6, sam, 20", iterator.next().toString());
		assertEquals("6, sam, 30", iterator.next().toString());
		iterator = new ParameterIterator(list);
		assertTrue(iterator.hasNext());
		point = iterator.next(); 
		assertEquals("6, fred, 0", point.toString()); 
		assertEquals("6, fred, 10", iterator.next().toString());
		assertEquals("6, fred, 20", iterator.next().toString());
		assertEquals("6, fred, 30", iterator.next().toString());

	}
	@Test
	public void verifyIteratorReturnsPointsWhoseValueDoesntChange() throws Exception
	{
		iterator = new ParameterIterator(list);
		ParameterPoint point1 = iterator.next();
		assertEquals("6, fred, 0", point1.toString());
		ParameterPoint point2 = iterator.next();
		assertEquals("6, fred, 10", point2.toString());
		assertEquals("should still be the same, if new copy of values array is passed","6, fred, 0", point1.toString());
	}
}
