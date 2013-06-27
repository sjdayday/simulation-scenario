/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public class Pattern 
{

	private boolean[] input;
	private int place;

	public Pattern(boolean[] input)
	{
		this.input = input; 
		if (this.input==null) this.input = new boolean[]{false};
		place = 0; 
	}
	public Pattern(Boolean[] input)
	{
		if (input == null) input = new Boolean[]{false}; 
		this.input = new boolean[input.length]; 
		for (int i = 0; i < input.length; i++)
		{
			this.input[i] = input[i]; 
		}
	}
	public boolean nextBoolean()
	{
//		System.out.println("length input: "+input.length);
		boolean result = input[place]; 
		if (++place == input.length) place = 0; 
		return result; 
	}
	public Boolean[] toArray(int length)
	{
		int savePlace = place; 
		place = 0; 
		Boolean[] array = new Boolean[length]; 
		for (int i = 0; i < array.length; i++)
		{
			array[i] = nextBoolean(); 
		}
		place = savePlace; 
		return array;
	}
	@Override
	public boolean equals(Object obj)
	{
		boolean match = true; 
		Pattern pattern = null;
		try 
		{
			pattern = (Pattern) obj; 
		}
		catch (ClassCastException e)
		{
			return false; 
		}
		if (pattern == null) return false;  
		if (this.input.length != pattern.input.length) return false;  
		for (int i = 0; i < input.length; i++)
		{
			if (input[i] != pattern.input[i]) return false; 
		}
		return match;
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < input.length; i++)
		{
			sb.append(input[i]);
			if (i < input.length-1) sb.append(", "); 
		}
		return sb.toString();
	}
	/* required for persistence */
	public Pattern()
	{
	}
	public boolean[] getInput()
	{
		return input;
	}
	public void setInput(boolean[] input)
	{
		this.input = input;
	}
	public int getPlace()
	{
		return place;
	}
	public void setPlace(int place)
	{
		this.place = place;
	}
}
