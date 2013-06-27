/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

@SuppressWarnings("rawtypes")
public class TestingComparableClass implements Comparable
{

	public TestingComparableClass(String string)
	{
	}

	@Override
	public int compareTo(Object o)
	{
		return 0;
	}
}
