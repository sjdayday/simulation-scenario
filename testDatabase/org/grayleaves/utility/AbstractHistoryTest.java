package org.grayleaves.utility;

import java.io.File;

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
		session = HibernateUtil.getSessionFactory().getCurrentSession(); 
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
