/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.grayleaves.utility.Model;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.Result;
import org.grayleaves.utility.TestingInput;
import org.junit.Before;
import org.junit.Test;


public class ModelPersistenceTest extends AbstractHistoryTest
{
	private Model<String> model;
	@Before
	public void setUp() throws Exception
	{
		super.setUp(); 
		model = new PersistentModel<String>();
	}
	@SuppressWarnings("unchecked")
	@Test  
	public void verifySaveAndRetrieveModelFromClazzString() throws Exception
	{
		tx = getTx(); 
		List<Model<String>> list = session.createQuery("from Model").list();
		tx.commit(); 
		assertEquals(0, list.size());
		tx = getTx();
		model.setName("Persistent Test 1"); 
		model.setClazz("org.grayleaves.utility.TestingModel");
		session.save(model);
		tx.commit();
		tx = getTx(); 
		list = session.createQuery("from Model").list();
		Model<String> newModel = list.get(0); 
		tx.commit();
		assertEquals(1, list.size());
		assertEquals("Persistent Test 1", newModel.getName());
		assertTrue(newModel instanceof PersistentModel); 
		newModel.setInput(new TestingInput(17) ); // newInput 
		Result<String> result = newModel.run(); 
		assertEquals("TestingModel from input 17\n"+ 
				"parameters string fred, int 3, boolean true\n"+
				"output:  20", result.toString()); 
	}

}
