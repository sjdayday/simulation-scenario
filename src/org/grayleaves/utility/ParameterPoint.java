package org.grayleaves.utility;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ParameterPoint implements SingleParameterPoint
{
	private static Logger logger = Logger.getLogger(ParameterPoint.class);

	private static final String PARAMETERS_LIST_AND_PARAMETER_VALUE_ARRAY_ARE_NOT_THE_SAME_SIZE = "Parameters list and ParameterValue array are not the same size.";
	private static final String CONSTRUCTOR = "constructor:  ";
	private static final String PARAMETER_POINT = "ParameterPoint.";
	private ParameterValue<?>[] values;
	private List<Parameter<?>> parameters;
	private Map<String, String> parameterMap;
	private String persistedMapEntryList;
	private List<MapEntry> mapEntryList;
	public ParameterPoint()
	{
		
	}
	public ParameterPoint(ParameterValue<?>[] values, List<Parameter<?>> parameters)
	{
		this.values = values; 
		this.parameters = parameters;
		verifyValuesAndParametersSameSize(); 
		buildMapEntryList(values, parameters); 
		buildParameterMap(); 
	}
	private void verifyValuesAndParametersSameSize()
	{
		if (values.length != parameters.size())
			throw new IllegalArgumentException(PARAMETER_POINT+CONSTRUCTOR+PARAMETERS_LIST_AND_PARAMETER_VALUE_ARRAY_ARE_NOT_THE_SAME_SIZE); 
	}

	private void buildMapEntryList(ParameterValue<?>[] values,
			List<Parameter<?>> parameters)
	{
		mapEntryList = new ArrayList<MapEntry>(); 
		for (int i = 0; i < values.length; i++)
		{
			mapEntryList.add(new MapEntry(parameters.get(i).getName(), values[i].toString()));
		}
	}
	private void buildParameterMap()
	{
		parameterMap = new HashMap<String, String>();
		for (MapEntry mapEntry : mapEntryList)
		{
			parameterMap.put(mapEntry.getKey(), mapEntry.getValue()); 
		}
	}

	public ParameterValue<?> get(int i)
	{
		return values[i];
	}
	public int dimensions()
	{
		return values.length;
	}
	@Override
	public String toString()
	{
		return display(false);
	}
	public String verboseToString()
	{
		return display(true);
	}
	private String display(boolean verbose)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mapEntryList.size(); i++)
		{
			if (verbose) sb.append(mapEntryList.get(i).getKey()+"=");
			sb.append(mapEntryList.get(i).getValue());
			if (i < mapEntryList.size()-1) sb.append(", ");
		}
		return sb.toString();
	}

	public void updateModelParameters()
	{
		for (int i = 0; i < values.length; i++)
		{
			logger.debug("about to update model for parameter number "+i+" named "+parameters.get(i).getName()+" with value "+values[i].toString()); 
			parameters.get(i).updateModel(values[i]);
		}
	}
	@Override
	public boolean equals(Object obj)
	{
		ParameterPoint otherPoint = (ParameterPoint) obj;
		boolean match = true; 
		if (parameterMap.size() != otherPoint.parameterMap.size()) match = false; 
		for (String name : parameterMap.keySet())
		{
			if (!parameterMap.get(name).equals(otherPoint.parameterMap.get(name)))
			match = false; 
		}
		return match;
	}
	@Override
	public int hashCode()
	{
		int code = 0; 
		for (int i = 0; i < values.length; i++)
		{
			 code += values[i].hashCode();
		}
		return code;
	}
	public String getPersistedMapEntryList()
	{
		if (persistedMapEntryList == null)
		{
			Persister<List<MapEntry>> persister = new SimplePersister<List<MapEntry>>();
			try
			{
				persistedMapEntryList = persister.save(mapEntryList);
			}
			catch (PersistenceException e)
			{
				logger.error("PersistenceException saving persistedMapEntryList", e);
				e.printStackTrace();
			} 
		}
		
		return persistedMapEntryList;
	}
	@SuppressWarnings("unchecked")
	public void setPersistedMapEntryList(String map)
	{
		@SuppressWarnings("rawtypes")
		Persister<List> persister = new SimplePersister<List>();
		try
		{
			mapEntryList = (List<MapEntry>) persister.load(List.class, map);
			buildParameterMap();
		}
		catch (PersistenceException e)
		{
			e.printStackTrace();
		} 
	}
	@Override
	public int getId()
	{
		return -1;  //FIXME verify whether hibernate needs this? 
	}
	@Override
	public ParameterPoint getParameterPoint()
	{
		return this;
	}
	public ParameterValue<?>[] getValues()
	{
		return values;
	}
	public void setValues(ParameterValue<?>[] values)
	{
		this.values = values;
	}
	public List<Parameter<?>> getParameters()
	{
		return parameters;
	}
	public void setParameters(List<Parameter<?>> parameters)
	{
		this.parameters = parameters;
	}
}
