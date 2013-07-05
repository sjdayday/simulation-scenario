/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.PersistentInput;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioSet;
import org.junit.Before;
import org.junit.Test;


public class ScenarioSetPersistenceTest extends AbstractHistoryTest
{
	private ScenarioSet<String, PersistentInput> scenarioSet;
//	private ParameterSpace space;
//	private Input input;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		buildParameterSpace(); 
//		space = ScenarioSetTest.buildSmallParameterSpace(); 
		createModel();
		buildPersistentInput();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void verifyScenarioSetIsPersistedAndRetrieved() throws Exception
	{
		tx = getTx(); 
		scenarioSet = new ScenarioSet<String, PersistentInput>(false); 
		scenarioSet.setName("test scenario set"); 
		scenarioSet.setModel(model);
		scenarioSet.setInput(persistentInput);
		scenarioSet.setParameterSpace(space); 
		scenarioSet.setCalendar(MockClock.getCalendar()); 
		scenarioSet.buildParameterSpace(); 
		session.save(scenarioSet.getParameterSpace());
		tx.commit(); 
		tx = getTx(); 
		scenarioSet.buildScenarios(); 
		session.save(scenarioSet); 
		tx.commit(); 
		tx=getTx();
		scenarioSet.runScenarios(); 
//		session.merge(scenarioSet); 
		session.update(scenarioSet); 
		tx.commit(); 
		int id = scenarioSet.getId(); 
//		System.out.println("scenarioSet id: "+scenarioSet.getId());
		tx=getTx();
		ScenarioSet<String, PersistentInput> set = (ScenarioSet) session.get(ScenarioSet.class, id); 
		assertEquals("test scenario set", set.getName());
		assertEquals(id, set.getId()); 
//		List<ScenarioSet> scenarios = session.createQuery("from ScenarioSet").list(); 
		List<Scenario<String, PersistentInput>> list = set.getScenarios(); // scenarios.get(0).getScenarios();
		assertEquals("fred, 40", list.get(0).getParameterPoint().toString()); 
		assertEquals("fred, 50", list.get(1).getParameterPoint().toString());
		assertEquals("sam, 40", list.get(2).getParameterPoint().toString());
		assertEquals("sam, 50", list.get(3).getParameterPoint().toString());
		tx.commit();
//		List<ScenarioResult<String>> results = scenarioSet.getScenarioResults(); 
//		assertEquals(0, results.size());
//		results = scenarioSet.getScenarioResults(); 
//		assertEquals(4, results.size());
//		assertEquals("TestingModel from input 3\n"+ 
//				"parameters string fred, int 40, boolean true\n"+
//				"output:  43", results.get(0).getResult().toString()); 
//		assertEquals("TestingModel from input 3\n"+ 
//				"parameters string fred, int 50, boolean true\n"+
//				"output:  53", results.get(1).getResult().toString()); 
//		assertEquals("TestingModel from input 3\n"+ 
//				"parameters string sam, int 40, boolean true\n"+
//				"output:  43", results.get(2).getResult().toString()); 
//		assertEquals("TestingModel from input 3\n"+ 
//				"parameters string sam, int 50, boolean true\n"+
//				"output:  53", results.get(3).getResult().toString()); 

	}
}
