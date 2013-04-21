package org.grayleaves.utility;

public class NameValuePairBuilder
{
	public NameValuePair<Integer> integerPair(String string)
	{
		return new NameValuePair<Integer>(string, Integer.class);
	}
	public NameValuePair<Boolean> booleanPair(String string)
	{
		return new NameValuePair<Boolean>(string, Boolean.class);
	}
	public NameValuePair<String> stringPair(String string)
	{
		return new NameValuePair<String>(string, String.class);
	}
	public NameValuePair<Double> doublePair(String string)
	{
		return new NameValuePair<Double>(string, Double.class);
	}
	
	
}
