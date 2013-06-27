/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScenarioPersistenceTest extends AbstractHistoryTest
{
	private Scenario<String, PersistentInput> scenario; 
	private ParameterIterator iterator;
	private String filename;
	private OutputFileBuilder builder;
	@Before
	public void setUp() throws Exception
	{
		super.setUp(); 
		ScenarioTest test = new ScenarioTest();
		iterator = test.buildParameterIterator(); 
		TestingFileBuilder.cleanUpDirectory("scenario_root"); 
	}
	@SuppressWarnings("unchecked")
	@Test
	public void verifyScenarioCreatedWithModel() throws Exception
	{
		builder = new OutputFileBuilder("scenario_root"); 
		int id = createModel(); 
		buildPersistentInput();
		tx = getTx();
		scenario = new SimpleScenario<String, PersistentInput>(); 
		scenario.setName("test 2"); 
		scenario.setCalendar(MockClock.getCalendar()); 
		scenario.setModel(model); 
		scenario.setInput(persistentInput);
		scenario.setParameterPoint(iterator.next());  //TODO persist this 
		scenario.setOutputFileBuilder(builder); 
		session.save(scenario); 
//		System.out.println("id "+scenario.getId()); 
		ScenarioResult<String> result = scenario.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string tom, int 6, boolean true\n"+
				"output:  23", result.getResult().toString()); 
		assertEquals(builder.getRootDirectoryName()+Constants.SLASH+"logs"+Constants.SLASH+"Scenario_"+scenario.getId()+"_2005_10_15__12_00_14PM_test 2.log", scenario.getLog().getFilename());
		assertEquals(4, scenario.getLog().getRecordCount()); 
		assertEquals("some custom data=23, another=true, last=random string", scenario.getLog().getCustomData()); 
		assertEquals("INFO utility.ScenarioLog:  TestingModel from input 17 ",scenario.getLog().getRecords().get(2));
		tx.commit(); 
		tx = getTx(); 
		List<Scenario<String, PersistentInput>> list = session.createQuery("from Scenario").list(); 
		assertEquals(1, list.size()); 
		Scenario<String, PersistentInput> newScenario = list.get(0); 
		assertEquals("test 2", newScenario.getName()); 
		assertEquals("Test 1", newScenario.getModel().getName());
		assertEquals("Integer Name=6, String Public Name=tom, Int2 Public Name=0", newScenario.getParameterPoint().verboseToString());
		assertEquals(id, newScenario.getModel().getId());
		filename = newScenario.getLog().getFilename();
		assertEquals(builder.getRootDirectoryName()+Constants.SLASH+"logs"+Constants.SLASH+"Scenario_"+newScenario.getId()+"_2005_10_15__12_00_14PM_test 2.log", filename ); 
		assertEquals(4, newScenario.getLog().getRecordCount()); 
		assertEquals("some custom data=23, another=true, last=random string", newScenario.getLog().getCustomData()); 

		tx.commit();
	}
	@After
	public void tearDown()
	{
		File file = new File(filename);
		if (file.exists()) file.delete();
	}
}
