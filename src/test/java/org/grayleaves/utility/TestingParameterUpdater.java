/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import org.grayleaves.utility.ParameterUpdater;
import org.grayleaves.utility.ParameterValue;



public class TestingParameterUpdater<P> implements ParameterUpdater<P>
{


	private Class<P> parameterClass;

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
		this.parameterClass = parameterClass; 
	}

	public Class<P> getParameterClass()
	{
		return parameterClass;
	}

	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public TestingParameterUpdater()
	{
	}



}
