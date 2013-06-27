/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.List;

public interface Result<R>
{
	public void add(R result);
	
	public List<R> getList();

	public void setSummaryData(String summaryData);

	public String getSummaryData(); 
	
	//TODO consider adding list of files 
}
