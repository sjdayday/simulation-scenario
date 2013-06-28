/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;



public abstract class AbstractParameter<P> implements Parameter<P>
{
	

	protected String name;
	protected ParameterUpdater<P> parameterUpdater; 

	@Override
	public abstract P defaultValue(); 

	@Override
	public abstract boolean hasNext();
	
	@Override
	public abstract P next(); 

	@Override
	public abstract void reset();

	@Override
	public String getName()
	{
		return name;
	}
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
    
	@Override
	public void updateModel(P value)
	{  //TODO delete
		parameterUpdater.setParameter(value);
	}
	@Override
	public void updateModel(ParameterValue<?> parameterValue)
	{
		parameterUpdater.setParameter(parameterValue.getValue());
	}
}
