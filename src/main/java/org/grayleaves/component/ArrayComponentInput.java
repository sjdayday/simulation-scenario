/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

public class ArrayComponentInput<I> implements ComponentInput<I>
{

	private I[] input;
	private String filename;
	private int id;

	public ArrayComponentInput(I[] input)
	{
		this.input = input; 
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false; 
		ArrayComponentInput<I> compareInput = (ArrayComponentInput<I>) obj; 
		I[] otherTypes = compareInput.getInput();
		if (input.length != otherTypes.length) return false; 
		for (int i = 0; i < input.length; i++)
		{
			if (!input[i].equals(otherTypes[i])) return false; 
		}
		return true;
	}
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
	@Override
	public I[] getInput()
	{
		return input;
	}
	@Override
	public int getId()
	{
		return id;
	}
	@Override
	public void setId(int id)
	{
		this.id = id;
	}
}
