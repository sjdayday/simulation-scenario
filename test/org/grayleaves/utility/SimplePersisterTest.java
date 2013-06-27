/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.ExceptionListener;
import java.io.File;

import org.grayleaves.utility.Input;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.PersistenceException;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.Persister;
import org.grayleaves.utility.Result;
import org.grayleaves.utility.SimplePersister;
import org.junit.Before;
import org.junit.Test;


public class SimplePersisterTest
{
	private Persister<Input> persister;
	
	@Before
	public void setUp() throws Exception
	{
		persister = new SimplePersister<Input>();
		File file = new File("testingInput.xml");
		if (file.exists()) file.delete(); 
	}
	
	@Test
	public void verifyInputSavedAndRetrieved() throws Exception
	{
		//TODO suppress 4 lines:  INFO utility.TestingModel:  Testing Model initializing 
		TestingBean.resetForTesting();
		persister = new SimplePersister<Input>(); 
		Persister<Input> persister2 = new SimplePersister<Input>();
//		persister.setPersistenceFilename("testingInput.xml");
		File file = persister.save(new TestingInput(17), "testingInput.xml"); 
		Input input = persister2.load(Input.class, file);  
		Model<String> model = new PersistentModel<String>(); 
		model.setInput(input); 
		model.setClazz("org.grayleaves.utility.TestingModel");
		Result<String> result = model.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 3, boolean true\n"+
				"output:  20", result.toString()); 

	}
	@Test
	public void verifyCanPersistToAndFromString() throws Exception
	{
		String persisted = persister.save(new TestingInput(18)); 
		Input input = persister.load(Input.class, persisted); 
		assertEquals(18, ((TestingInput) input).getNum());
	}
	@Test
	public void verifyCanPersistToAndFromFile() throws Exception
	{
		File file = persister.save(new TestingInput(17), "testingInput.xml");
		Input input = persister.load(Input.class, file); 
		assertEquals(17, ((TestingInput) input).getNum());
	}
	@Test
	public void verifyExceptionThrownForInvalidFile() throws Exception
	{
		@SuppressWarnings("unused")
		File file = persister.save(new TestingInput(17), "testingInput.xml");
		try 
		{
			@SuppressWarnings("unused")
			Input input = persister.load(Input.class, new File("nonexistentFile.xml")); 
			fail("should throw file not found");
		}
		catch (PersistenceException e)
		{
			assertEquals(SimplePersister.SIMPLE_PERSISTER+SimplePersister.LOAD+"nonexistentFile.xml (No such file or directory)", e.getMessage());
		}
	}
	@Test
	public void verifyPersistenceExceptionForClassThatCantBeEncoded() throws Exception
	{
		Persister<EncoderError> persister = new SimplePersister<EncoderError>(); 
		try
		{
			@SuppressWarnings("unused")
			String persisted = persister.save(new EncoderError());  
			fail("should throw Persistence Exception"); 
		}
		catch (PersistenceException e)
		{
			assertTrue(e.getMessage().startsWith(SimplePersister.SIMPLE_PERSISTER+SimplePersister.WRITE_TARGET+'\n'+"java.lang.InstantiationException"));
		}
	}
	//TODO persist List<TestingInput> -- don't think this is possible, since it's really List<Object>
	@SuppressWarnings("rawtypes")
	@Test
	public void verifyDetectsFileLoadedWithUnexpectedClass() throws Exception
	{
		String persisted = persister.save(new TestingInput(18)); 
		Persister<Model> wrongPersister = new SimplePersister<Model>(); 
		try
		{
			@SuppressWarnings("unused")
			Model model= wrongPersister.load(Model.class, persisted); 
			fail("should throw class cast");
		}
		catch (PersistenceException e)
		{
			assertTrue(e.getMessage().startsWith(SimplePersister.SIMPLE_PERSISTER+SimplePersister.READ_TARGET+SimplePersister.OBJECT_REQUESTED_NOT_SAME_AS_PREVIOUSLY_PERSISTED)); 
		}
	}
	@Test
	public void verifyErrorsDetectedAtLoadTimeGeneratePersistenceException() throws Exception
	{
		String persisted = persister.save(new TestingInput(18)); 
		Persister<TestingInput> newPersister = new SimplePersister<TestingInput>(); 
		((ExceptionListener) newPersister).exceptionThrown(new Exception("Test error")); 
		try
		{
			@SuppressWarnings("unused")
			TestingInput input = newPersister.load(TestingInput.class, persisted); 
			fail("should throw PersistenceException");
		}
		catch (PersistenceException e)
		{
			assertTrue(e.getMessage().startsWith(SimplePersister.SIMPLE_PERSISTER+SimplePersister.READ_TARGET+SimplePersister.PERSISTENCE_EXCEPTION_RECEIVED_IN_XMLDECODER_PROCESSING+'\n'+
			"java.lang.Exception: Test error")); 
		}
	}
	private class EncoderError
	{
		@SuppressWarnings("unused")
		private int fred = 6; 
		@SuppressWarnings("unused")
		private boolean question = true;
		@SuppressWarnings("unused")
		private String thing = "stuff"; 
	}
}
