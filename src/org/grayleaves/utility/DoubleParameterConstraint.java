/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public class DoubleParameterConstraint
{


	private Double value2;
	private Double value1;
	private Parameter<Double> parameter2;
	private Parameter<Double> parameter1;

	public void parameter1(Parameter<Double> parmd1)
	{
		this.parameter1 = parmd1; 
	}

	public void parameter2(Parameter<Double> parmd1)
	{
		this.parameter2 = parmd1; 
	}
	public void value1(Double value)
	{
		this.value1 = value;
	}

	public void value2(Double value)
	{
		this.value2 = value;
	}

	public boolean isAllowed()
	{
		return (value1 < value2);
	}

	public String message()
	{
		StringBuffer sb = new StringBuffer();
		messageAllowed(sb, isAllowed());
		return sb.toString();
	}

	public void messageAllowed(StringBuffer sb, boolean allowed)
	{
		sb.append("Value ");
		sb.append(value1);
		sb.append(" for parameter ");
		sb.append(parameter1.getName());
		sb.append(" is ");
		sb.append((allowed) ? "" : "not ");
		sb.append("valid with value ");
		sb.append(value2);
		sb.append(" for parameter ");
		sb.append(parameter2.getName());
	}

}
