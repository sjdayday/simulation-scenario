/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

public interface ScenarioAnalyzer<R, I>
{

	ComparisonResult compare(Scenario<R, I> baselineScenario, Scenario<R, I> testScenario);

	ScenarioAnalyzer<R, I> newCopy();

}
