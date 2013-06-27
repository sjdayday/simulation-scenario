/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;



public class SimpleScenarioResult<R, I> implements ScenarioResult<R>
{


	private Result<R> result;

	public SimpleScenarioResult(Scenario<R, I> simpleScenario, Result<R> result)
	{
		this.result = result; 
	}

	@Override
	public Result<R> getResult()
	{
		return result;
	}

}
