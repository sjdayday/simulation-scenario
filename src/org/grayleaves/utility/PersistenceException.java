/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public class PersistenceException extends Exception
{

	private static final long serialVersionUID = 1L;

	public PersistenceException()
	{
	}

	public PersistenceException(String message)
	{
		super(message);

	}

	public PersistenceException(Throwable cause)
	{
		super(cause);

	}

	public PersistenceException(String message, Throwable cause)
	{
		super(message, cause);

	}

}
