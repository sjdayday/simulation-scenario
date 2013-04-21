package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.grayleaves.utility.NameValuePair;
import org.junit.Before;
import org.junit.Test;


public class NameValuePairTest
{
	private NameValuePair<Integer> pair;
	@Before
	public void setUp() throws Exception
	{
		pair = new NameValuePair<Integer>("a", Integer.class); 
	}
	@Test
	public void verifyFieldNameAndType() throws Exception
	{
		assertEquals("a", pair.getFieldName()); 
		assertEquals(Integer.class, pair.getFieldClass()); 
	}
	@Test
	public void verifyFieldClassImplementsComparable() throws Exception
	{
		try
		{
			@SuppressWarnings("unused")
			NameValuePair<NotComparableClass> pair = new NameValuePair<NotComparableClass>("b", NotComparableClass.class); 
			fail("expecting exception cause not comparable");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("NameValuePair.constructor:  class NotComparableClass must implement Comparable", e.getMessage());
		}
//		NameValuePair<TestingComparableClass> goodPair = new NameValuePair<TestingComparableClass>("c", TestingComparableClass.class); 
		assertTrue("TestingComparableClass implements Comparable", true); 
	}
	@Test
	public void verifyFieldClassHasStringConstructor() throws Exception
	{
		try
		{
			@SuppressWarnings("unused")
			NameValuePair<BadConstructorClass> pair = new NameValuePair<BadConstructorClass>("blah", BadConstructorClass.class);  
			fail("expecting exception"); 
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(e.getMessage().startsWith("NameValuePair.constructor:  class BadConstructorClass must have a String constructor")); 
		}
	}
	
	@Test
	public void verifyInvalidFormatPassedInValueStringThrowsIllegalArgument() throws Exception
	{
		trySetValueStringExpectingException("aaaa", "value string must have format \"name=value\", but was \"aaaa\"");
		trySetValueStringExpectingException("aaaa=", "value string must have format \"name=value\", but was \"aaaa=\"");
		trySetValueStringExpectingException("=aaaa", "value string must have format \"name=value\", but was \"=aaaa\"");
		trySetValueStringExpectingException("a=b=c", "value string must have format \"name=value\", but was \"a=b=c\"");
		trySetValueStringExpectingException("b=2", "expecting field \"a\" but was \"b\"");
		trySetValueStringExpectingException("a=sam", "the string \"sam\" could not be converted to the expected class Integer for field \"a\"");
		trySetValueStringExpectingException("a=2.0", "the string \"2.0\" could not be converted to the expected class Integer for field \"a\"");
		
	}
	public void trySetValueStringExpectingException(String valueString, String exception)
	{
		try
		{
			pair.setValueString(valueString); 
			fail("expecting exception "+exception);
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("NameValuePair.setValueString:  "+exception, e.getMessage());
		}
	}
	
	@Test
	public void verifyParseIgnoresBlanksAndCase() throws Exception
	{
		pair.setValueString("a=2"); 
		pair.setValueString(" a=2"); 
		pair.setValueString("a =2"); 
		pair.setValueString("A=2"); 
	}
	@Test
	public void verifyValuesConvertProperly() throws Exception
	{
		NameValuePair<Integer> intPair = new NameValuePair<Integer>("a", Integer.class); 
		intPair.setValueString("A=2"); 
		assertEquals(2, intPair.getValue().intValue()); 
		intPair.setValueString("A= 2 "); 
		assertEquals(2, intPair.getValue().intValue()); 
		
		NameValuePair<Double> doublePair = new NameValuePair<Double>("a", Double.class); 
		doublePair.setValueString("A= 2.15 "); 
		assertEquals(2.15, doublePair.getValue().doubleValue(), 0.0); 

		NameValuePair<Boolean> booleanPair = new NameValuePair<Boolean>("a", Boolean.class); 
		booleanPair.setValueString("A= true "); 
		assertTrue(booleanPair.getValue()); 
	
		NameValuePair<String> stringPair = new NameValuePair<String>("a", String.class); 
		stringPair.setValueString("A= sam I am "); 
		assertEquals("sam I am", stringPair.getValue()); 
	}
	@Test
	public void verifyCopyReturnsCopyOfThisInstance() throws Exception
	{
		NameValuePair<Integer> pairCopy = pair.copy(); 
		assertEquals("a", pairCopy.getFieldName()); 
		assertEquals(Integer.class, pairCopy.getFieldClass()); 
		pairCopy.setValueString("A=2"); 
		assertEquals(2, pairCopy.getValue().intValue()); 

	}
}
