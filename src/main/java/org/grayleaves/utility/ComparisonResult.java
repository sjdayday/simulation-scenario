/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public class ComparisonResult
{
	private String details;
	private boolean match;
	private String moreDetails = "";
	

	public ComparisonResult(boolean match, String details)
	{
		this.details = details;
		this.match = match; 
	}
	
	public ComparisonResult()
	{
	}

	public String getDetails()
	{
		return details;
	}
	public void setDetails(String details)
	{
		this.details = details;
	}
	public boolean isMatch()
	{
		return match;
	}
	public void setMatch(boolean match)
	{
		this.match = match;
	}
	public String getMoreDetails() 
	{
		return this.moreDetails ;
	} 
	public void setMoreDetails(String moreDetails) 
	{
		this.moreDetails = moreDetails;
	}
	
	
}
