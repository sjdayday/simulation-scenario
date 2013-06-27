/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.io.File;
import java.io.IOException;





public class OutputFileBuilder 
{

	private static final String CLONE_WITH_ID = ".cloneWithId: ";
	private static final String DOT = ".";
	private static final String UNDERSCORE = "_";
	protected static final String GET_ROOT_DIRECTORY_NAME = ".getRootDirectoryName:  ";
	protected static final String CONSTRUCTOR = ".constructor:  ";
	private static final String IO_EXCEPTION_ATTEMPTING_TO_READ_OR_WRITE = "IOException attempting to read or write ";
	private static final String COULDN_T_CREATE_DIRECTORY = "Couldn't create directory ";
	private static final String OUTPUT_FILE_BUILDER = "OutputFileBuilder";
	private File rootDirectory;
	private String rootDirectoryName;
	private int id;

	public OutputFileBuilder(String rootDirectoryName) throws OutputFileBuilderException
	{
		this.rootDirectoryName = rootDirectoryName; 
		rootDirectory = new File(rootDirectoryName); 
		makeDir(rootDirectory);
	}

	protected void createDirectory(String directory) throws OutputFileBuilderException
	{
		makeDir(new File(getRootDirectoryName()+Constants.SLASH+directory)); 
	}


	public String getRootDirectoryName() throws OutputFileBuilderException 
	{
		String directory = null; 
		try
		{
			directory = rootDirectory.getCanonicalPath();
		}
		catch (IOException e)
		{
			exception(GET_ROOT_DIRECTORY_NAME, rootDirectoryName, e);
		}
		return directory; 
	}

	protected void exception(String method, String name, IOException e) throws OutputFileBuilderException
	{
		if (e == null) throw new OutputFileBuilderException(OUTPUT_FILE_BUILDER+method+COULDN_T_CREATE_DIRECTORY+name);
		else throw new OutputFileBuilderException(OUTPUT_FILE_BUILDER+method+IO_EXCEPTION_ATTEMPTING_TO_READ_OR_WRITE+name+Constants.CRLF+e.getMessage());
	}
	private void makeDir(File directory) throws OutputFileBuilderException
	{
		if (!directory.exists()) 
		{	
			if (!directory.mkdir()) exception(CONSTRUCTOR, directory.getName(), null);
		}
	}
	public OutputFileBuilder cloneWithId(int id)  
	{
		OutputFileBuilder copy = null; 
		try
		{
			copy = new OutputFileBuilder(rootDirectoryName);
		}
		catch (OutputFileBuilderException e)
		{
			throw new RuntimeException(OUTPUT_FILE_BUILDER+CLONE_WITH_ID+"unexpected OutputFileBuilderException; logic error or filesystem modified during execution: "+Constants.CRLF+e.getMessage()); 
		}  
		copy.setId(id);
		return copy;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	public int getId()
	{
		return id;
	}

	public File buildFile(String directory, String name, String prefix, String extension) throws OutputFileBuilderException
	{
		createDirectory(directory); 
		File file = new File(getRootDirectoryName()+Constants.SLASH+directory+Constants.SLASH+buildFileName(name, prefix, extension)); 
		return file; 
	}
	public File buildSummaryFile(String name) throws OutputFileBuilderException
	{
		File file = new File(getRootDirectoryName()+Constants.SLASH+name); 
		return file;
	}

	private String buildFileName(String name, String prefix, String extension)
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(prefix);
		sb.append(UNDERSCORE); 
		sb.append(getId());
		sb.append(UNDERSCORE); 
		sb.append((name != null) ? name : "no_name_provided"); 
		sb.append(DOT); 
		sb.append(extension); 
		return sb.toString();
	}

}
