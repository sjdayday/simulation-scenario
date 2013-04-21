package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.InvalidPropertiesException;
import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.ModelProperties;
import org.grayleaves.utility.ParameterIterator;
import org.grayleaves.utility.ParameterPoint;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.ParameterSpacePersister;
import org.grayleaves.utility.Persister;
import org.grayleaves.utility.PropertiesParameterUpdater;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.StaticParameterUpdater;
import org.grayleaves.utility.UnsupportedParameterException;
import org.junit.Before;
import org.junit.Test;


public class ParameterSpaceTest
{
	private ParameterSpace space;
	private Persister<ParameterSpace> spacePersister; 
	
	@Before
	public void setUp() throws Exception
	{
		space = buildParameterSpace(); 
	}
	public static ParameterSpace buildParameterSpace() throws InvalidPropertiesException,
			InvalidStaticParameterException, UnsupportedParameterException
	{
		ModelProperties.setPropertiesForTesting(null);
		ParameterSpace space = new ParameterSpace(); 
		space.loadProperties("org/grayleaves/utility/testing.properties"); 
		space.addParameter(new ArrayParameter<Integer>("Integer Name", new PropertiesParameterUpdater<Integer>(Integer.class, "integer.property"), new Integer[]{6, 7, 8}));
		space.addParameter(new ArrayParameter<String>("String Public Name", new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean"), new String[]{"fred", "sam"}));
		space.addParameter(new RangeParameter<Integer>("Int2 Public Name", new TestingParameterUpdater<Integer>(), 0, 30, 10, 20));
		return space; 
	}
	@Test
	public void verifyParametersDefinitionCanBePersistedAndReconsitituted() throws Exception
	{
		//TODO refactor parms.xml creation for TestingFileBuilder
		space.setFilename("parms.xml"); 
		spacePersister = new ParameterSpacePersister<ParameterSpace>();
		File file = spacePersister.save(space, "parms.xml"); 
		ParameterSpace newSpace = spacePersister.load(ParameterSpace.class, file);
		assertEquals(6, newSpace.getParameter(0).next());
		assertEquals("fred", newSpace.getParameter(1).next()); 
		assertEquals(0, newSpace.getParameter(2).next());
	}
	@Test
	public void verifyParameterSpaceCreatedWithMixtureOfArrayAndRangeParameters() throws Exception
	{
		assertEquals(3,space.getParameters().size());
	}
	@Test
	public void verifyIterationThroughParametersFirstParmMovingSlowlyLastMovingQuickly() throws Exception
	{
		ParameterIterator iterator = space.iterator(); 
		ParameterPoint point = iterator.next(); // space.nextPoint(); 
		assertEquals("6, fred, 0", point.toString()); 
		assertEquals("6, fred, 10", iterator.next().toString());
		assertEquals("6, fred, 20", iterator.next().toString());
		assertEquals("6, fred, 30", iterator.next().toString());
		assertEquals("6, sam, 0", iterator.next().toString());
		assertTrue(iterator.hasNext()); 
	}
	@Test
	public void verifyDetectsInvalidPropertiesFile() throws Exception
	{
		ModelProperties.setPropertiesForTesting(null); 
		space = new ParameterSpace(); 
		try
		{
			space.loadProperties("invalidProperties"); 
			fail("should throw properties file not found.");
		}
		catch (InvalidPropertiesException e)
		{
			assertTrue(e.getMessage().startsWith("Could not find invalidProperties:  ")); 
		}
	}
	@Test
	public void verifyKnowsNumberOfParameterPoints() throws Exception
	{	
		assertEquals(24, space.size()); 
	}
}
