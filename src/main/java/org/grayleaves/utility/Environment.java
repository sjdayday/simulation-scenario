package org.grayleaves.utility;

import java.util.AbstractMap;

public class Environment
{
	private static AbstractMap.SimpleEntry<String, String> TEST_ENV_VAR_ENTRY;

	public static void setEnvironmentVariableForTesting(String key,
			String value)
	{
		TEST_ENV_VAR_ENTRY = new AbstractMap.SimpleEntry<String, String>(key, value); 
	}

	public static String getEnv(String key)
	{
		if (TEST_ENV_VAR_ENTRY != null)
		{
			if (TEST_ENV_VAR_ENTRY.getKey().equals(key)) return TEST_ENV_VAR_ENTRY.getValue(); 
			else return null;
		}
		return System.getenv(key);
	}

	public static void resetEnvironmentVariableForTesting()
	{
		TEST_ENV_VAR_ENTRY = null; 
	}

	
}
