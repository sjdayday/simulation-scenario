/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.grayleaves.utility.Input;
import org.grayleaves.utility.PersistentInput;
import org.junit.Before;
import org.junit.Test;


public class InputPersistenceTest extends AbstractHistoryTest
{
	private Input input;
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		input = new PersistentInput();
	}
	@SuppressWarnings("unchecked")
	@Test
	public void verifySaveAndRetrieveInputString() throws Exception
	{
		tx = getTx(); 
		List<Input> list = session.createQuery("from Input").list();
		tx.commit(); 
		assertEquals(0, list.size());
		tx = getTx();
		input.setFilename("testingInput.xml"); 
		session.save(input);
		tx.commit();
		tx = getTx(); 
		list = session.createQuery("from Input").list(); 
		tx.commit();
		assertEquals(1, list.size());
		Input newInput = list.get(0); 
		assertEquals("testingInput.xml", newInput.getFilename());
		assertTrue(newInput instanceof PersistentInput); 
	}

}
