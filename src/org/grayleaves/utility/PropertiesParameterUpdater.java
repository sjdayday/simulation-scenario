/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class PropertiesParameterUpdater<P> implements ParameterUpdater<P>
{

	private String internalName;
	private Class<P> parameterClass;

	public PropertiesParameterUpdater(Class<P> parameterClass, String internalName) throws InvalidPropertiesException
	{
		this.parameterClass = parameterClass; 
		this.internalName = internalName; 	
		if (ModelProperties.getProperties().getProperty(internalName) == null)
			throw new InvalidPropertiesException("Could not find property "+internalName+" in ModelProperties");
	}
	public P getParameter()
	{
		String propertyParameter = ModelProperties.getProperties().getProperty(internalName); 
		Constructor<P> constructor = null;
		P parameter = null; 
		try
		{
			constructor = parameterClass.getConstructor(String.class);
		} catch (SecurityException e) 
		{ 
		} catch (NoSuchMethodException e)
		{
		} 
		try
		{
			parameter = constructor.newInstance(propertyParameter);
		} catch (IllegalArgumentException e)
		{
		} catch (InstantiationException e)
		{
		} catch (IllegalAccessException e)
		{
		} catch (InvocationTargetException e)
		{
		} 
		return parameter; 
//		return (P) ModelProperties.getProperties().getProperty(parameter);
	} 
	@Override
	public void setParameter(ParameterValue<?> parameterValue)
	{
		Properties temp = ModelProperties.getProperties();
		if (temp != null) 
			temp.setProperty(internalName, parameterValue.getValue().toString()); 
	}
	@Override
	public <T> void setParameter(T value)
	{
		//TODO rework or delete; currently unused
//		ModelProperties.getProperties().setProperty(param, value.toString());
		setParameter(new ParameterValue<T>(value)); 
//		Properties temp = ModelProperties.getProperties();
//		if (temp != null) 
//			temp.setProperty(internalName, value.toString()); 
	}
	// Class<T> c
	// T cons.newInstance(String parameter);   // should work for Number and Boolean types
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public PropertiesParameterUpdater()
	{
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	@Override
	public final Class<P> getParameterClass()
	{
		return parameterClass;
	}
	@Override
	public final void setParameterClass(Class<P> parameterClass)
	{
		this.parameterClass = parameterClass;
	}
	public String getInternalName()
	{
		return internalName;
	}
	public void setInternalName(String internalName)
	{
		this.internalName = internalName;
	}
}
