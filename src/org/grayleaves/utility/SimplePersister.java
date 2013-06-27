/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public  class SimplePersister<P> implements ExceptionListener, Persister<P>
{

	protected static final String OBJECT_REQUESTED_NOT_SAME_AS_PREVIOUSLY_PERSISTED = "The object that is being requested by the load is not the same type as was previously persisted: ";
	protected static final String SIMPLE_PERSISTER = "SimplePersister.";
	protected static final String SAVE = "save: ";
	protected static final String LOAD = "load: ";
	protected static final String READ_TARGET = "readTarget: ";
	protected static final String WRITE_TARGET = "writeTarget: ";
	public static final String PERSISTENCE_EXCEPTION_RECEIVED_IN_XMLDECODER_PROCESSING = "PersistenceException received in XMLDecoder processing.  Consider whether the object represented by the persistence file may have changed its names or properties since the file was created: ";
	protected boolean persistenceExceptionThrown;
	protected StringBuffer persistenceExceptions;
	protected P target;
	protected XMLEncoder encoder;
	private XMLDecoder decoder;

	public SimplePersister()
	{
		persistenceExceptionThrown = false; 
		persistenceExceptions = new StringBuffer(); 
	}
	@Override
	public String save(P target) throws PersistenceException
	{
		this.target = target; 
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		encoder = new XMLEncoder(bos);
		writeTarget();
		return bos.toString();
	}
	@Override
	public File save(P target, String filename) throws PersistenceException
	{
		this.target = target; 
		File file = new File(filename); 
		try
		{
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
		}
		catch (FileNotFoundException e)
		{
			throw new PersistenceException(SIMPLE_PERSISTER+SAVE+e.getMessage());
		}
		writeTarget();
		return file;
	}
	private void writeTarget() throws PersistenceException
	{
		encoder.setExceptionListener(this);
		setupDelegates();
		encoder.writeObject(target); 
		encoder.close();
		if (isPersistenceExceptionThrown()) 
			throw new PersistenceException(SIMPLE_PERSISTER+WRITE_TARGET+'\n'+getPersistenceExceptions().toString()); 
	}
	@Override
	public P load(Class<P> clazz, String persisted) throws PersistenceException
	{
		decoder = new XMLDecoder(new ByteArrayInputStream(persisted.getBytes())); 
		return readTarget(clazz);
	}
	@Override
	public P load(Class<P> clazz, File file) throws PersistenceException
	{
		try
		{
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
		}
		catch (FileNotFoundException e)
		{
			throw new PersistenceException(SIMPLE_PERSISTER+LOAD+e.getMessage());
		}
		return readTarget(clazz); 
		
	}
	private P readTarget(Class<P> clazz) throws PersistenceException
	{
		decoder.setExceptionListener(this); 
		try 
		{
			target = (P) clazz.cast(decoder.readObject());
		}
		catch (ClassCastException e)
		{
			throw new PersistenceException(SIMPLE_PERSISTER+READ_TARGET+OBJECT_REQUESTED_NOT_SAME_AS_PREVIOUSLY_PERSISTED+e.getMessage());
		}
		decoder.close();
		if (isPersistenceExceptionThrown()) 
			throw new PersistenceException(SIMPLE_PERSISTER+READ_TARGET+PERSISTENCE_EXCEPTION_RECEIVED_IN_XMLDECODER_PROCESSING+'\n'+getPersistenceExceptions().toString()); 
		return target;
	}
	@Override
	public void exceptionThrown(Exception e)
	{
		persistenceExceptionThrown = true;
		persistenceExceptions.append(e.toString()+'\n');
		persistenceExceptions.append(e.getMessage()+'\n');
		StackTraceElement[] elements = e.getStackTrace(); 
		for (int i = 0; i < elements.length; i++)
		{
			persistenceExceptions.append(elements[i].toString()+"\n");
		}
	}
	private boolean isPersistenceExceptionThrown()
	{
		return persistenceExceptionThrown;
	}
	private StringBuffer getPersistenceExceptions()
	{
		return persistenceExceptions;
	}
	protected void setupDelegates()
	{
	}


}
