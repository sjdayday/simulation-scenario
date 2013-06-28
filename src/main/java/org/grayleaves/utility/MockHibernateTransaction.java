/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;

public class MockHibernateTransaction implements Transaction
{

	@Override
	public void begin() throws HibernateException
	{
	}
	@Override
	public void commit() throws HibernateException
	{
	}
	@Override
	public boolean isActive() throws HibernateException
	{
		return false;
	}
	@Override
	public void registerSynchronization(Synchronization arg0)
			throws HibernateException
	{
	}
	@Override
	public void rollback() throws HibernateException
	{
	}
	@Override
	public void setTimeout(int arg0)
	{
	}
	@Override
	public boolean wasCommitted() throws HibernateException
	{
		return false;
	}
	@Override
	public boolean wasRolledBack() throws HibernateException
	{
		return false;
	}
}
