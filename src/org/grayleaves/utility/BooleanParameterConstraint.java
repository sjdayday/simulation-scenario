package org.grayleaves.utility;


public class BooleanParameterConstraint <P>
{

	private Parameter<Boolean> parameter1;
	private Parameter<Boolean> parameter2;
	private Boolean value1;
	private Boolean value2;
	public BooleanParameterConstraint()
	{
	}

	public boolean isAllowed()
	{
		return ! (value1 && value2);
//		return ! (parameter1.nextParameterValue().getValue() && parameter2.nextParameterValue().getValue());
	}
	public void parameter1(Parameter<Boolean> parm1)
	{
		this.parameter1 = parm1; 
	}
	public void parameter2(Parameter<Boolean> parm2)
	{
		this.parameter2 = parm2; 
	}

	public void value1(Boolean bool1)
	{
		this.value1 = bool1; 
	}

	public void value2(Boolean bool2)
	{
		this.value2 = bool2; 
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
