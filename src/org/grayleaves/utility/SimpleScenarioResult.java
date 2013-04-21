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
