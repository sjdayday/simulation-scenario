package org.grayleaves.utility;

import java.io.BufferedReader;

public interface Model<R>
{

	public Result<R> run() throws ModelException;

	public void setInput(Input input);
	public Input getInput(); 

	//TODO return Log? 
	public BufferedReader getLog();

	public int getId();
	public void setId(int id);

	public String getName();
	public void setName(String name);

	public String getClazz();
	public void setClazz(String clazz);

	public void setOutputFileBuilder(OutputFileBuilder outputFileBuilder);
	public OutputFileBuilder getOutputFileBuilder();

}
