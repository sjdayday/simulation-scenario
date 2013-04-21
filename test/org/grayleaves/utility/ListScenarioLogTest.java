package org.grayleaves.utility;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.grayleaves.utility.ListScenarioLog;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioException;
import org.grayleaves.utility.ScenarioLog;
import org.grayleaves.utility.SimpleScenario;
import org.junit.Before;
import org.junit.Test;

public class ListScenarioLogTest
{

	private ScenarioLog<String, TestingInput> log;
	private Scenario<String, TestingInput> scenario; 
	@Before
	public void setUp() throws Exception
	{
		scenario = new SimpleScenario<String, TestingInput>();
        MockClock.setDateForTesting("10/15/2005 12:00:14 PM");		
        scenario.setCalendar(MockClock.getCalendar());
        scenario.setId(3); 
        scenario.setName("some name");
        log = new ListScenarioLog<String, TestingInput>(scenario); 
	}
	@Test
	public void verifyNoFilenameForListLog() throws Exception
	{
		log.close(); 
		assertNull(log.getFilename()); 
//		assertEquals("Scenario_3_2005_10_15__12_00_14PM_some name.log", log.getFilename()); 
	}
	@Test
	public void verifyHeaderRecord() throws Exception
	{
		log.close(); 
		assertEquals("INFO utility.ListScenarioLog:  Scenario: some name   Scenario Id: 3   Date/Time:  2005_10_15__12_00_14PM ", log.getRecords().get(0)); 
//		printLog(); 
		assertEquals(1, log.getRecords().size()); 
		
	}
	@Test
	public void verifyRecordsAreLogged() throws Exception
	{
		log.log("some data go here");
		log.log("more data -- second line");
		log.close();
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ListScenarioLog:  some data go here ", log.getRecords().get(1));
	}
	@Test
	public void verifyErrorsAreLogged() throws Exception
	{
		log.error("An error was thrown");
		log.close();
		assertEquals("ERROR utility.ListScenarioLog:  An error was thrown ", log.getRecords().get(1));
	}
	@Test
	public void verifyTrailerPrintsRecordCountByDefault() throws Exception
	{
		log.log("some data go here");
		log.trailer(); 
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ListScenarioLog:  Scenario: some name   Scenario Id: 3   Record count: 1 ", log.getRecords().get(2));
	}
	@Test
	public void verifyTrailerPrintsCustomDetails() throws Exception
	{
		log.log("some data go here");
		log.trailer("Any custom data: blah"); 
		assertEquals(3, log.getRecords().size());
		assertEquals("INFO utility.ListScenarioLog:  Scenario: some name   Scenario Id: 3   Record count: 1   Any custom data: blah ", log.getRecords().get(2));
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
