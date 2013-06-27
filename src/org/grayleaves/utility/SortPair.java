/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

/**
 * Defines in what order a given field from a set of fields will be sorted, and whether in ascending or descending direction.  
 * @author stevedoubleday
 *
 */
public class SortPair
{
	public final int order;
	public final int direction;
	public SortPair(int order, Direction direction)
	{
		this.order = order; 
		this.direction = direction.getDirection(); 
	}
	/**
	 * Convenience constructor that defaults to Direction.ASCENDING
	 * @param order
	 */
	public SortPair(int order)
	{
		this(order, Direction.ASCENDING); 
	}
}
