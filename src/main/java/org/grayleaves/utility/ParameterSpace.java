/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.util.ArrayList;
import java.util.List;

/**
 * Collects the set of {@link Parameter Parameters} that control the behavior of a {@link Model}.  Provides a {@link SimpleParameterIterator} to  
 * return each {@link ParameterPoint} in the ParameterSpace.  Typically, each ParameterPoint is then used as the set of values defining a {@link Scenario}.
 * @see Scenario
 * @see Parameter
 * @see ParameterPoint
 */
public class ParameterSpace  
{
	

	private int id; 
	private String name; 
	private List<Parameter<?>> parameters;
	private String propertiesLocation;
	private ParameterIterator iterator = null;
	private String filename; // = "fileNotSpecified";
	private boolean grid;
	private int level;
	private int factor;
	public ParameterSpace()
	{
		parameters = new ArrayList<Parameter<?>>(); 

	}
	public void addParameter(Parameter<?> parameter)
	{
		parameters.add(parameter); 
	}
	public Parameter<?> getParameter(int index)
	{
		return parameters.get(index);
	}
	public void loadProperties(String propertiesLocation) throws InvalidPropertiesException
	{
		this.propertiesLocation = propertiesLocation; 
		if (ModelProperties.getProperties() == null)
		{
			try
			{
				ModelProperties.load(propertiesLocation);
			} catch (ModelPropertiesException e)
			{
				throw new InvalidPropertiesException("Could not find "+propertiesLocation+":  "+e.getMessage());
			}
		}
	}

	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public final String getPropertiesLocation()
	{
		return propertiesLocation;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public final void setPropertiesLocation(String propertiesLocation)
	{
		this.propertiesLocation = propertiesLocation;
		try
		{
			loadProperties(this.propertiesLocation);
		} catch (InvalidPropertiesException e)
		{
			//TODO ParameterSpace.setPropertiesLocation test exception 
			System.out.println("ParameterSpace.setPropertiesLocation: exception executing loadProperties: "+e.getMessage());
		}
	}
	public ParameterIterator iterator()
	{
		if (iterator  == null) 
		{
			if (grid ) iterator = new GridParameterIterator(parameters, null, level, factor);
			else iterator = new SimpleParameterIterator(parameters);
		}
		return iterator;
	}
	@SuppressWarnings("rawtypes")
	public int size()
	{
		int size = 1;
		for (Parameter parameter : parameters)
		{
			size = size * parameter.size(); 
		}
		return size;
	}

	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public List<Parameter<?>> getParameters()
	{
		return parameters;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setParameters(List<Parameter<?>> parameters)
	{
		this.parameters = parameters;
	}
	public String getFilename()
	{
		return filename;
	}
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void useGridIteration(int level, int factor)
	{
		grid = true; 
		this.level =level; 
		this.factor = factor; 
	}
}
