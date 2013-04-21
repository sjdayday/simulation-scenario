package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.grayleaves.utility.Constants;
import org.grayleaves.utility.OutputFileBuilder;
import org.grayleaves.utility.OutputFileBuilderException;
import org.junit.Before;
import org.junit.Test;


public class OutputFileBuilderTest
{
	private OutputFileBuilder builder;


	@Before
	public void setUp() throws Exception
	{
		TestingFileBuilder.cleanUpDirectory("scenario_root");
	}
	@Test
	public void verifyBuildsDirectoryAtSimulationRootLevel() throws Exception
	{
		File dir = new File("scenario_root");
		assertTrue(!dir.exists());  
		builder = new OutputFileBuilder("scenario_root");
		assertTrue("should have created scenario root",dir.exists());  
		File here = new File("."); 
		String rootDirectoryPathName = here.getCanonicalPath()+Constants.SLASH+"scenario_root";
		assertEquals("full path string is equivalent",rootDirectoryPathName, builder.getRootDirectoryName()); 
		builder = new OutputFileBuilder(rootDirectoryPathName);
		assertTrue("should still exist",dir.exists());  
	}
	@Test
	public void verifyCreatesSubdirectoriesUnderRootDirectory() throws Exception
	{
		builder = new OutputFileBuilder("scenario_root");
		File dir = new File(builder.getRootDirectoryName()+Constants.SLASH+"logs");
		assertTrue(!dir.exists());  
		builder.createDirectory("logs"); 
		assertTrue("should create dir",dir.exists());  
	}
	@Test
	public void verifyCopiesSelfWithNewId() throws Exception
	{
		builder = new OutputFileBuilder("scenario_root");
		OutputFileBuilder copy = builder.cloneWithId(7); 
		assertEquals(7, copy.getId()); 
		File here = new File("."); 
		String rootDirectoryPathName = here.getCanonicalPath()+Constants.SLASH+"scenario_root";
		assertEquals("full path string is equivalent",rootDirectoryPathName, copy.getRootDirectoryName()); 
	}
	@Test
	public void verifyBuildsDesiredFileInDesiredDirectory() throws Exception
	{
		builder = new OutputFileBuilder("scenario_root");
		builder = builder.cloneWithId(7); 
		File targetFile = builder.buildFile("logs", "a_name", "some_prefix", "extension"); 
		assertEquals(targetFile.getName(), "some_prefix_7_a_name.extension");
		assertEquals(targetFile.getCanonicalPath(), builder.getRootDirectoryName()+Constants.SLASH+"logs"+Constants.SLASH+"some_prefix_7_a_name.extension");
		assertTrue("file should not exist yet; caller will write to it",!targetFile.exists());  
	}
	@Test
	public void verifyBuildsSummaryFileAtRootDirectory() throws Exception
	{
		builder = new OutputFileBuilder("scenario_root");
		File targetFile = builder.buildSummaryFile("summary_file.ext"); 
		assertEquals(targetFile.getName(), "summary_file.ext");
		assertEquals(targetFile.getCanonicalPath(), builder.getRootDirectoryName()+Constants.SLASH+"summary_file.ext");
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
			assertEquals("OutputFileBuilder.constructor:  Couldn't create directory scenario_root", e.getMessage());
		}
		try 
		{
			builder = getFailingBuilder(false); 
			builder.getRootDirectoryName();
			fail("should throw"); 
		}
		catch (OutputFileBuilderException e)
		{
			assertTrue(e.getMessage().startsWith("OutputFileBuilder.getRootDirectoryName:  IOException attempting to read or write "+"scenario_root"));
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
			super("scenario_root"); 
			if (failInConstructor) exception(OutputFileBuilder.CONSTRUCTOR, "scenario_root", null); 
		}
		@Override
			public String getRootDirectoryName() throws OutputFileBuilderException
			{
				exception(OutputFileBuilder.GET_ROOT_DIRECTORY_NAME, "scenario_root", new IOException());
				return null; 
			}
	}
}
