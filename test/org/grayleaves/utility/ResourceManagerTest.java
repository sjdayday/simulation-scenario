package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;

import org.grayleaves.utility.Listener;
import org.grayleaves.utility.LogEvent;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.ResourceManager;
import org.grayleaves.utility.ScenarioSet;
import org.grayleaves.utility.SimulationListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceManagerTest
{
	@Before
	public void setUp() throws Exception
	{
		ResourceManager.resetForTesting(); 
		TestingFileBuilder.buildPersistentInput(); 
	}
	@After
	public void tearDown() throws Exception
	{
		ResourceManager.resetForTesting(); 
	}
	
	@Test
	public void verifyListenerNotifiedOfResourceEvents() throws Exception
	{
		assertEquals(0, ResourceManager.getListeners().size()); 
		ResourceManager.echoEventForTesting(new LogEvent("echo this")); // nobody's listening for this event yet
		Listener listener = new SimulationListener(); 
		ResourceManager.addListener(listener); 
		assertEquals(1, ResourceManager.getListeners().size()); 
		ResourceManager.echoEventForTesting(new LogEvent("echo this")); 
		assertEquals("echo this", listener.getEvents().get(0).toString());
	}
	@Test
	public void verifyListenerMustBeExplicitlyRemoved() throws Exception
	{
		addListener();
		assertEquals("although listener is out of scope, it hasn't gone away",1, ResourceManager.getListeners().size()); 
		ResourceManager.echoEventForTesting(new LogEvent("echo this"));  // listener is out of program scope but RM doesn't care
		ResourceManager.removeListener(ResourceManager.getListeners().get(0));
		assertEquals(0, ResourceManager.getListeners().size()); 
	}
	private void addListener()
	{
		assertEquals(0, ResourceManager.getListeners().size()); 
		Listener listener = new SimulationListener(); 
		ResourceManager.addListener(listener); 
		assertEquals(1, ResourceManager.getListeners().size()); 
	}
	@Test
	public void verifyManuallyResourceManagerWaitsBeforeDispatchingScenarioSetAfterThreshold() throws Exception
	{
		MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
		Listener listener = new SimulationListener(); 
		ResourceManager.addListener(listener); 
		ScenarioSet<String, TestingInput> scenarioSet = new ScenarioSet<String, TestingInput>();
		scenarioSet.setName("test scenario set");
		ResourceManager.registerScenarioSet(scenarioSet); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set registered.", listener.getEvents().get(0).toString()); 
		ResourceManager.SCENARIO_SET_DELAY_THRESHOLD = 2;
		ResourceManager.SCENARIO_SET_DELAY_MILLISECONDS = 1;
		ResourceManager.run(scenarioSet); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after no delay", listener.getEvents().get(1).toString()); 
		ResourceManager.run(scenarioSet); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after no delay", listener.getEvents().get(2).toString()); 
		ResourceManager.run(scenarioSet); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after delay of 1 milliseconds", listener.getEvents().get(3).toString()); 
	}
	@Test
	public void verifyAutomaticallyResourceManagerWaitsBeforeDispatchingScenarioSetAfterThresholdAndThenContinuesToWaitHenceforward() throws Exception
	{
		MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
		Listener listener = new SimulationListener(); 
		ResourceManager.addListener(listener); 
		ScenarioSetTest test = new ScenarioSetTest();
		test.setUp(); 
		ResourceManager.SCENARIO_SET_DELAY_THRESHOLD = 2;
		ResourceManager.SCENARIO_SET_DELAY_MILLISECONDS = 1;
		test.scenarioSet.run(); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set registered.", listener.getEvents().get(0).toString()); 
		assertEquals(5, listener.getEvents().size());
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after no delay", listener.getEvents().get(1).toString()); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after no delay", listener.getEvents().get(2).toString()); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after delay of 1 milliseconds", listener.getEvents().get(3).toString()); 
		assertEquals("2005-10-15 12:00:14.000 ScenarioSet test scenario set running next scenario after delay of 1 milliseconds", listener.getEvents().get(4).toString()); 
	}
	//TODO verifyScenarioSetRegisteredImplicitlyIfNotRegisteredExplicitly, verifyMultiProgrammingLevelControlledAcrossThreads
}
