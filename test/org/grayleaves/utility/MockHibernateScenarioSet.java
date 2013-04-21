package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

import org.grayleaves.utility.HibernateRetrievable;
import org.grayleaves.utility.Scenario;
import org.grayleaves.utility.ScenarioSet;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MockHibernateScenarioSet<R, I> extends ScenarioSet<R, I>
{

	public MockHibernateScenarioSet()
	{
	}

	public MockHibernateScenarioSet(boolean listLog)
	{
		super(listLog);

	}
	@Override
	protected Transaction getTx()
	{
		tx = new MockHibernateTransaction();
		return tx;
	}
	@Override
	protected Session getSession()
	{
		if (session == null)
		{
			session = new MockHibernateSession(getRetrievableScenarios());
		}
		return super.getSession();
	}
//	scenarioSet.setSessionForTesting(new MockHibernateSession(scenarioSet.getRetrievableScenarios())); 
//	public void setSessionForTesting(Session session)
//	{
//		mockTx = true; 
//		this.session = session;
//		this.tx = new MockHibernateTransaction();
//	}
//	public List<HibernateRetrievable> getRetrievableScenarios()
//	{
//		retrievableScenarios = new ArrayList<HibernateRetrievable>(); 
//		for (Scenario<R, I> scenario : scenarios)
//		{
//			retrievableScenarios.add((HibernateRetrievable) scenario);
//		}
//		return retrievableScenarios; 
//	}
//	protected Session getSession()
//	{
//		if (session == null)
//		{
//			session = HibernateUtil.getSessionFactory().getCurrentSession();
//		}
//		return session;
//	}

	public List<HibernateRetrievable> getRetrievableScenarios()
	{
		retrievableScenarios = new ArrayList<HibernateRetrievable>(); 
		for (Scenario<R, I> scenario : scenarios)
		{
			retrievableScenarios.add((HibernateRetrievable) scenario);
		}
		return retrievableScenarios; 
	}
}
