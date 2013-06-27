/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

public class ArrayComponentOutput<O> implements ComponentOutput<O>
{

	private O[] output;

	public ArrayComponentOutput(O[] output)
	{
		this.output = output; 
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false; 
		ArrayComponentOutput<O> compareOutput = (ArrayComponentOutput<O>) obj; 
		O[] otherOutput = compareOutput.getOutput();
		if (output.length != otherOutput.length) return false; 
		for (int i = 0; i < output.length; i++)
		{
			if (!output[i].equals(otherOutput[i])) return false; 
		}
		return true;
	}
	public O[] getOutput()
	{
		return output;
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("ArrayComponentOutput ");
		for (int i = 0; i < output.length; i++)
		{
			sb.append(output[i].toString()+" ");
		}
		return sb.toString();
	}
}
