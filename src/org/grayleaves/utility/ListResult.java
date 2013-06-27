/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


import java.util.ArrayList;
import java.util.List;

public class ListResult<R> implements Result<R>
{


	private ArrayList<R> list;
	private String summaryData;

	public ListResult()
	{
		this.list = new ArrayList<R>(); 
	}
	
	@Override
	public void add(R result)
	{
		list.add(result);
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < list.size(); i++)
		{
			sb.append(list.get(i).toString()); 
			if (i < list.size()-1) sb.append("\n"); 
			
		}
		return sb.toString();
	}

	@Override
	public List<R> getList()
	{
		return list;
	}

	@Override
	public void setSummaryData(String summaryData)
	{
		this.summaryData = summaryData; 
	}

	@Override
	public String getSummaryData()
	{
		return (summaryData != null) ? summaryData : "";   
	}

}
