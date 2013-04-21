package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.grayleaves.utility.InvalidStaticParameterException;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.ParameterSpacePersister;
import org.grayleaves.utility.ParameterUpdater;
import org.grayleaves.utility.Pattern;
import org.grayleaves.utility.PersistenceException;
import org.grayleaves.utility.SimplePersister;
import org.grayleaves.utility.StaticParameterUpdater;
import org.junit.Before;
import org.junit.Test;


public class StaticParameterUpdaterTest
{
	@Before
	public void setUp() throws Exception
	{
		TestingBean.resetForTesting(); 
	}
	@Test
	public void veriyErrorsForClassNotFound() throws Exception
	{
		try
		{
			@SuppressWarnings("unused")
			ParameterUpdater<String> updater = new StaticParameterUpdater<String>(String.class, "stringName", "org.grayleaves.utility.InvalidBean"); 
			fail("should throw class not found"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.createClass:  Could not create class for: org.grayleaves.utility.InvalidBean", e.getMessage());
		}
		
	}
	@Test
	public void verifyErrorsForFieldNotFoundOrWrongTypeOrFinal() throws Exception
	{
		@SuppressWarnings("unused")
		ParameterUpdater<String> updater = null; 
		try
		{
			updater = new StaticParameterUpdater<String>(String.class, "invalidParm", "org.grayleaves.utility.TestingBean"); 
			fail("should throw field not found"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.verifyField:  Could not find field: invalidParm in class org.grayleaves.utility.TestingBean", e.getMessage());
		}
		//TODO test SecurityException
		try
		{
			updater = new StaticParameterUpdater<String>(String.class, "INT_PARM", "org.grayleaves.utility.TestingBean"); 
			fail("should throw field not expected type"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.verifyField:  field: INT_PARM is of type int but expected type java.lang.String, in class org.grayleaves.utility.TestingBean", e.getMessage());
		}
		try
		{
			updater = new StaticParameterUpdater<String>(String.class, "NON_PUBLIC_STRING_PARM", "org.grayleaves.utility.TestingBean"); 
			fail("should throw field not expected modifier"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.verifyField:  field: NON_PUBLIC_STRING_PARM, in class org.grayleaves.utility.TestingBean must be public static", e.getMessage());
		}
		try
		{
			updater = new StaticParameterUpdater<String>(String.class, "nonStaticStringParm", "org.grayleaves.utility.TestingBean"); 
			fail("should throw field not expected modifier"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.verifyField:  field: nonStaticStringParm, in class org.grayleaves.utility.TestingBean must be public static", e.getMessage());
		}
		try
		{
			updater = new StaticParameterUpdater<String>(String.class, "FINAL_STRING_PARM", "org.grayleaves.utility.TestingBean"); 
			fail("should throw field cannot be final"); 
		}
		catch (InvalidStaticParameterException e)
		{
			assertEquals("StaticParameterUpdater.verifyField:  field: FINAL_STRING_PARM, in class org.grayleaves.utility.TestingBean must not be final", e.getMessage());
		}
		
	}
	@SuppressWarnings("unused")
	@Test
	public void verifyPrimitiveAndCorrespondingBoxedTypesAreEquivalent() throws Exception
	{
		ParameterUpdater<Integer> intUpdater = null; 
		intUpdater = new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean");
		intUpdater = new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean");
	}
	@Test
	public void verifyGetsParameterValue() throws Exception
	{
		ParameterUpdater<String> stringUpdater = new StaticParameterUpdater<String>(String.class, "STRING_PARM", "org.grayleaves.utility.TestingBean");
		assertEquals("fred", stringUpdater.getParameter()); 
		stringUpdater.setParameter("sam");
		assertEquals("sam", stringUpdater.getParameter());
		ParameterUpdater<Integer> intUpdater = new StaticParameterUpdater<Integer>(Integer.class, "INT_PARM", "org.grayleaves.utility.TestingBean");
		assertEquals(3, intUpdater.getParameter().intValue()); 
		intUpdater.setParameter(17);
		assertEquals(17,  intUpdater.getParameter().intValue());
		ParameterUpdater<Integer> integerUpdater = new StaticParameterUpdater<Integer>(Integer.class, "INTEGER_PARM", "org.grayleaves.utility.TestingBean");
		assertEquals("constructor handles both boxed and primitive types",2, integerUpdater.getParameter().intValue()); 
		integerUpdater.setParameter(4);
		assertEquals(4, integerUpdater.getParameter().intValue());
		ParameterUpdater<Boolean> booleanUpdater = new StaticParameterUpdater<Boolean>(Boolean.class, "BOOLEAN_PARM", "org.grayleaves.utility.TestingBean");
		assertTrue(booleanUpdater.getParameter()); 
		booleanUpdater.setParameter(false);
		assertTrue(!booleanUpdater.getParameter());
		ParameterUpdater<Double> doubleUpdater = new StaticParameterUpdater<Double>(Double.class, "DOUBLE_PARM", "org.grayleaves.utility.TestingBean");
		assertEquals(0.01d, (double) doubleUpdater.getParameter(), 0.0d); 
		doubleUpdater.setParameter(4.35d);
		assertEquals(4.35d, doubleUpdater.getParameter(), 0.0d);
	}
	@Test
	public void verifyPatternsCanBeUpdated() throws Exception
	{
		ParameterUpdater<Pattern> patternUpdater = new StaticParameterUpdater<Pattern>(Pattern.class, "PATTERN_PARM", "org.grayleaves.utility.TestingBean");
		assertTrue(patternUpdater.getParameter().nextBoolean()); 
		patternUpdater.setParameter(new Pattern(new Boolean[]{false, true}));
		assertTrue("first is false",!patternUpdater.getParameter().nextBoolean()); 
		assertTrue("then true", patternUpdater.getParameter().nextBoolean()); 
		assertTrue("back to false",!patternUpdater.getParameter().nextBoolean()); 
		

	}
	@SuppressWarnings("unused")
	@Test
	public void verifyPersistedParametersDetectsErrorIfUnderlyingParametersAreRenamed() throws Exception
	{
		File fileWithRenamedParameter = buildParameterSpaceWithOldCurrentlyNonExistentStaticParameterName();
		ParameterSpacePersister<ParameterSpace> spacePersister = new ParameterSpacePersister<ParameterSpace>();
		try 
		{
			ParameterSpace newSpace = spacePersister.load(ParameterSpace.class, fileWithRenamedParameter);
		}
		catch (PersistenceException e)
		{
			assertTrue(e.getMessage().startsWith(SimplePersister.SIMPLE_PERSISTER+SimplePersister.READ_TARGET+SimplePersister.PERSISTENCE_EXCEPTION_RECEIVED_IN_XMLDECODER_PROCESSING+'\n'+
					"org.grayleaves.utility.InvalidStaticParameterException: StaticParameterUpdater.verifyField:  Could not find field: OLD_STRING_PARM in class org.grayleaves.utility.TestingBean"));
		}
	}

	public File buildParameterSpaceWithOldCurrentlyNonExistentStaticParameterName()
			throws Exception, FileNotFoundException, IOException
	{
		String filename = TestingFileBuilder.buildParameterSpaceFilenameAndPersist(true); 
		File renamedParameterFile = new File("smallParmsWithRenamedParameter.xml");
		if (renamedParameterFile.exists()) renamedParameterFile.delete(); 
		BufferedReader reader = new BufferedReader(new FileReader(filename)); 
		BufferedWriter  writer = new BufferedWriter(new FileWriter(renamedParameterFile));
		String line = null; 
		for (line = reader.readLine(); line != null; line = reader.readLine())
		{
			line = line.replaceAll("STRING_PARM", "OLD_STRING_PARM");
			writer.write(line); 
			writer.newLine(); 
		}
		reader.close();
		writer.flush();
		writer.close();
		return renamedParameterFile; 
	}

}
