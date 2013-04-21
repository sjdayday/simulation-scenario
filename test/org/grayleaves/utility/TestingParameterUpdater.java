package org.grayleaves.utility;

import org.grayleaves.utility.ParameterUpdater;
import org.grayleaves.utility.ParameterValue;



public class TestingParameterUpdater<P> implements ParameterUpdater<P>
{


	@Override
	public P getParameter()
	{
		return null;
	}

	@Override
	public <T> void setParameter(T value)
	{
	}
	@Override
	public void setParameter(ParameterValue<?> parameterValue)
	{
	}

	public void setParameterClass(Class<P> parameterClass)
	{
	}

	public Class<P> getParameterClass()
	{
		return null;
	}

	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public TestingParameterUpdater()
	{
	}



}
