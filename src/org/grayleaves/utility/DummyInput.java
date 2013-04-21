package org.grayleaves.utility;

public class DummyInput implements Input
{

	@Override
	public int getId()
	{
		return 0;
	}

	@Override
	public void setId(int i)
	{
	}

	@Override
	public String getFilename()
	{
//		return "Dummy";
		return null;
	}

	@Override
	public void setFilename(String filename)
	{
	}

}
