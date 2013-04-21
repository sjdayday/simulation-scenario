package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.Parameter;
import org.grayleaves.utility.ParameterValue;
import org.grayleaves.utility.StaticParameterUpdater;
import org.junit.Test;


public class ParameterValueTest
{

	private ParameterValue<Integer> value;
	private ParameterValue<String> value2;
	private ParameterValue<Boolean> value3;
	@Test
	public void verifyReturnsCorrectValueForType() throws Exception
	{
		setValues();
		assertEquals(7, (int) value.getValue()); 
		assertEquals("fred", value2.getValue());
		assertTrue(!value3.getValue());
	}
	public void setValues()
	{
		value = new ParameterValue<Integer>(7);
		value2 = new ParameterValue<String>("fred");
		value3 = new ParameterValue<Boolean>(false);
	}
	@Test
	public void verifyArrayOfParamaterValuesCanHoldMultipleTypes() throws Exception
	{
		setValues(); 
		ParameterValue<?>[] values = new ParameterValue<?>[] {value, value2, value3}; 
		assertEquals(Integer.class, values[0].getValue().getClass()); 
		assertEquals(String.class, values[1].getValue().getClass());
		assertEquals(Boolean.class, values[2].getValue().getClass());
	}
	@Test
	public void verifyEquals() throws Exception
	{
		Parameter<Integer> parm = new ArrayParameter<Integer>("Integer Name", new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean"), 
				new Integer[]{6, 7, 8});
		assertEquals(new ParameterValue<Integer>(6), parm.nextParameterValue()); 
	}
}
