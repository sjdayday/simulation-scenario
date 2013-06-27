/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterSpaceMapperEnum;
import org.grayleaves.utility.Pattern;
import org.junit.Test;


public class ArrayParameterTest
{
	public void verifyEmptyParameterNotAllowed() throws Exception
	{
		//FIXME verifyEmptyParameterNotAllowed
	}
	@Test
	public void verifyConstructor() throws Exception
	{
		Parameter<Boolean> BOOLEAN_PARM = new ArrayParameter<Boolean>("Some Public Name", new TestingParameterUpdater<Boolean>(), new Boolean[] {true, false});
		assertEquals("Some Public Name", BOOLEAN_PARM.getName());
	}
	@Test
	public void verifyArrayParameterWithBooleansTakesAllPossibleValuesWithFirstValueAsDefaultAndResetsWhenInvokedPastEnd() throws Exception
	{
		Parameter<Boolean> BOOLEAN_PARM = new ArrayParameter<Boolean>("parmName", new TestingParameterUpdater<Boolean>(), new Boolean[] {true, false}); 
		assertTrue(BOOLEAN_PARM.defaultValue());
		assertTrue(BOOLEAN_PARM.hasNext()); 
		assertTrue("first invocation returns default value",BOOLEAN_PARM.next());
		assertTrue(BOOLEAN_PARM.hasNext());
		assertTrue("next invocation returns next permissable value",!BOOLEAN_PARM.next());
		assertTrue("no more new values",!BOOLEAN_PARM.hasNext());
		assertTrue("but next will continue to cycle...",BOOLEAN_PARM.next());
		assertTrue("once we've gone past the end of 'hasNext', it resets", BOOLEAN_PARM.hasNext());
		
	}
	@Test
	public void verifyArrayParameterWithStringsTakesAllPossibleValuesWithFirstValueAsDefaultAndResetsWhenInvokedPastEnd() throws Exception
	{
		Parameter<String> STRING_PARM = new ArrayParameter<String>("fred", new TestingParameterUpdater<String>(), new String[] {"A", "B", "C"});
		assertTrue(STRING_PARM.hasNext());
		assertEquals("A", STRING_PARM.defaultValue());
		assertTrue(STRING_PARM.hasNext());
		assertEquals("A", STRING_PARM.next());
		assertTrue(STRING_PARM.hasNext());
		assertEquals("B", STRING_PARM.next());
		assertTrue(STRING_PARM.hasNext());
		assertEquals("C", STRING_PARM.next());
		assertTrue(!STRING_PARM.hasNext());
		assertEquals("A", STRING_PARM.next());
		assertTrue("once we've gone past the end of 'hasNext', it resets", STRING_PARM.hasNext());
		
	}
	@Test
	public void verifyArrayParameterWithIntegersTakesAllPossibleValuesAndFirstValueIsDefault() throws Exception
	{
		Parameter<Integer> INT_PARM = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), new Integer[] {5, 4, 3, 2, 1, 0, -1});
		testIntegerParameter(INT_PARM);
	}
	@Test
	public void verifyBeanConstructorAndGetterSetterBehaviorForXmlEncoder() throws Exception
	{
		Parameter<Integer> INT_PARM = new ArrayParameter<Integer>();
		assertNull("no argument constructor must be followed by setting individual fields for Parameter to function",INT_PARM.next());
		assertTrue("no argument constructor must be followed by setting individual fields for Parameter to function",!INT_PARM.hasNext()); 
		((ArrayParameter<Integer>) INT_PARM).setValues((new Integer[] {5, 4, 3, 2, 1, 0, -1}));
		((ArrayParameter<Integer>) INT_PARM).setHasNext(true);
		((ArrayParameter<Integer>) INT_PARM).setIndex(-1);
		testIntegerParameter(INT_PARM);
		
	}
	private void testIntegerParameter(Parameter<Integer> INT_PARM)
	{
		assertTrue(INT_PARM.hasNext());
		assertEquals(5, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(4, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(3, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(2, (int) INT_PARM.next());
		assertEquals("defaultValue always returns",5, (int) INT_PARM.defaultValue());
		assertTrue(INT_PARM.hasNext());
		assertEquals(1, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(0, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(-1, (int) INT_PARM.next());
		assertTrue(!INT_PARM.hasNext());
		assertEquals(5, (int) INT_PARM.next());
		assertTrue("reset after running over the end",INT_PARM.hasNext());
	}
	@Test
	public void verifyResetStartsOverFromInitialValues() throws Exception
	{
		Parameter<Integer> INT_PARM = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), new Integer[] {5, 4, 3, 2, 1, 0, -1});
		assertTrue(INT_PARM.hasNext());
		assertEquals(5, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(4, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		assertEquals(3, (int) INT_PARM.next());
		assertTrue(INT_PARM.hasNext());
		INT_PARM.reset(); 
		assertTrue(INT_PARM.hasNext());
		assertEquals(5, (int) INT_PARM.next());
	}
	@Test
	public void verifyArrayParameterWithOneValueReturnsThatValue() throws Exception
	{
		Parameter<Integer> oneParm = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), new Integer[] {6});
		assertTrue("only true prior to the first invocation", oneParm.hasNext());
		assertEquals(6, (int) oneParm.next());
		assertTrue("a parameter with a single value never hasNext after the first invocation", !oneParm.hasNext());
		assertEquals(6, (int) oneParm.next());
		assertEquals(6, (int) oneParm.defaultValue());
		assertTrue("a parameter with a single value never hasNext after the first invocation", !oneParm.hasNext());
	}
	@Test
	public void verifyArrayParameterWithNullInputReturnsNull() throws Exception
	{
		Parameter<Integer> nullParm = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), null);
		assertTrue(!nullParm.hasNext());
		assertNull(nullParm.defaultValue());
		assertNull(nullParm.next());
	}
	@Test
	public void verifyParameterReturnsNextAsParameterValue() throws Exception
	{
		Parameter<String> STRING_PARM = new ArrayParameter<String>("fred", new TestingParameterUpdater<String>(), new String[] {"A", "B", "C"});
		assertEquals("A", STRING_PARM.nextParameterValue().getValue()); 
		assertEquals("B", STRING_PARM.nextParameterValue().getValue());
		assertEquals("C", STRING_PARM.nextParameterValue().getValue());
		Parameter<Integer> INT_PARM = new ArrayParameter<Integer>("name", new TestingParameterUpdater<Integer>(), new Integer[] {5, 4, 3, 2, 1, 0, -1});
		assertEquals(5, (int) INT_PARM.nextParameterValue().getValue());
		assertEquals(4, (int) INT_PARM.nextParameterValue().getValue());
		assertEquals(3, (int) INT_PARM.nextParameterValue().getValue());
	}
	@Test
	public void verifyKnowsSize() throws Exception
	{
		Parameter<String> STRING_PARM = new ArrayParameter<String>("fred", new TestingParameterUpdater<String>(), new String[] {"A", "B", "C"});
		assertEquals(3, STRING_PARM.size()); 
	}
	@Test
	public void verifySupportsEnums() throws Exception
	{
		Parameter<ParameterSpaceMapperEnum> enumParm = new ArrayParameter<ParameterSpaceMapperEnum>("fred", new TestingParameterUpdater<ParameterSpaceMapperEnum>(), 
				new ParameterSpaceMapperEnum[]{ParameterSpaceMapperEnum.BASELINE_IS_SUBSET_OF_COMPARED});
		assertEquals(ParameterSpaceMapperEnum.BASELINE_IS_SUBSET_OF_COMPARED, enumParm.nextParameterValue().getValue()); 
	}
	@Test
	public void verifyArrayOfPatternsIsSupported() throws Exception
	{
		Parameter<Pattern> patterns = new ArrayParameter<Pattern>("test pattern", new TestingParameterUpdater<Pattern>(), 
				new Pattern[]{new Pattern(new Boolean[]{true}), new Pattern(new Boolean[] {false, true}), new Pattern(new Boolean[]{true, false, false})});
		Pattern test = patterns.nextParameterValue().getValue(); 		
		assertTrue("always true",test.nextBoolean()); 
		assertTrue("still true",test.nextBoolean()); 
		test = patterns.nextParameterValue().getValue(); 		
		assertTrue("false",!test.nextBoolean()); 
		assertTrue("then true",test.nextBoolean()); 
		assertTrue("back to false",!test.nextBoolean()); 
		test = patterns.nextParameterValue().getValue(); 		
		assertTrue("true",test.nextBoolean()); 
		assertTrue("then false",!test.nextBoolean()); 
		assertTrue("then false",!test.nextBoolean()); 
		assertTrue("back to true",test.nextBoolean()); 
	}
	
}
