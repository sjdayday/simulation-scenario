/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import org.grayleaves.utility.PersistentInput;

public class TestingInput extends PersistentInput 
{

	private int num;
	public TestingInput()
	{
	}
	public TestingInput(int i)
	{
		this.num = i; 
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

}
