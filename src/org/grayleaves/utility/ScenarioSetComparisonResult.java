package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

public class ScenarioSetComparisonResult
{
	private List<ComparisonResult> comparisonResults;
	private String summary;
	private ParameterSpaceMapperEnum overlap;
	private String overlapDetails;

	public ScenarioSetComparisonResult()
	{
		comparisonResults = new ArrayList<ComparisonResult>(); 
	}
	public void addComparisonResult(ComparisonResult comparisonResult)
	{
		comparisonResults.add(comparisonResult); 
	}
	public List<ComparisonResult> getComparisonResults()
	{
		return comparisonResults;
	}
	public String getSummary()
	{
		return summary;
	}
	public void setSummary(String summary)
	{
		this.summary = summary; 
	}
	public ParameterSpaceMapperEnum getOverlap()
	{
		return this.overlap;
	}
	public void setOverlap(ParameterSpaceMapperEnum overlap)
	{
		this.overlap = overlap;
	}
	public String getOverlapDetails()
	{
		return this.overlapDetails;
	}
	public void setOverlapDetails(String overlapDetails)
	{
		this.overlapDetails = overlapDetails;
	}
	
}
