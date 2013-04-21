package org.grayleaves.utility;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.grayleaves.utility.Constants;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.OutputFileBuilder;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioException;
import org.grayleaves.utility.ScenarioLog;
import org.grayleaves.utility.SimpleScenario;
import org.junit.Before;
import org.junit.Test;

public class ScenarioLogTest
{

	private ScenarioLog<String, TestingInput> log;
	private Scenario<String, TestingInput> scenario;
	private OutputFileBuilder builder; 
	@Before
	public void setUp() throws Exception
	{
		deleteTestScenarioLog(); 
		builder = new OutputFileBuilder("scenario_root");
		scenario = new SimpleScenario<String, TestingInput>();
        MockClock.setDateForTesting("10/15/2005 12:00:14 PM");		
        scenario.setCalendar(MockClock.getCalendar());
        scenario.setId(3); 
        scenario.setOutputFileBuilder(builder.cloneWithId(scenario.getId()));
        scenario.setName("some name");
        //TODO determine which class should be responsible for log4j configuration
//        BasicConfigurator.resetConfiguration();  // moved from configureAppender
//        log = new ScenarioLog(3, MockClock.getCalendar(), "some name"); 
        log = new ScenarioLog<String, TestingInput>(scenario); 
	}
	@Test
	public void verifyHeaderHasScenarioIdDateTimeName() throws Exception
	{
		log.close(); 
		assertEquals(builder.getRootDirectoryName()+Constants.SLASH+"logs"+Constants.SLASH+"Scenario_3_2005_10_15__12_00_14PM_some name.log", log.getFilename()); 
	}
	@Test
	public void verifyHeaderRecord() throws Exception
	{
		log.close(); 
		assertEquals("INFO utility.ScenarioLog:  Scenario: some name   Scenario Id: 3   Date/Time:  2005_10_15__12_00_14PM ", log.getRecords().get(0)); 
		assertEquals(1, log.getRecords().size()); 
	}
	@Test
	public void verifyRecordsAreLogged() throws Exception
	{
		log.log("some data go here");
		log.log("more data -- second line");
		log.close();
//		printLog(); 
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ScenarioLog:  some data go here ", log.getRecords().get(1));
	}
	@Test
	public void verifyErrorsAreLogged() throws Exception
	{
		log.error("An error was thrown");
		log.close();
		assertEquals("ERROR utility.ScenarioLog:  An error was thrown ", log.getRecords().get(1));
	}
	@Test
	public void verifyTrailerPrintsRecordCountByDefault() throws Exception
	{
		log.log("some data go here");
		log.trailer(); 
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ScenarioLog:  Scenario: some name   Scenario Id: 3   Record count: 1 ", log.getRecords().get(2));
	}
	@Test
	public void verifyNewLogCanBeCreatedFromPreviouslyWrittenFile() throws Exception
	{
		log.log("some data go here");
		log.trailer(); 
		String filename = log.getFilename(); 
		ScenarioLog<String, TestingInput> newLog = new ScenarioLog<String, TestingInput>(); 
		newLog.setFilename(filename); 
		assertEquals(3, newLog.getRecords().size());
		assertEquals("INFO utility.ScenarioLog:  Scenario: some name   Scenario Id: 3   Record count: 1 ", newLog.getRecords().get(2));
	}
	@Test
	public void verifyTrailerPrintsCustomDetails() throws Exception
	{
		log.log("some data go here");
		log.trailer("Any custom data: blah"); 
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ScenarioLog:  Scenario: some name   Scenario Id: 3   Record count: 1   Any custom data: blah ", log.getRecords().get(2));
	}
	@Test
	public void verifyLogBeforeAndAfterTrailer() throws Exception
	{
		log.log("some data go here");
		log.log("more data -- second line");
		assertTrue(!log.getHasTrailer());
		assertEquals(2, log.getRecordCount());
		log.trailer("Custom data:  fred"); 
		assertTrue(log.getHasTrailer());
		assertEquals(2, log.getRecordCount()); 
		assertEquals("Custom data:  fred", log.getCustomData()); 
	}
	@Test
	public void verifyCantLogAfterClose() throws Exception
	{
		assertTrue(log.log("some data go here"));
		log.close(); 
		assertTrue("can't log after close",!log.log("more data after trailer"));
	}
	@Test
	public void verifyCantLogAfterTrailerBecauseTrailerIncorporatesClose() throws Exception
	{
		assertTrue(log.log("some data go here"));
		log.trailer(); 
		assertTrue("can't log after trailer",!log.log("more data after trailer"));
	}
	@Test
	public void verifyLogThrowsAppropriateErrorIfNotInitializedCorrectly() throws Exception
	{
		deleteTestScenarioLog();
		ScenarioLog<String, TestingInput> log = getUninitializedLog(); 
		try
		{
			log.configureAppender(); 
			fail("should throw");
		}
		catch (ScenarioException e)
		{
			assertTrue(e.getMessage().startsWith("ScenarioException:  IOException in ScenarioLog.configureAppender:  "));
		}
	}
	@Test 
	public void verifyExceptionThrownWhenFileNotFound() throws Exception
	{
		deleteTestScenarioLog();
		log = new ScenarioLog<String, TestingInput>(); 
		log.setFilename("invalidFile.txt"); 
		try
		{
			log.getRecords();
			fail("should throw");
		}
		catch (ScenarioException e)
		{
			assertTrue(e.getMessage().startsWith("ScenarioLog.buildRecordsFromLog File not found:"));
		}
	}
	private void deleteTestScenarioLog() throws Exception
	{
		if (log!= null) log.close();
		TestingFileBuilder.cleanUpDirectory("scenario_root"); 
//		File file = new File("Scenario_3_2005_10_15__12_00_14PM_some name.log");
//		if (file.exists())	
//			assertTrue("couldn't delete file",file.delete());
	}
	public ScenarioLog<String, TestingInput> getUninitializedLog()
	{
		return new ScenarioLog<String, TestingInput>()
		{
			// anonymous inner override
			private static final String LAYOUT = "%p %c{2}:  %m %n";
			@SuppressWarnings("unused")
			private FileAppender appender;
			protected void configureAppender() throws ScenarioException 
			{
				try
				{
					appender = new FileAppender(new EnhancedPatternLayout(LAYOUT), "");
				} 
				catch (IOException e)
				{
					throw new ScenarioException("ScenarioException:  IOException in ScenarioLog.configureAppender:  "+e.getMessage()); 
				}
			}
		};
	}
	@SuppressWarnings("unused")
	private void printLog() throws ScenarioException
	{
		List<String> records = log.getRecords(); 
		for (String record : records)
		{
			System.out.println(record);
		}
	}
}
