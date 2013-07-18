/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OutputFileBuilderTest
{
	private static final String SCENARIO_SUBDIR = "scenario_subdir";
	private OutputFileBuilder builder;
	private File somePathFromEnv;
	private String somePath;


	@Before
	public void setUp() throws Exception
	{
		TestingFileBuilder.cleanUpDirectory(SCENARIO_SUBDIR);
		Environment.resetEnvironmentVariableForTesting(); 
		somePathFromEnv = new File("somePath");
		somePath = somePathFromEnv.getCanonicalPath(); 
		if (somePathFromEnv.exists()) 
		{
			TestingFileBuilder.cleanUpDirectory(somePath);
		}
	}
	private void buildSomePath() throws IOException
	{
		assertTrue(somePathFromEnv.mkdir()); 
		somePath = somePathFromEnv.getCanonicalPath();
//		System.out.println("build some path: "+somePath);
	}
	@After
	public void tearDown() throws Exception
	{
		Environment.resetEnvironmentVariableForTesting(); 
	}
	@Test
	public void verifyScenarioRootDefaultsToCurrentPathIfEnvironmentVariableNotPresent() throws Exception
	{
		builder = new OutputFileBuilder("SCENARIO_ROOT",SCENARIO_SUBDIR);
		assertEquals("env variable not yet present so defaults to one directory per invocation, in users working directory",
				getRootDirPathName(), builder.getRootDirectoryFullPathName()); 
	}
	@Test
	public void verifyEnvironmentVariablePrefixedToScenarioRootIfPresent() throws Exception
	{
		setUpSomePathAsScenarioRoot(); 
		builder = new OutputFileBuilder("SCENARIO_ROOT",SCENARIO_SUBDIR);
//		System.out.println("what:  "+builder.getRootDirectoryFullPathName()); 
//		System.out.println("file:  "+builder.getRootDirectory().getCanonicalPath()); 
		
		assertEquals("scenario root set from env variable",somePath+"/scenario_subdir", builder.getRootDirectoryFullPathName()); 
	}
	private void setUpSomePathAsScenarioRoot() throws IOException
	{
		Environment.setEnvironmentVariableForTesting("SCENARIO_ROOT", somePath);
		buildSomePath();
	}
	@Test
	public void verifyBuildsDirectoryAtSimulationRootLevel() throws Exception
	{
		File dir = new File(SCENARIO_SUBDIR);
		assertTrue(!dir.exists());  
		builder = new OutputFileBuilder(SCENARIO_SUBDIR);
		assertTrue("should have created scenario subdir",dir.exists());  
		assertEquals("full path string is equivalent",getRootDirPathName(), builder.getRootDirectoryFullPathName()); 
		builder = new OutputFileBuilder(getRootDirPathName());
		assertTrue("should still exist",dir.exists());  
	}
	private String getRootDirPathName() throws IOException
	{
		File here = new File("."); 
		String rootDirectoryPathName = here.getCanonicalPath()+Constants.SLASH+SCENARIO_SUBDIR;
		return rootDirectoryPathName;
	}
	@Test
	public void verifyCreatesSubdirectoriesUnderRootDirectory() throws Exception
	{
		builder = new OutputFileBuilder(SCENARIO_SUBDIR);
		checkSubdirCreatedInRightPlace();  
		setUpSomePathAsScenarioRoot(); 
		builder = new OutputFileBuilder("SCENARIO_ROOT",SCENARIO_SUBDIR);
		checkSubdirCreatedInRightPlace();  
	}
	private void checkSubdirCreatedInRightPlace()
			throws OutputFileBuilderException
	{
		String path = builder.getRootDirectoryFullPathName(); 
		File dir = new File(path+Constants.SLASH+"logs");
		assertTrue(!dir.exists());  
		builder.createDirectory(path, "logs"); 
		assertTrue("should create dir",dir.exists());
	}
	@Test
	public void verifyCopiesSelfWithNewId() throws Exception
	{
		builder = new OutputFileBuilder(SCENARIO_SUBDIR);
		checkCloneIsCopiedWithSameState(); 
		setUpSomePathAsScenarioRoot(); 
		builder = new OutputFileBuilder("SCENARIO_ROOT",SCENARIO_SUBDIR);
		checkCloneIsCopiedWithSameState(); 
	}
	private void checkCloneIsCopiedWithSameState() throws IOException,
			OutputFileBuilderException
	{
		OutputFileBuilder copy = builder.cloneWithId(7); 
		assertEquals(7, copy.getId()); 
		assertEquals(builder.getRootDirectoryFullPathName(),copy.getRootDirectoryFullPathName()); 
	}
	@Test
	public void verifyBuildsDesiredFileInDesiredDirectory() throws Exception
	{
		builder = new OutputFileBuilder(SCENARIO_SUBDIR);
		checkFileBuiltInDesiredDirectory();  
		setUpSomePathAsScenarioRoot(); 
		builder = new OutputFileBuilder("SCENARIO_ROOT",SCENARIO_SUBDIR);
		checkFileBuiltInDesiredDirectory();  
	}
	private void checkFileBuiltInDesiredDirectory()
			throws OutputFileBuilderException, IOException
	{
		
		OutputFileBuilder newBuilder = builder.cloneWithId(7); 
//		System.out.println(builder.getRootDirectoryFullPathName()+" builder");
//		System.out.println(newBuilder.getRootDirectoryFullPathName()+" new builder");
		assertEquals(builder.getRootDirectoryFullPathName(), newBuilder.getRootDirectoryFullPathName()); 
		File targetFile = newBuilder.buildFile("logs", "a_name", "some_prefix", "extension"); 
		assertEquals(targetFile.getName(), "some_prefix_7_a_name.extension");
		assertEquals(targetFile.getCanonicalPath(), newBuilder.getRootDirectoryFullPathName()+Constants.SLASH+"logs"+Constants.SLASH+"some_prefix_7_a_name.extension");
		assertTrue("file should not exist yet; caller will write to it",!targetFile.exists());
	}
	@Test
	public void verifyBuildsSummaryFileAtRootDirectory() throws Exception
	{
		builder = new OutputFileBuilder(SCENARIO_SUBDIR);
		File targetFile = builder.buildSummaryFile("summary_file.ext"); 
		assertEquals(targetFile.getName(), "summary_file.ext");
		assertEquals(targetFile.getCanonicalPath(), builder.getRootDirectoryFullPathName()+Constants.SLASH+"summary_file.ext");
		assertTrue("summary file should not exist yet; caller will write to it",!targetFile.exists());  
	}
	@Test
	public void verifyThrowsExceptionWhenDirectoryCreationFails() throws Exception
	{
		try 
		{
			builder = getFailingBuilder(true); 
			fail("should throw"); 
		}
		catch (OutputFileBuilderException e)
		{
			assertEquals("OutputFileBuilder.constructor:  Couldn't create directory scenario_subdir", e.getMessage());
		}
		try 
		{
			builder = getFailingBuilder(false); 
			builder.getRootDirectoryFullPathName();
			fail("should throw"); 
		}
		catch (OutputFileBuilderException e)
		{
			assertTrue(e.getMessage().startsWith("OutputFileBuilder.getRootDirectoryName:  IOException attempting to read or write "+SCENARIO_SUBDIR));
		}
		
	}
	private OutputFileBuilder getFailingBuilder(boolean failInConstructor) throws OutputFileBuilderException
	{
		return new ExceptionOutputFileBuilder(failInConstructor);
	}
	private class ExceptionOutputFileBuilder extends OutputFileBuilder
	{
		public ExceptionOutputFileBuilder(boolean failInConstructor) throws OutputFileBuilderException
		{
			super(SCENARIO_SUBDIR); 
			if (failInConstructor) exception(OutputFileBuilder.CONSTRUCTOR, SCENARIO_SUBDIR, null); 
		}
		@Override
			public String getRootDirectoryFullPathName() throws OutputFileBuilderException
			{
				exception(OutputFileBuilder.GET_ROOT_DIRECTORY_NAME, SCENARIO_SUBDIR, new IOException());
				return null; 
			}
	}
}
