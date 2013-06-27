/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public class SortOrder
{

	private SortPair[] sortPairs;
	private int[] order;

	public SortOrder(SortPair... sortPairs)
	{
		this.sortPairs = sortPairs; 
		order = new int[sortPairs.length]; 
		for (int i = 0; i < order.length; i++)
		{
			order[i] = sortPairs[i].order; 
		}
	}
	/*
	 * Convenience constructor that defaults to sorting all fields in Ascending direction. 
	 */
	public SortOrder(int[] order)
	{
		this.order = order;
		sortPairs = new SortPair[order.length];
		for (int i = 0; i < order.length; i++)
		{
			sortPairs[i] = new SortPair(order[i], Direction.ASCENDING); 
		}
	}
	
	public int[] getOrder()
	{
		return order;
	}

	public SortPair get(int i)
	{
		return sortPairs[i];
	}

}
