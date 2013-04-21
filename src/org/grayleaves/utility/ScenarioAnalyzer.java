package org.grayleaves.utility;

public interface ScenarioAnalyzer<R, I>
{

	ComparisonResult compare(Scenario<R, I> baselineScenario, Scenario<R, I> testScenario);

	ScenarioAnalyzer<R, I> newCopy();

}
