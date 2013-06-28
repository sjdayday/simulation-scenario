/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public interface Parameter<P>
{
	public P defaultValue();

	public P next();

	public boolean hasNext();

	public void setName(String string);

	public String getName();

	public ParameterValue<P> nextParameterValue();

	public void updateModel();

	public void reset();

	public abstract void updateModel(P value);

	public abstract void updateModel(ParameterValue<?> parameterValue);

	public int size();
	
	public ParameterUpdater<P> getParameterUpdater();

}