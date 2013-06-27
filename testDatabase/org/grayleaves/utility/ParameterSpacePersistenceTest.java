/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ParameterSpacePersistenceTest extends AbstractHistoryTest
{

	private ParameterSpace space;
	private ParameterSpacePersister<ParameterSpace> spacePersister;

	@Before
	public void setUp() throws Exception
	{
		super.setUp(); 
		cleanupFile(); 
		space = new ParameterSpace(); 
		space.setName("test name");
		space.setFilename("parameterSpace.xml"); 
		space.loadProperties("org/grayleaves/utility/testing.properties");
//		spacePersister = new ParameterSpacePersister<ParameterSpace>(space);
//		spacePersister.save(); 
		spacePersister = new ParameterSpacePersister<ParameterSpace>();
		spacePersister.save(space, space.getFilename()); 
//		checkExceptionsThrown("decoder");
//		assertEquals(6, newSpace.getParameter(0).next());
//		assertEquals("fred", newSpace.getParameter(1).next()); 
//		assertEquals(0, newSpace.getParameter(2).next());

	}
	protected void cleanupFile()
	{
		File file = new File("parameterSpace.xml");
		if (file.exists()) file.delete();
	}
	@SuppressWarnings("unchecked")
	@Test
	public void verifyParameterSpaceFileIsPersistedThenMatchesOriginal() throws Exception
	{
		tx = getTx(); 
		List<ParameterSpace> parms = session.createQuery("from ParameterSpace").list(); 
		tx.commit(); 
		assertEquals(0, parms.size()); 
		tx = getTx(); 
		session.save(space); 
		tx.commit(); 
		tx = getTx(); 
		parms = session.createQuery("from ParameterSpace").list();
		tx.commit(); 
		ParameterSpace newSpace = parms.get(0); 
		assertEquals("parameterSpace.xml", newSpace.getFilename());
		assertEquals("test name", newSpace.getName()); 
	}
}
