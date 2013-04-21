package org.grayleaves.utility;

import java.io.File;

public interface Persister<P>
{

	public String save(P target) throws PersistenceException;
	public P load(Class<P> clazz, String persisted) throws PersistenceException;

	public File save(P target, String filename) throws PersistenceException;
	public P load(Class<P> clazz, File file) throws PersistenceException;

}