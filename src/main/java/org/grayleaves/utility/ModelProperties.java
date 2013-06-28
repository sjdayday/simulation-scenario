/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.Properties;

public class ModelProperties extends PropertyLoader
{
	private static boolean hasLoaded = false; 
	
	public static void load(String propertiesFileName) throws ModelPropertiesException
	{
		try
		{
			if (!hasLoaded) properties = PropertyLoader.loadProperties(propertiesFileName); 
			hasLoaded = true; 
		}
		catch (IllegalArgumentException e)
		{
			throw new ModelPropertiesException("Could not find properties "+propertiesFileName+":  "+e.getMessage()); 
		}
	}
	public static void forceload(String propertiesFileName) throws ModelPropertiesException
	{
		hasLoaded = false;
		load(propertiesFileName); 
	}
	public static Properties getProperties()
	{
		return properties;
	}
	/**
	 * Used for unit testing only
	 * @param properties 
	 */
	public static void setPropertiesForTesting(Properties properties)
	{
		PropertyLoader.properties = properties;
		hasLoaded = false;
	}
}
