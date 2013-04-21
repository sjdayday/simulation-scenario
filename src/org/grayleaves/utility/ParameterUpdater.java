package org.grayleaves.utility;

public interface ParameterUpdater<P>
{

	public P getParameter();

	//TODO fix this mess 
	public <T>  void setParameter(T value);
	// ... here to enable ParameterPoint to call without knowing the Type at compile time
	public void setParameter(ParameterValue<?> parameterValue);

	public void setParameterClass(Class<P> parameterClass);

	public Class<P> getParameterClass(); 
}