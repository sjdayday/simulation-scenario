/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;



public class ParameterValue<V> 
{


	private V value;
	public ParameterValue()
	{
	}

	public ParameterValue(V value)
	{
		this.value = value;
	}
	@Override
	public String toString()
	{
		return value.toString();
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{ //TODO parameterValue remove cast to Type
		return (getValue().equals(((ParameterValue<V>) obj).getValue()));  
	}

	public V getValue()
	{
		return value;
	}
	public void setValue(V value)
	{
		this.value = value;
	}
}
