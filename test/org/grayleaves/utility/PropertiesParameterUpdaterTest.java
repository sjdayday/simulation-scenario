package org.grayleaves.utility;

import static org.junit.Assert.*;

import java.util.Properties;

import org.grayleaves.utility.InvalidPropertiesException;
import org.grayleaves.utility.ModelProperties;
import org.grayleaves.utility.ParameterUpdater;
import org.grayleaves.utility.PropertiesParameterUpdater;
import org.junit.Before;
import org.junit.Test;


public class PropertiesParameterUpdaterTest
{
	//TODO invert the dependency on ModelProperties -- use Spring to pass the class that holds the properties 
	private ParameterUpdater<String> updater;

	@Before
	public void setUp() throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty("some.internal.name", "fred"); 
		properties.setProperty("integer.internal.name", "2");
		properties.setProperty("boolean.internal.name", "true");
		ModelProperties.setPropertiesForTesting(properties); 
	}
	@Test
	public void verifyErrorsDetectedForInvalidProperties() throws Exception
	{
		try
		{
			updater = new PropertiesParameterUpdater<String>(String.class, "an.invalid.name"); 
			fail("should throw property not found.");
		}
		catch (InvalidPropertiesException e)
		{
			assertTrue(e.getMessage().startsWith("Could not find property an.invalid.name in ModelProperties")); 
		}
	}
	@Test
	public void verifyStringPropertiesCanBeUpdatedAndRetrieved() throws Exception
	{
		updater = new PropertiesParameterUpdater<String>(String.class, "some.internal.name");
		assertEquals("fred", updater.getParameter()); 
//		updater.setParameter(new ParameterValue<String>("anotherValue")); 
		updater.setParameter("anotherValue");
		assertEquals("anotherValue", updater.getParameter());
		assertEquals("anotherValue", ModelProperties.getProperties().getProperty("some.internal.name"));
	}
	@Test
	public void verifyNumberAndBooleanTypesCanBeUpdated() throws Exception
	{
		ParameterUpdater<Integer> intUpdater = new PropertiesParameterUpdater<Integer>(Integer.class, "integer.internal.name");
		assertEquals(2, (int) intUpdater.getParameter()); 
//		intUpdater.setParameter(new ParameterValue<Integer>(3)); 
		intUpdater.setParameter(3);
		assertEquals(3, (int) intUpdater.getParameter());
		assertEquals("3", ModelProperties.getProperties().getProperty("integer.internal.name"));
	}
}
