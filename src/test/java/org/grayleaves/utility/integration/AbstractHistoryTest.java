/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility.integration;

import java.io.File;

import org.grayleaves.utility.HibernateUtil;
import org.grayleaves.utility.Input;
import org.grayleaves.utility.MockClock;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.PersistentInput;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.TestingBean;
import org.grayleaves.utility.TestingFileBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;

public class AbstractHistoryTest
{

	protected Session session;
	protected Transaction tx;
	protected Model<String> model;
	protected Input persistentInput;
	protected ParameterSpace space;

	@Before
	public void setUp() throws Exception
	{
		TestingBean.resetForTesting();
		cleanUpTables();
		MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
	}
	protected void cleanUpTables()
	{
		tx = getTx(); 
		session.createQuery("delete Scenario").executeUpdate();
		session.createQuery("delete ScenarioSet").executeUpdate();
		session.createQuery("delete Model").executeUpdate();
		session.createQuery("delete Input").executeUpdate();
		session.createQuery("delete ParameterSpace").executeUpdate();
		tx.commit();
	}
	protected Transaction getTx()
	{
		session = HibernateUtil.getSessionFactory().getCurrentSession();  //openSession(); 
		return session.beginTransaction();
	}
	protected int createModel()
	{
		tx = getTx(); 
		model = new PersistentModel<String>();
		model.setClazz("org.grayleaves.utility.TestingModel");
		model.setName("Test 1"); 
		session.save(model); 
		tx.commit();
		return model.getId(); 
	}
	public void buildPersistentInput() throws Exception
	{
		tx = getTx(); 
		persistentInput = new PersistentInput(); 
		persistentInput.setFilename(TestingFileBuilder.buildPersistentInput());
		session.save(persistentInput);
		tx.commit(); 
	}

	public void buildParameterSpace() throws Exception
	{
//		buildPersistentParameterSpace(TestingFileBuilder.buildParameterSpaceFilename(true), new ParameterSpace()); 
		buildPersistentParameterSpace(TestingFileBuilder.buildParameterSpaceFilename(true), TestingFileBuilder.buildParameterSpace(true)); 

	}
	public ParameterSpace buildPersistentParameterSpace(String filename, ParameterSpace space) throws Exception
	{
		tx = getTx(); 
		this.space = space; 
		this.space.setFilename(filename); 
		session.save(this.space);
		tx.commit();
		TestingFileBuilder.persistParameterSpace(this.space, filename);
		return this.space; 
	}
	public void cleanUpFile(String filename)
	{
		File file = new File(filename);
		if (file.exists()) file.delete();
	}

}
