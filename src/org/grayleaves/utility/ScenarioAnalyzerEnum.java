package org.grayleaves.utility;

public enum ScenarioAnalyzerEnum
{
	//FIXME determine how to add type information; does this have to be a factory class instead of enum? 
	MINIMAL_SCENARIO_LOG_ANALYZER
	{
		@SuppressWarnings("rawtypes")
		public ScenarioAnalyzer getAnalyzer() { return new MinimalScenarioLogAnalyzer(); } 
	},
	SIMPLE_SCENARIO_LOG_ANALYZER
	{
		@SuppressWarnings("rawtypes")
		public ScenarioAnalyzer getAnalyzer() { return new SimpleScenarioLogAnalyzer(); }
	},
	SCENARIO_SETUP_ANALYZER
	{
		@SuppressWarnings("rawtypes")
		public ScenarioAnalyzer getAnalyzer() { return new ScenarioSetupAnalyzer(); }
	}
	;

	@SuppressWarnings("rawtypes")
	public abstract ScenarioAnalyzer getAnalyzer(); 
}
