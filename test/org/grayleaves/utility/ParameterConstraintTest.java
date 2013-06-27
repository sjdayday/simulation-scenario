/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.BooleanParameterConstraint;
import org.grayleaves.utility.DoubleParameterConstraint;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterValue;
import org.junit.Before;
import org.junit.Test;

//TODO incorporate MikeTest logic here or replace; then use...
public class ParameterConstraintTest
{
	@SuppressWarnings("rawtypes")
	private BooleanParameterConstraint constraint;
	private Parameter<Boolean> parm1;
	private Parameter<Boolean> parm2;
//	private Parameter<Boolean> parm3;
//	private ParameterSpace space;
	private List<Parameter<?>> list;
	private ParameterIterator iterator;
	private DoubleParameterConstraint doubleConstraint;
	private Parameter<Double> parmd1;
	private Parameter<Double> parmd2;
	@Before
	public void setUp() throws Exception
	{
		parm1 = new ArrayParameter<Boolean>("Some Public Name", new TestingParameterUpdater<Boolean>(), new Boolean[] {true, false});
		parm2 = new ArrayParameter<Boolean>("Another Public Name", new TestingParameterUpdater<Boolean>(), new Boolean[] {true, false});
		parmd1 = new ArrayParameter<Double>("Some Public Name", new TestingParameterUpdater<Double>(), new Double[] {.1, .5});
		parmd2 = new ArrayParameter<Double>("Another Public Name", new TestingParameterUpdater<Double>(), new Double[] {.2, .6});
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void verifyParameterCantCoexistWithAnother() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(parm1);
		list.add(parm2);
		iterator = new ParameterIterator(list); 
		constraint = new BooleanParameterConstraint(); 
		constraint.parameter1(parm1);
		constraint.parameter2(parm2); 
		ParameterValue<?>[] values = iterator.next().getParameterPoint().getValues(); 
		constraint.value1((Boolean) values[0].getValue());
		constraint.value2((Boolean) values[1].getValue());
		assertFalse(constraint.isAllowed()); 
		assertEquals("Value true for parameter Some Public Name is not valid with value true for parameter Another Public Name",constraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		constraint.value1((Boolean) values[0].getValue());
		constraint.value2((Boolean) values[1].getValue());
		assertTrue(constraint.isAllowed()); 
		assertEquals("Value true for parameter Some Public Name is valid with value false for parameter Another Public Name",constraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		constraint.value1((Boolean) values[0].getValue());
		constraint.value2((Boolean) values[1].getValue());
		assertTrue(constraint.isAllowed()); 
		assertEquals("Value false for parameter Some Public Name is valid with value true for parameter Another Public Name",constraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		constraint.value1((Boolean) values[0].getValue());
		constraint.value2((Boolean) values[1].getValue());
		assertTrue(constraint.isAllowed()); 
		assertEquals("Value false for parameter Some Public Name is valid with value false for parameter Another Public Name",constraint.message()); 
	}
	@Test
	public void verifyOneParameterIsLessThanAnother() throws Exception
	{
		list = new ArrayList<Parameter<?>>(); 
		list.add(parmd1);
		list.add(parmd2);
		iterator = new ParameterIterator(list); 
		doubleConstraint = new DoubleParameterConstraint(); 
		doubleConstraint.parameter1(parmd1);
		doubleConstraint.parameter2(parmd2); 
		ParameterValue<?>[] values = iterator.next().getParameterPoint().getValues(); 
		doubleConstraint.value1((Double) values[0].getValue());
		doubleConstraint.value2((Double) values[1].getValue());
		assertTrue(doubleConstraint.isAllowed()); 
		assertEquals("Value 0.1 for parameter Some Public Name is valid with value 0.2 for parameter Another Public Name",doubleConstraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		doubleConstraint.value1((Double) values[0].getValue());
		doubleConstraint.value2((Double) values[1].getValue());
		assertTrue(doubleConstraint.isAllowed()); 
		assertEquals("Value 0.1 for parameter Some Public Name is valid with value 0.6 for parameter Another Public Name",doubleConstraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		doubleConstraint.value1((Double) values[0].getValue());
		doubleConstraint.value2((Double) values[1].getValue());
		assertFalse(doubleConstraint.isAllowed()); 
		assertEquals("Value 0.5 for parameter Some Public Name is not valid with value 0.2 for parameter Another Public Name",doubleConstraint.message()); 
		values = iterator.next().getParameterPoint().getValues(); 
		doubleConstraint.value1((Double) values[0].getValue());
		doubleConstraint.value2((Double) values[1].getValue());
		assertTrue(doubleConstraint.isAllowed()); 
		assertEquals("Value 0.5 for parameter Some Public Name is valid with value 0.6 for parameter Another Public Name",doubleConstraint.message()); 
	}
}
