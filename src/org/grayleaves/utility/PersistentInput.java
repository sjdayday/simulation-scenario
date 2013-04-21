package org.grayleaves.utility;



public class PersistentInput implements Input
{


	private String filename;
	private int id; 
	
	@Override
	public String getFilename()
	{
		return filename;
	}

	@Override
	public void setFilename(String filename)
	{
		this.filename = filename; 
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
