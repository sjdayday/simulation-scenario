package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DoubleParameterShrinkerTest
{

	private DoubleParameterShrinker<Double> shrinker;
	private double target;
	private Parameter<Double> parameter;
	private Parameter<Double> smallParameter;
	private TestingParameterUpdater<Double> parameterUpdater;

	@Before
	public void setUp() throws Exception
	{
		parameterUpdater = new TestingParameterUpdater<Double>();
		parameterUpdater.setParameterClass(Double.class); 
		parameter = new ArrayParameter<Double>("name", parameterUpdater, new Double[] {1.0, 2.0, 3.0, 4.0});
		target = parameter.next(); 
	}
	@Test
	public void verifySinglePointJustReturnsExistingParameter() throws Exception
	{
		parameter = new ArrayParameter<Double>("name", parameterUpdater, new Double[] {1.0});
		shrinker = new DoubleParameterShrinker<Double>(parameter, 1.0); 
		assertEquals(parameter, shrinker.shrink(10)); 
	}
	@Test
	public void verifyDoubleRangeShrunkByTen() throws Exception
	{
		target = parameter.next(); 
		assertEquals(2.0, target, .001);
		shrinker = new DoubleParameterShrinker<Double>(parameter, target); 
		smallParameter = shrinker.shrink(10); 
		assertEquals(11, ((ArrayParameter<Double>)smallParameter).getValues().length);
		assertTrue(smallParameter.hasNext()); 
		assertEquals(1.5, smallParameter.next(), .001);
		assertEquals(1.6, smallParameter.next(), .001);
		assertEquals(1.7, smallParameter.next(), .001);
		assertEquals(1.8, smallParameter.next(), .001);
		assertEquals(1.9, smallParameter.next(), .001);
		assertEquals(2.0, smallParameter.next(), .001);
		assertEquals(2.1, smallParameter.next(), .001);
		assertEquals(2.2, smallParameter.next(), .001);
		assertEquals(2.3, smallParameter.next(), .001);
		assertEquals(2.4, smallParameter.next(), .001);
		assertEquals(2.5, smallParameter.next(), .001);
		assertFalse(smallParameter.hasNext()); 
		assertEquals(smallParameter.getParameterUpdater(),parameter.getParameterUpdater()); 
	}
	@Test
	public void verifyDoubleRangeShrunkOnlyForUpperBoundIfTargetIsLowestValue() throws Exception
	{
		assertEquals(1.0, target, .001);
		shrinker = new DoubleParameterShrinker<Double>(parameter, target); 
		smallParameter = shrinker.shrink(10); 
		assertEquals(6, ((ArrayParameter<Double>)smallParameter).getValues().length);
		assertTrue(smallParameter.hasNext()); 
		assertEquals(1.0, smallParameter.next(), .001);
		assertEquals(1.1, smallParameter.next(), .001);
		assertEquals(1.2, smallParameter.next(), .001);
		assertEquals(1.3, smallParameter.next(), .001);
		assertEquals(1.4, smallParameter.next(), .001);
		assertEquals(1.5, smallParameter.next(), .001);
		assertFalse(smallParameter.hasNext());
	}
	@Test
	public void verifyDoubleRangeShrunkOnlyForLowerBoundIfTargetIsHighestValue() throws Exception
	{
		target = parameter.next();
		target = parameter.next();
		target = parameter.next();
		assertEquals(4.0, target, .001);
		shrinker = new DoubleParameterShrinker<Double>(parameter, target); 
		smallParameter = shrinker.shrink(10); 
		assertEquals(6, ((ArrayParameter<Double>)smallParameter).getValues().length);
		assertTrue(smallParameter.hasNext()); 
		assertEquals(3.5, smallParameter.next(), .001);
		assertEquals(3.6, smallParameter.next(), .001);
		assertEquals(3.7, smallParameter.next(), .001);
		assertEquals(3.8, smallParameter.next(), .001);
		assertEquals(3.9, smallParameter.next(), .001);
		assertEquals(4.0, smallParameter.next(), .001);
		assertFalse(smallParameter.hasNext());
	}
	//TODO lots more tests...target at lower or upperbound, etc.
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfParameterIsNotDouble() throws Exception {
		ArrayParameter<Integer> parameter = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), new Integer[] {1, 2, 3, 4});
		@SuppressWarnings("unused")
		DoubleParameterShrinker<Integer> shrinker = new DoubleParameterShrinker<Integer>(parameter, target); 
	}
	@Test(expected=IllegalArgumentException.class)
	public void verifyThrowsIfParameterIsNotArray() throws Exception {
		RangeParameter<Integer> parameter = new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 100, 10, 50);
		@SuppressWarnings("unused")
		DoubleParameterShrinker<Integer> shrinker = new DoubleParameterShrinker<Integer>(parameter, target); 
	}
}
