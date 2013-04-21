package org.grayleaves.utility;


public class ScenarioSetupAnalyzer<R, I> extends AbstractScenarioAnalyzer<R, I>
{
//	private static Logger logger = Logger.getLogger(ScenarioSetupAnalyzer.class);
	private static final String MODEL = "Model";
	private static final String MODELS = "Models";
	private static final String INPUT = "Input";
	private static final String INPUTS = "Inputs";
	private static final String PARAMETER_POINT = "Parameter point";
	private static final String PARAMETER_POINTS = "Parameter points";
	protected void checkMatches()
	{
		matchModel();
		matchInput();
		matchParameterPoint();
	}
	private void matchModel()
	{
		if (!(baselineScenario.getModel().getClass().getName().equals(testScenario.getModel().getClass().getName())))
		{
			mismatches.add(mismatch(MODEL, MODELS, this.testScenario.getModel().getClass().getName(), this.baselineScenario.getModel().getClass().getName()));
			match = false;
		}
		else
		{
			matches.add(match(MODELS, this.testScenario.getModel().getClass().getName()));
		}
	}
	private void matchInput()
	{
		if (!(baselineScenario.getInput().getFilename().equals(testScenario.getInput().getFilename())))
		{
			mismatches.add(mismatch(INPUT, INPUTS, this.testScenario.getInput().getFilename(), this.baselineScenario.getInput().getFilename()));
			match = false;
		}
		else
		{
			matches.add(match(INPUTS, this.testScenario.getInput().getFilename()));
		}
		
	}
	private void matchParameterPoint()
	{
		if (!(baselineScenario.getParameterPoint().toString().equals(testScenario.getParameterPoint().toString())))
		{
			mismatches.add(mismatch(PARAMETER_POINT, PARAMETER_POINTS, this.testScenario.getParameterPoint().toString(), this.baselineScenario.getParameterPoint().toString()));
			match = false;
		}
		else
		{
			matches.add(match(PARAMETER_POINTS, this.testScenario.getParameterPoint().toString()));
		}
	}
	protected void appendDetails()
	{
		details.append(match(MODELS, this.testScenario.getModel().getClass().getName()));
		details.append(match(INPUTS, this.testScenario.getInput().getFilename()));
		details.append(match(PARAMETER_POINTS, this.testScenario.getParameterPoint().toString()));
	}
}
