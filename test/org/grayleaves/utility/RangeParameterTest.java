package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.UnsupportedParameterException;
import org.junit.Test;


public class RangeParameterTest
{
	private Parameter<Integer> INT_PARM;
	@Test
	public void verifyConstructor() throws Exception
	{	
		INT_PARM = new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 100, 10, 50);
		assertEquals("Int2 Public Name", INT_PARM.getName()); 
	}
//	@Test
	public void verifyRangeParameterStartsAtDefaultAndCyclesThroughTheFullRangeReturningToTheBeginning() throws Exception
	{
		//FIXME verifyRangeParameterStartsAtDefaultAndCyclesThroughTheFullRangeReturningToTheBeginning
	}
	@Test
	public void verifySimpleRangeParameterReturnsAllPossibleValuesAndResetsWhenWeContinuePastNotHasNext() throws Exception
	{
		INT_PARM = new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 100, 10, 50); 
		assertEquals(50, (int) INT_PARM.defaultValue()); 
		for (int i = 0; i <= 100; i=i+10)
		{
			assertTrue(INT_PARM.hasNext());
			assertEquals(i, (int) INT_PARM.next());
		}
		assertTrue(!INT_PARM.hasNext());
		assertEquals(0, (int) INT_PARM.next());
		assertTrue("hasNext resets once we have gone past the end of the range.",INT_PARM.hasNext());
	}
	@Test
	public void verifyResetCausesReturnToInitialValue() throws Exception
	{
		INT_PARM = new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 100, 10, 50); 
		assertEquals(0, (int) INT_PARM.next());
		assertEquals(10, (int) INT_PARM.next());
		assertEquals(20, (int) INT_PARM.next());
		INT_PARM.reset(); 
		assertEquals(0, (int) INT_PARM.next());
		assertEquals(10, (int) INT_PARM.next());
	}
	@Test
	public void verifyUnsupportedVariantsThrowException() throws Exception
	{
		try
		{
			INT_PARM = new RangeParameter<Integer>(null, new TestingParameterUpdater<Integer>(), 100, 0, 10, 50);
			fail("should throw -- min > max");
		}
		catch (UnsupportedParameterException e)
		{
			assertEquals("RangeParameter constructor:  Minimum must be less than maximum", e.getMessage()); 
		}
		try
		{
			INT_PARM = new RangeParameter<Integer>(null, new TestingParameterUpdater<Integer>(), 0, 100, 10, -50);
			fail("should throw -- default out of range ");
		}
		catch (UnsupportedParameterException e)
		{
			assertEquals("RangeParameter constructor:  Default must be within range of minimum to maximum", e.getMessage()); 
		}
		try
		{
			INT_PARM = new RangeParameter<Integer>(null, new TestingParameterUpdater<Integer>(), 0, 100, 200, 50);
			fail("should throw -- interval too great");
		}
		catch (UnsupportedParameterException e)
		{
			assertEquals("RangeParameter constructor:  Interval must be less than maximum - minimum", e.getMessage()); 
		}
	}
	@Test
	public void verifyKnowsSize() throws Exception
	{
		INT_PARM = new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 100, 10, 50);
		assertEquals(11, INT_PARM.size());
	}
}
