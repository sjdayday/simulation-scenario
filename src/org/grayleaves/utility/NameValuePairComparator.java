/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;


public class NameValuePairComparator<T> implements Comparator<T>
{

	private static final String SORT_ORDER_VALUES_MUST_BE_ETC_PLUS_EXAMPLE = "sort order lowest value must be 0; highest value must be total number of pairs-1.  Example for 3 pairs:  \"2,0,1\": third pair sorts first, followed by first pair, followed by second pair.";
//	private static final String COLON = ": ";
//	private static final String NAME_VALUE_PAIRS_BUT_FOUND = " name value pairs but found ";
//	private static final String EXPECTED = "expected ";
	private static final String DOT = ".";
	private static final String RETURNED_EXCEPTION = "() returned exception: \n";
	private static final String ATTEMPT_TO_INVOKE = "attempt to invoke ";
	private static final String COMPARE = ".compare:  ";
	private static final String NUMBER_OF_NAME_VALUE_PAIRS_MUST_EQUAL_THE_NUMBER_OF_SORT_ORDER_POSITIONS = "the number of name value pairs must equal the number of sort order positions";
	private static final String SORT_ORDER_MAY_NOT_BE_NULL_OR_EMPTY = "sort order may not be null or empty";
	private static final String LIST_OF_NAME_VALUE_PAIRS_MAY_NOT_BE_NULL_OR_EMPTY = "list of name value pairs may not be null or empty";
	private static final String DOES_NOT_HAVE_METHOD = " does not have method ";
	private static final String OR_METHOD_DOES_NOT_RETURN_A_STRING = "() or method does not return a String";
	private static final String CONSTRUCTOR = ".constructor:  ";
	private static final String NAME_VALUE_PAIR_COMPARATOR = "NameValuePairComparator";
	private Method method;
	private Class<T> targetClass;
	private NameValuePair<?>[] nameValuePairs;
	private int[] sortOrderArray;
	private String firstValueString;
	private String secondValueString;
	private String methodName;
	private NameValuePair<?>[] firstPairs;
	private NameValuePair<?>[] secondPairs;
	private SortOrder sortOrder;
	private NameValuePairParser nameValuePairParser;

	public NameValuePairComparator(Class<T> targetClass, String methodName, NameValuePair<?>[] nameValuePairs, int[] sortOrder)
	{
		this(targetClass, methodName, nameValuePairs, new SortOrder(sortOrder)); 
	}

	public NameValuePairComparator(Class<T> targetClass,
			String methodName, NameValuePair<?>[] nameValuePairs, SortOrder sortOrder)
	{
		this.targetClass = targetClass; 
		this.nameValuePairs = nameValuePairs; 
		this.sortOrder = sortOrder; 
		this.sortOrderArray = sortOrder.getOrder(); 
		this.methodName = methodName; 
		validateMethod();
		validatePairsAndSortOrder(); 
		validateSortOrder(); 
		this.nameValuePairParser = new NameValuePairParser(nameValuePairs); 
	}

	private void validatePairsAndSortOrder()
	{
		if ((nameValuePairs == null) || (nameValuePairs.length == 0)) 
			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+CONSTRUCTOR+LIST_OF_NAME_VALUE_PAIRS_MAY_NOT_BE_NULL_OR_EMPTY);
		if ((sortOrderArray == null) || (sortOrderArray.length == 0))
			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+CONSTRUCTOR+SORT_ORDER_MAY_NOT_BE_NULL_OR_EMPTY);
		if (nameValuePairs.length != sortOrderArray.length)
			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+CONSTRUCTOR+NUMBER_OF_NAME_VALUE_PAIRS_MUST_EQUAL_THE_NUMBER_OF_SORT_ORDER_POSITIONS); 
		
	}
	
	private void validateSortOrder()
	{
		int length = sortOrderArray.length; 
		int last = length - 1; 
		int[] sortedSortOrder = Arrays.copyOf(sortOrderArray, length); 
		Arrays.sort(sortedSortOrder);
		if ((sortedSortOrder[0] != 0) || (sortedSortOrder[last] != last))
			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+CONSTRUCTOR+SORT_ORDER_VALUES_MUST_BE_ETC_PLUS_EXAMPLE); 
	}
	public void validateMethod()
	{
		String msg = NAME_VALUE_PAIR_COMPARATOR+CONSTRUCTOR+"class "+targetClass.getName()+DOES_NOT_HAVE_METHOD+methodName+OR_METHOD_DOES_NOT_RETURN_A_STRING;
		try
		{
			method = targetClass.getMethod(methodName, (Class<?>[]) null); 
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(msg); 
		}
		if (!method.getReturnType().equals(String.class)) throw new IllegalArgumentException(msg);
	}


	@Override
	public int compare(T first, T second)
	{
		parseValueStrings(first, second); 
		return comparePairsPerSortOrder();
	}

	private void parseValueStrings(T first, T second)
	{
		firstValueString = getValueStringFromTargetObject(first);
		firstPairs = nameValuePairParser.parse(firstValueString); 
//		firstPairs = parseValueStringToNameValuePairs(firstValueString); 
		secondValueString = getValueStringFromTargetObject(second);
		secondPairs = nameValuePairParser.parse(secondValueString); 
	}

//	private NameValuePair<?>[] parseValueStringToNameValuePairs(String valueString)
//	{
//		int numberOfPairs = nameValuePairs.length; 
//		NameValuePair<?>[] workingPair = copyNameValuePairArray(numberOfPairs); 
//		StringTokenizer st = new StringTokenizer(valueString, ",");
//		if (numberOfPairs != st.countTokens())
//			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+COMPARE+EXPECTED+numberOfPairs+NAME_VALUE_PAIRS_BUT_FOUND+st.countTokens()+COLON+valueString);
//		for (int i = 0; i < workingPair.length; i++)
//		{
//			workingPair[i].setValueString(st.nextToken()); 
//		}
//		return workingPair; 
//	}

	public NameValuePair<?>[] copyNameValuePairArray(int numberOfPairs)
	{
		NameValuePair<?>[] copyPairs = new NameValuePair<?>[numberOfPairs];
		for (int i = 0; i < copyPairs.length; i++)
		{
			copyPairs[i] = nameValuePairs[i].copy(); 
		}
		return copyPairs;
	}

	public String getValueStringFromTargetObject(T target)
	{
		String valueString = null;
		try
		{
			valueString = (String) method.invoke(target, (Object[]) null);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(NAME_VALUE_PAIR_COMPARATOR+COMPARE+ATTEMPT_TO_INVOKE+targetClass.getName()+DOT+methodName+RETURNED_EXCEPTION+
					e.toString()+" "+e.getMessage()+"\nObject that failed: "+target.toString()); 
		}
		return valueString;
	}

	private int comparePairsPerSortOrder()
	{
		int compareResult = 0; 
		for (int i = 0; i < sortOrderArray.length; i++)
		{
			compareResult = comparePair(sortOrder.get(i));
			if (compareResult != 0) break; 
		}
		return compareResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int comparePair(SortPair whichPair)
	{
		return (whichPair.direction)*((Comparable) firstPairs[whichPair.order].getValue()).compareTo(((Comparable) secondPairs[whichPair.order].getValue()));
	}
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private int comparePair(int whichPair)
//	{
//		return ((Comparable) firstPairs[whichPair].getValue()).compareTo(((Comparable) secondPairs[whichPair].getValue()));
//	}

}
