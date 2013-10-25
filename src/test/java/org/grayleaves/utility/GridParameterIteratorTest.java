package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GridParameterIteratorTest extends SimpleParameterIteratorTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}
	@Test
	public void verifyIteratesOneLevelDeepGivenFitnessTrackerAndLevelOne() throws Exception
	{
		
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7}));
		list.add(new ArrayParameter<Double>("Double Name", new StaticParameterUpdater<Double>(Double.class, "DOUBLE_PARM", "org.grayleaves.utility.TestingBean"), 
				new Double[]{1.0, 2.0, 3.0}));
		iterator = new GridParameterIterator(list, new TestingFitnessTracker(list), 1, 10); 
		assertTrue(iterator.hasNext());
		ParameterPoint point = iterator.next(); 
		assertEquals("6, 1.0", point.toString()); 
		assertEquals("6, 2.0", iterator.next().toString()); 
		assertEquals("6, 3.0", iterator.next().toString()); 
		assertEquals("7, 1.0", iterator.next().toString()); 
		assertEquals("7, 2.0", iterator.next().toString()); 
		assertEquals("7, 3.0", iterator.next().toString()); 
		assertEquals("6, 2.0 was a best point","6, 1.5", iterator.next().toString()); 
		assertEquals("6, 1.6", iterator.next().toString()); 
		assertEquals("6, 1.7", iterator.next().toString()); 
		assertEquals("6, 1.8", iterator.next().toString()); 
		assertEquals("6, 1.9", iterator.next().toString()); 
		assertEquals("6, 2.0", iterator.next().toString()); 
		assertEquals("6, 2.1", iterator.next().toString()); 
		assertEquals("6, 2.2", iterator.next().toString()); 
		assertEquals("6, 2.3", iterator.next().toString()); 
		assertEquals("6, 2.4", iterator.next().toString()); 
		assertEquals("6, 2.5", iterator.next().toString()); 
		assertEquals("7, 1.0 was a best point","7, 1.0", iterator.next().toString()); 
		assertEquals("7, 1.1", iterator.next().toString()); 
		assertEquals("7, 1.2", iterator.next().toString()); 
		assertEquals("7, 1.3", iterator.next().toString()); 
		assertEquals("7, 1.4", iterator.next().toString()); 
		assertTrue(iterator.hasNext());
		assertEquals("7, 1.5", iterator.next().toString()); 
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void verifyDelegatesToSimpleParameterIterator() throws Exception
	{
		iterator = buildIterator();
		assertTrue(iterator instanceof GridParameterIterator); 
		assertTrue(((GridParameterIterator) iterator).getParameterIterator() instanceof SimpleParameterIterator); 
	}
	@Test
	public void verifyFitnessTrackerPassedToAllSimpleParameterIterators() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7}));
		list.add(new ArrayParameter<Double>("Double Name", new StaticParameterUpdater<Double>(Double.class, "DOUBLE_PARM", "org.grayleaves.utility.TestingBean"), 
				new Double[]{1.0, 2.0, 3.0}));
		ParameterIterator iterator = new GridParameterIterator(list, new TestingFitnessTracker(list), 1, 10); 
		assertTrue(iterator instanceof GridParameterIterator); 
		TestingFitnessTracker tracker = new TestingFitnessTracker(list); 
		iterator.setFitnessTracker(tracker);
		ParameterIterator simpleParameterIterator = ((GridParameterIterator) iterator).getParameterIterator();  
		assertEquals(tracker, ((SimpleParameterIterator)simpleParameterIterator).getFitnessTrackerForTesting()); 
		while (iterator.hasNext()) 
		{
			iterator.next(); 
		}
		int index = ((GridParameterIterator) iterator).getProcessedParameterIteratorsForTesting().size() - 1;
		ParameterIterator simpleParameterIterator2 = ((GridParameterIterator) iterator).getProcessedParameterIteratorsForTesting().get(index);  
		assertFalse("iterated through multiple simple parameter iterators",simpleParameterIterator2.equals(simpleParameterIterator)); 
		assertEquals("all should have the same tracker",tracker, ((SimpleParameterIterator)simpleParameterIterator2).getFitnessTrackerForTesting()); 
		
	}
	@Test
	public void verifyIteratesOnceAsSimpleParameterIteratorForLevelZero() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7}));
		list.add(new ArrayParameter<Double>("Double Name", new StaticParameterUpdater<Double>(Double.class, "DOUBLE_PARM", "org.grayleaves.utility.TestingBean"), 
				new Double[]{1.0, 2.0}));
		iterator = buildIterator();
		assertTrue(iterator.hasNext());
		ParameterPoint point = iterator.next(); 
		assertEquals("6, 1.0", point.toString()); 
		assertEquals("6, 2.0", iterator.next().toString()); 
		assertEquals("7, 1.0", iterator.next().toString()); 
		assertEquals("7, 2.0", iterator.next().toString()); 
		assertFalse(iterator.hasNext());
	}
	@Override
	protected ParameterIterator buildIterator()
	{
		return new GridParameterIterator(list);
	}
	private class TestingFitnessTracker implements FitnessTracker
	{

		private List<ParameterPoint> bestPoints;

		public TestingFitnessTracker(List<Parameter<?>> list)
		{
			bestPoints = new ArrayList<ParameterPoint>(); 
			iterator = new SimpleParameterIterator(list); 
			iterator.next(); 
			bestPoints.add(iterator.next()); 
			iterator.next(); 
			bestPoints.add(iterator.next()); 
		}

		@Override
		public List<ParameterPoint> getBestParameterPoints()
		{
			return bestPoints;
		}

		@Override
		public void setCurrentParameterPoint(ParameterPoint point)
		{
		}
		
	}
}
