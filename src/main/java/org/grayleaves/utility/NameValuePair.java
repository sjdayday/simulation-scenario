/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

public class NameValuePair<F> 
{


	private static final String FOR_FIELD = " for field ";
	private static final String MUST_IMPLEMENT_COMPARABLE = " must implement Comparable";
	private static final String THE_STRING = "the string ";
	private static final String COULD_NOT_BE_CONVERTED_TO_THE_EXPECTED_CLASS = " could not be converted to the expected class ";
	private static final String CLASS = "class ";
	private static final String MUST_HAVE_A_STRING_CONSTRUCTOR = " must have a String constructor";
	private static final String CONSTRUCTOR = ".constructor:  ";
	private static final String BUT_WAS = " but was ";
	private static final String EXPECTING_FIELD = "expecting field ";
	private static final String VALUE_STRING_MUST_HAVE_FORMAT_NAME_VALUE = "value string must have format \"name=value\",";
	protected static final String SET_VALUE_STRING = ".setValueString:  ";
	protected static final String NAME_VALUE_PAIR = "NameValuePair";
	private static final String QUOTE = "\"";
	private String fieldName;
	private Class<F> fieldClass;
	private F value;
	private String valueString;
	private String parsedField;
	private String parsedValue;
	private Constructor<F> constructor;

	public NameValuePair(String fieldName, Class<F> fieldClass)
	{
		this.fieldName = fieldName;
		this.fieldClass = fieldClass;
		verifyAndBuildStringConstructorForClass(); 
		verifyClassImplementsComparable(); 
	}
    
	private void verifyAndBuildStringConstructorForClass()
	{
		constructor = null;
		try
		{
			constructor = fieldClass.getConstructor(String.class);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(NAME_VALUE_PAIR+CONSTRUCTOR+CLASS+fieldClass.getSimpleName()+MUST_HAVE_A_STRING_CONSTRUCTOR+"\n"+e.toString());
		}
	}

	private void verifyClassImplementsComparable()
	{
		boolean implementsComparable = false;
		Class<?>[] interfaces = fieldClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			if (interfaces[i].equals(Comparable.class)) implementsComparable = true; 
		}
		if (!implementsComparable) throw new IllegalArgumentException(NAME_VALUE_PAIR+CONSTRUCTOR+CLASS+fieldClass.getSimpleName()+MUST_IMPLEMENT_COMPARABLE); 
//		class NotComparableClass must implement Comparable
	}

	public void setValueString(String valueString)
	{
		this.valueString = valueString; 
		parseValueString(); 
		verifyName(); 
		verifyValue(); 
	}

	private void parseValueString()
	{
		StringTokenizer st = new StringTokenizer(valueString,"=");
		if (st.countTokens() != 2) throw new IllegalArgumentException(NAME_VALUE_PAIR+SET_VALUE_STRING+VALUE_STRING_MUST_HAVE_FORMAT_NAME_VALUE+BUT_WAS+QUOTE+valueString+QUOTE); 
		parsedField = st.nextToken(); 
		parsedValue = st.nextToken(); 
	}

	private void verifyName()
	{
		if (!parsedField.trim().equalsIgnoreCase(fieldName)) throw new IllegalArgumentException(NAME_VALUE_PAIR+SET_VALUE_STRING+EXPECTING_FIELD+QUOTE+fieldName+QUOTE+BUT_WAS+QUOTE+parsedField+QUOTE); 
	}
	private void verifyValue()
	{
		try
		{
			value = (F) constructor.newInstance(parsedValue.trim());
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(NAME_VALUE_PAIR+SET_VALUE_STRING+THE_STRING+QUOTE+parsedValue+QUOTE+COULD_NOT_BE_CONVERTED_TO_THE_EXPECTED_CLASS+fieldClass.getSimpleName()+FOR_FIELD+QUOTE+fieldName+QUOTE);
		}
	}

	public String getFieldName()
	{
		return fieldName;
	}
	
	public Class<F> getFieldClass()
	{
		return fieldClass;
	}
	
	public F getValue()
	{
		return value;
	}

	public NameValuePair<F> copy()
	{
		return new NameValuePair<F>(fieldName, fieldClass); 
	}

}
