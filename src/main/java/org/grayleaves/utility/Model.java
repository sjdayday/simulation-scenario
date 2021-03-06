/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


public interface Model<R>
{

	public Result<R> run() throws ModelException;

	public void setInput(Input input);
	public Input getInput(); 

	public int getId();
	public void setId(int id);

	public String getName();
	public void setName(String name);

	public String getClazz();
	public void setClazz(String clazz);

	public void setOutputFileBuilder(OutputFileBuilder outputFileBuilder);
	public OutputFileBuilder getOutputFileBuilder();

}
