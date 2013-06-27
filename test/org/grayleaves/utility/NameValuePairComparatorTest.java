/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.grayleaves.utility.Direction;
import org.grayleaves.utility.NameValuePair;
import org.grayleaves.utility.NameValuePairComparator;
import org.grayleaves.utility.SortOrder;
import org.grayleaves.utility.SortPair;
import org.junit.Before;
import org.junit.Test;


public class NameValuePairComparatorTest
{
	private NameValuePairComparator<TestingTargetBean> comparator;
	private TestingTargetBean target1;
	private TestingTargetBean target2;
	private TestingTargetBean target3;
	private List<TestingTargetBean> list;

	@Before
	public void setUp() throws Exception
	{
		target1 = new TestingTargetBean("a=1, b.c=fred, ddd e=2.0"); 
		target2 = new TestingTargetBean("a=2, b.c=don, ddd e=1.5");  
		target3 = new TestingTargetBean("a=0, b.c=sammy, ddd e=-1.5");  // 312, 213, 321 
		rebuildList();
	}

	public void rebuildList()
	{
		list = new ArrayList<TestingTargetBean>(); 
		list.add(target1);
		list.add(target2);
		list.add(target3);
	}
	
	@Test
	public void verifyConstructorInputs() throws Exception
	{
		tryConstructingInvalidComparatorExpectingException("nonexistentMethod", new NameValuePair[]{}, new int[]{},
				"class org.grayleaves.utility.NameValuePairComparatorTest$TestingTargetBean does not have method nonexistentMethod() or method does not return a String"); 
		tryConstructingInvalidComparatorExpectingException("methodDoesntReturnString", new NameValuePair[]{}, new int[]{}, 
		"class org.grayleaves.utility.NameValuePairComparatorTest$TestingTargetBean does not have method methodDoesntReturnString() or method does not return a String"); 
		tryConstructingInvalidComparatorExpectingException("nonNullArgumentMethod", new NameValuePair[]{}, new int[]{},
		"class org.grayleaves.utility.NameValuePairComparatorTest$TestingTargetBean does not have method nonNullArgumentMethod() or method does not return a String"); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{}, new int[]{},
		"list of name value pairs may not be null or empty"); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{new NameValuePair<String>("x", String.class)}, new int[]{},
		"sort order may not be null or empty"); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{new NameValuePair<String>("x", String.class)}, new int[]{1,2},
		"the number of name value pairs must equal the number of sort order positions"); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{new NameValuePair<String>("x", String.class), 
				new NameValuePair<String>("y", String.class), new NameValuePair<String>("z", String.class)}, new int[]{1,2,3},
		"sort order lowest value must be 0; highest value must be total number of pairs-1.  Example for 3 pairs:  \"2,0,1\": third pair sorts first, followed by first pair, followed by second pair."); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{new NameValuePair<String>("x", String.class), 
				new NameValuePair<String>("y", String.class), new NameValuePair<String>("z", String.class)}, new int[]{0,1,3},
		"sort order lowest value must be 0; highest value must be total number of pairs-1.  Example for 3 pairs:  \"2,0,1\": third pair sorts first, followed by first pair, followed by second pair."); 
		tryConstructingInvalidComparatorExpectingException("getCustomData", new NameValuePair[]{new NameValuePair<String>("x", String.class), 
				new NameValuePair<String>("y", String.class), new NameValuePair<String>("z", String.class)}, new int[]{0,-1,2},
		"sort order lowest value must be 0; highest value must be total number of pairs-1.  Example for 3 pairs:  \"2,0,1\": third pair sorts first, followed by first pair, followed by second pair."); 
	}
	
	@Test
	public void verifyComparatorThrowsIllegalArgumentIfTargetClassDoesNotReturnCorrectlyParseableString() throws Exception
	{
		NameValuePair<?>[] pairs = new NameValuePair[]{new NameValuePair<Integer>("a", Integer.class), 
				new NameValuePair<String>("b.c", String.class),};
		int[] sortOrder = new int[]{0, 1}; 
		comparator = new NameValuePairComparator<TestingTargetBean>(TestingTargetBean.class, "getCustomData", pairs, sortOrder); 
		try 
		{
			Collections.sort(list, comparator); 
			fail("expecting exception ");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("NameValuePairParser.parse:  "+"expected 2 name value pairs but found 3: a=1, b.c=fred, ddd e=2.0", e.getMessage());
		}
	}
	
	@Test
	public void verifyComparatorReturnsListInExpectedOrder() throws Exception
	{
		NameValuePair<?>[] pairs = buildTestPairs();
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target3, target1, target2, "first field sorts 3,1,2"); 
		checkListOrderForSortOrder(pairs, new int[]{1, 0, 2}, target2, target1, target3, "second field sorts 2,1,3"); 
		checkListOrderForSortOrder(pairs, new int[]{2, 0, 1}, target3, target2, target1, "third field sorts 3,2,1"); 
//		target1 = new TestingTargetBean("a=1, b.c=fred, ddd e=2.0"); 
		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
//		target3 = new TestingTargetBean("a=0, b.c=sammy, ddd e=-1.5");   
		list.remove(1);
		list.add(1, target2);
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target3, target2, target1, "first two records tie-break in third field"); 

		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
		list.remove(1);
		list.add(1, target2);
		target3 = new TestingTargetBean("a=1, b.c=sammy, ddd e=-1.5");   
		list.remove(2);
		list.add(2, target3);
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target2, target1, target3, "all tie in field 1; 3rd last in field 2; first 2 records tie-break in field 3"); 

	}

	@Test
	public void verifyComparatorReturnsListInDefaultAscendingOrder() throws Exception
	{
		NameValuePair<?>[] pairs = buildTestPairs();
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target3, target1, target2, "first field sorts 3,1,2"); 
		checkListOrderForSortOrder(pairs, new int[]{1, 0, 2}, target2, target1, target3, "second field sorts 2,1,3"); 
		checkListOrderForSortOrder(pairs, new int[]{2, 0, 1}, target3, target2, target1, "third field sorts 3,2,1"); 
//		target1 = new TestingTargetBean("a=1, b.c=fred, ddd e=2.0"); 
		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
//		target3 = new TestingTargetBean("a=0, b.c=sammy, ddd e=-1.5");   
		list.remove(1);
		list.add(1, target2);
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target3, target2, target1, "first two records tie-break in third field"); 
		
		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
		list.remove(1);
		list.add(1, target2);
		target3 = new TestingTargetBean("a=1, b.c=sammy, ddd e=-1.5");   
		list.remove(2);
		list.add(2, target3);
		checkListOrderForSortOrder(pairs, new int[]{0, 1, 2}, target2, target1, target3, "all tie in field 1; 3rd last in field 2; first 2 records tie-break in field 3"); 
	}
	public NameValuePair<?>[] buildTestPairs()
	{
		NameValuePair<?>[] pairs = new NameValuePair[]{new NameValuePair<Integer>("a", Integer.class), 
				new NameValuePair<String>("b.c", String.class),
				new NameValuePair<Double>("ddd e", Double.class)};
		return pairs;
	}
//	target1 = new TestingTargetBean("a=1, b.c=fred, ddd e=2.0"); 
//	target2 = new TestingTargetBean("a=2, b.c=don, ddd e=1.5");  
//	target3 = new TestingTargetBean("a=0, b.c=sammy, ddd e=-1.5");  // 312, 213, 321 

	@Test
	public void verifyComparatorReturnsListInExpectedAscendingOrDescendingOrder() throws Exception
	{
		NameValuePair<?>[] pairs = buildTestPairs();
		SortOrder sortOrder = new SortOrder(new SortPair(0, Direction.DESCENDING), new SortPair(1), new SortPair(2)); 
		checkListOrderForSortOrder(pairs, sortOrder, target2, target1, target3, "first field sorts descending 2,1,3"); 
		checkListOrderForSortOrder(pairs, new SortOrder(new SortPair(1, Direction.DESCENDING), new SortPair(0), new SortPair(2)), target3, target1, target2, "second field sorts desc 3,1,2"); 
		checkListOrderForSortOrder(pairs, new SortOrder(new SortPair(2, Direction.DESCENDING), new SortPair(0), new SortPair(1)), target1, target2, target3, "third field sorts desc 1,2,3"); 
//		target1 = new TestingTargetBean("a=1, b.c=fred, ddd e=2.0"); 
		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
//		target3 = new TestingTargetBean("a=0, b.c=sammy, ddd e=-1.5");   
		list.remove(1);
		list.add(1, target2);
		checkListOrderForSortOrder(pairs, new SortOrder(new SortPair(0, Direction.DESCENDING), new SortPair(1, Direction.DESCENDING), new SortPair(2, Direction.DESCENDING)), 
				target1, target2, target3, "first two records tie-break in third field"); 
		
		target2 = new TestingTargetBean("a=1, b.c=fred, ddd e=1.99");
		list.remove(1);
		list.add(1, target2);
		target3 = new TestingTargetBean("a=1, b.c=sammy, ddd e=-1.5");   
		list.remove(2);
		list.add(2, target3);
		checkListOrderForSortOrder(pairs, new SortOrder(new SortPair(0, Direction.DESCENDING), new SortPair(1, Direction.DESCENDING), new SortPair(2, Direction.DESCENDING)),
				target3, target1, target2, "all tie in field 1; 3rd first in field 2; first 2 records tie-break in field 3"); 
		

	}
	
	private void checkListOrderForSortOrder(NameValuePair<?>[] pairs, SortOrder sortOrder, TestingTargetBean a, TestingTargetBean b, TestingTargetBean c, String msg)
	{
		rebuildList();
		comparator = new NameValuePairComparator<TestingTargetBean>(TestingTargetBean.class, "getCustomData", pairs, sortOrder); 
		Collections.sort(list, comparator); 
		assertEquals(msg+" a: ", a, list.get(0)); 
		assertEquals(msg+" b: ", b, list.get(1)); 
		assertEquals(msg+" c: ", c, list.get(2));
		
	}
	private void checkListOrderForSortOrder(NameValuePair<?>[] pairs, int[] sortOrder, TestingTargetBean a, TestingTargetBean b, TestingTargetBean c, String msg) throws Exception
	{
		checkListOrderForSortOrder(pairs, new SortOrder(sortOrder), a, b, c, msg);
	}

	private void tryConstructingInvalidComparatorExpectingException(String method, NameValuePair<?>[] pairs, int[] sortOrder, String exception)
	{
		try
		{
			comparator = new NameValuePairComparator<TestingTargetBean>(TestingTargetBean.class, method, pairs, sortOrder); 
			fail("expecting exception "+exception);
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("NameValuePairComparator.constructor:  "+exception, e.getMessage());
		}
	}

	private class TestingTargetBean
	{
		private String data;
		public TestingTargetBean(String data)
		{
			this.data = data; 
		}
		@SuppressWarnings("unused")
		public String getCustomData()
		{
			return data; 
		}
		@SuppressWarnings("unused")
		public void methodDoesntReturnString()
		{
		}
		@SuppressWarnings("unused")
		public String nonNullArgumentMethod(int blah)
		{
			return null; 
		}
	}
}
