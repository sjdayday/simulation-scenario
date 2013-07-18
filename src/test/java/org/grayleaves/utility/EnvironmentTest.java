package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class EnvironmentTest
{

	@Test
	public void verifyWrapperForSystemEnvironmentReturnsValueSetOrNull() throws Exception
	{
		assertNull(Environment.getEnv("UNDEFINED_SYSTEM_ENV_VAR"));
		Environment.setEnvironmentVariableForTesting("SCENARIO_ROOT", "/mypath/somewhere"); 
		assertEquals("/mypath/somewhere",Environment.getEnv("SCENARIO_ROOT")); 
		assertNull(Environment.getEnv("UNDEFINED_SYSTEM_ENV_VAR"));
	}
	@After
	public void deleteSystemEnvVarsForTesting()
	{
		Environment.resetEnvironmentVariableForTesting(); 
	}

}
