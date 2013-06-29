/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.MapEntry;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.ParameterValue;
import org.grayleaves.utility.Persister;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.SimplePersister;
import org.grayleaves.utility.StaticParameterUpdater;
import org.grayleaves.utility.UnsupportedParameterException;
import org.junit.Before;
import org.junit.Test;


public class ParameterPointTest
{
	
	private ParameterPoint point;

	private List<Parameter<?>> list;
	private ParameterIterator iterator; 
	
	@Before
	public void setUp() throws Exception
	{
		buildIterator();
	}
	private void buildIterator() throws InvalidStaticParameterException,
			UnsupportedParameterException
	{
		TestingBean.resetForTesting();
		list = new ArrayList<Parameter<?>>(); 
		list.add(new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7, 8}));
		list.add(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"),
				new String[]{"tom", "sam"}));
		list.add(new RangeParameter<Integer>("Int2 Public Name", new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean"),
				0, 30, 10, 20));
		iterator = new ParameterIterator(list);
	}
	@Test
	public void verifyEqualsAndHashcode() throws Exception
	{
		ParameterPoint point1 = iterator.next(); 
		point1.updateModelParameters(); 
		buildIterator();
		ParameterPoint point2 = iterator.next();
		point2.updateModelParameters();
		assertEquals(point1, point2); 
		ParameterPoint point3 = iterator.next();
		point3.updateModelParameters(); 
		assertTrue(!point2.equals(point3));
	}
	@Test
	public void verifyTwoPointsFromParameterIteratorsWithDifferentParametersButSameParameterValuesAreNotEqual() throws Exception
	{
		TestingBean.resetForTesting();
		List<Parameter<?>> list1 =  new ArrayList<Parameter<?>>();
		List<Parameter<?>> list2 =  new ArrayList<Parameter<?>>();
		Parameter<Boolean> BOOLEAN_PARMA = new ArrayParameter<Boolean>("BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 
		Parameter<Boolean> ANOTHER_BOOLEAN_PARMA = new ArrayParameter<Boolean>("ANOTHER_BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "ANOTHER_BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 
		Parameter<Boolean> BOOLEAN_PARMB = new ArrayParameter<Boolean>("BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 
		Parameter<Boolean> YET_ANOTHER_BOOLEAN_PARMB = new ArrayParameter<Boolean>("yet ANOTHER_BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "YET_ANOTHER_BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 

		list1.add(BOOLEAN_PARMA);
		list1.add(ANOTHER_BOOLEAN_PARMA);

		list2.add(BOOLEAN_PARMB);
		list2.add(YET_ANOTHER_BOOLEAN_PARMB);
		ParameterIterator iteratorA = new ParameterIterator(list1);
		ParameterIterator iteratorB = new ParameterIterator(list2);
		ParameterPoint pointA = iteratorA.next();
		ParameterPoint pointB = iteratorB.next();
		assertFalse("although the two parameters have the same values, they are not the same (different names), so equals should fail", pointA.equals(pointB));
		assertFalse(iteratorA.next().equals(iteratorB.next()));
	}
	@Test
	public void verifyTwoPointsFromParameterIteratorsWithSameParametersButDifferentParameterOrdersAreEqual() throws Exception
	{
		TestingBean.resetForTesting();
		List<Parameter<?>> list1 =  new ArrayList<Parameter<?>>();
		List<Parameter<?>> list2 =  new ArrayList<Parameter<?>>();
		Parameter<Boolean> BOOLEAN_PARMA = new ArrayParameter<Boolean>("BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 
		Parameter<Boolean> ANOTHER_BOOLEAN_PARMA = new ArrayParameter<Boolean>("ANOTHER_BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "ANOTHER_BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 
		Parameter<Boolean> BOOLEAN_PARMB = new ArrayParameter<Boolean>("BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false}); // changed to make ParameterPoints at second iteration match.
		Parameter<Boolean> ANOTHER_BOOLEAN_PARMB = new ArrayParameter<Boolean>("ANOTHER_BOOLEAN_PARM name", new StaticParameterUpdater<Boolean>(Boolean.class, "ANOTHER_BOOLEAN_PARM", "org.grayleaves.utility.TestingBean"),
				new Boolean[] {false, true}); 

		list1.add(BOOLEAN_PARMA);
		list1.add(ANOTHER_BOOLEAN_PARMA);
		// add in different order, two copies of the two parameters.  Copies because typically the second set will be rebuilt from a previously persistent ParameterSpace
		list2.add(ANOTHER_BOOLEAN_PARMB);
		list2.add(BOOLEAN_PARMB);
		ParameterIterator iteratorA = new ParameterIterator(list1);
		ParameterIterator iteratorB = new ParameterIterator(list2);
		ParameterPoint pointA = iteratorA.next();
//		System.out.println("pointA "+pointA.verboseToString());
		ParameterPoint pointB = iteratorB.next();
//		System.out.println("pointB "+pointB.verboseToString());
		assertTrue("although the two parameters have the same values at the second iteration, they were added in a different order, which should not matter", pointA.equals(pointB));
		pointA = iteratorA.next();
//		System.out.println("pointA 2 "+pointA.verboseToString());
		pointB = iteratorB.next();
//		System.out.println("pointB 2 "+pointB.verboseToString());

		assertTrue(pointA.equals(pointB));
	}
	@Test
	public void verifyCanCreateParameterWithArbitraryNumberOfValuesOfArbitraryType() throws Exception
	{
		List<Parameter<?>> parms = buildParms();
		point = new ParameterPoint(new ParameterValue<?>[]{new ParameterValue<Boolean>(false), new ParameterValue<Integer>(7), new ParameterValue<String>("fred")}, parms);
		assertEquals(3, point.dimensions());
		assertEquals("false, 7, fred", point.toString()); 
		assertEquals("name 1=false, name 2=7, name 3=fred", point.verboseToString());
	}
	@Test
	public void verifyParameterListAndParameterValueArrayAreSameSize() throws Exception
	{
		List<Parameter<?>> parms = buildParms();
		parms.remove(2);
		try 
		{
			point = new ParameterPoint(new ParameterValue<?>[]{new ParameterValue<Boolean>(false), new ParameterValue<Integer>(7), new ParameterValue<String>("fred")}, parms);
			fail("should throw illegal argument exception");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("ParameterPoint.constructor:  Parameters list and ParameterValue array are not the same size.",e.getMessage()); 
		}
	}
	public List<Parameter<?>> buildParms()
	{
		List<Parameter<?>> parms = new ArrayList<Parameter<?>>();   
		parms.add(new ArrayParameter<Boolean>("name 1", null, new Boolean[]{true}));  
		parms.add(new ArrayParameter<Boolean>("name 2", null, new Boolean[]{true}));
		parms.add(new ArrayParameter<Boolean>("name 3", null, new Boolean[]{true}));
		return parms;
	}
	@Test
	public void verifyPointUpdatesUnderlyingParametersWithItsValues() throws Exception
	{
		assertEquals(3, TestingBean.INT_PARM); 
		assertEquals("fred", TestingBean.STRING_PARM); 
		assertEquals(2, (int) TestingBean.INTEGER_PARM); 
		point = iterator.next(); 
		assertEquals(new ParameterValue<Integer>(6), point.get(0)); 
		assertEquals(new ParameterValue<String>("tom"), point.get(1));
		assertEquals(new ParameterValue<Integer>(0), point.get(2));
		// but underlying values haven't been updated yet
		assertEquals(3, TestingBean.INT_PARM); 
		assertEquals("fred", TestingBean.STRING_PARM); 
		assertEquals(2, (int) TestingBean.INTEGER_PARM); 
		
		point.updateModelParameters();
		assertEquals(6, TestingBean.INT_PARM); 
		assertEquals("tom", TestingBean.STRING_PARM); 
		assertEquals(0, (int) TestingBean.INTEGER_PARM); 
	}
	@Test
	public void verifyUpdateCantBeDoneMultipleTimesWithSamePointCausingValuesToChange() throws Exception
	{	
		point = iterator.next(); 
		assertEquals(new ParameterValue<Integer>(6), point.get(0)); 
		assertEquals(new ParameterValue<String>("tom"), point.get(1));
		assertEquals(new ParameterValue<Integer>(0), point.get(2));
		point.updateModelParameters();
		assertEquals(6, TestingBean.INT_PARM); 
		assertEquals("tom", TestingBean.STRING_PARM); 
		assertEquals(0, (int) TestingBean.INTEGER_PARM); 
		iterator.next();  // force parameters to next values
		point.updateModelParameters(); // but original point should stay the same
		assertEquals(6, TestingBean.INT_PARM); 
		assertEquals("tom", TestingBean.STRING_PARM); 
		assertEquals("iterator has moved on, but point value should stay the same",0, (int) TestingBean.INTEGER_PARM); 
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void verifyParameterMapIsPersisted() throws Exception
	{
		point = iterator.next(); 
		String map = point.getPersistedMapEntryList(); 
		Persister<List> persister = new SimplePersister<List>();
		List<MapEntry> newMap = (List<MapEntry>) persister.load(List.class, map ); 
		assertEquals("6", newMap.get(0).getValue());
		assertEquals("Integer Name", newMap.get(0).getKey());
		assertEquals("tom", newMap.get(1).getValue());
		assertEquals("String Public Name", newMap.get(1).getKey());
		assertEquals("0", newMap.get(2).getValue());
		assertEquals("Int2 Public Name", newMap.get(2).getKey());
		ParameterPoint newPoint = new ParameterPoint();
		newPoint.setPersistedMapEntryList(map); 
		assertEquals("should be equal", point, newPoint); 
		assertEquals("Integer Name=6, String Public Name=tom, Int2 Public Name=0", newPoint.verboseToString()); 
	}
}
