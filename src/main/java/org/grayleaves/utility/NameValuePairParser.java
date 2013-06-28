/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.util.StringTokenizer;

public class NameValuePairParser 
{


	private static final String NAME_VALUE_PAIR_PARSER = "NameValuePairParser";
	private static final String PARSE = ".parse:  ";
	private static final String EXPECTED = "expected ";
	private static final String NAME_VALUE_PAIRS_BUT_FOUND = " name value pairs but found ";
	private static final String LIST_OF_NAME_VALUE_PAIRS_MAY_NOT_BE_NULL_OR_EMPTY = "list of name value pairs may not be null or empty";
	private static final String COLON = ": ";
	private static final String CONSTRUCTOR = ".constructor:  ";

	
	private NameValuePair<?>[] nameValuePairs;
	
	public NameValuePairParser(NameValuePair<?>... nameValuePairs)
	{
		this.nameValuePairs = nameValuePairs; 
		validate(); 
	}

	private void validate()
	{
		if ((nameValuePairs == null) || (nameValuePairs.length == 0)) 
			throw new IllegalArgumentException(NAME_VALUE_PAIR_PARSER+CONSTRUCTOR+LIST_OF_NAME_VALUE_PAIRS_MAY_NOT_BE_NULL_OR_EMPTY);
	}

	public NameValuePair<?>[] parse(String valueString)
	{
		return parseValueStringToNameValuePairs(valueString);
	}
	private NameValuePair<?>[] parseValueStringToNameValuePairs(String valueString)
	{
		int numberOfPairs = nameValuePairs.length; 
		NameValuePair<?>[] workingPair = copyNameValuePairArray(numberOfPairs); 
		StringTokenizer st = new StringTokenizer(valueString, ",");
		if (numberOfPairs != st.countTokens())
			throw new IllegalArgumentException(NAME_VALUE_PAIR_PARSER+PARSE+EXPECTED+numberOfPairs+NAME_VALUE_PAIRS_BUT_FOUND+st.countTokens()+COLON+valueString);
		for (int i = 0; i < workingPair.length; i++)
		{
			workingPair[i].setValueString(st.nextToken()); 
		}
		return workingPair; 
	}
	public NameValuePair<?>[] copyNameValuePairArray(int numberOfPairs)
	{
		NameValuePair<?>[] copyPairs = new NameValuePair<?>[numberOfPairs];
		for (int i = 0; i < copyPairs.length; i++)
		{
			copyPairs[i] = nameValuePairs[i].copy(); 
		}
		return copyPairs;
	}


}
