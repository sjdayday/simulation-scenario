package org.grayleaves.utility;

import java.util.Calendar;

public interface Scenario<R, I> extends SingleParameterPoint 
{

	public void setModel(Model<R> model);
	public Model<R> getModel();

	public ScenarioResult<R> run() throws ScenarioException;


	public void setId(int i);

	public Calendar getCalendar();
	public void setCalendar(Calendar calendar);

	public String getName();
	public void setName(String string);
	public void setParameterPoint(ParameterPoint point);


	public Input getInput();
	public void setInput(Input input);
	/**
	 * Scenario implementations must log their activity in a ScenarioLog 
	 */
	public ScenarioLog<R, I> getLog();
	public void setLog(ScenarioLog<R, I> log); 
	
	public ScenarioSet<R, I> getScenarioSet();
	public void setScenarioSet(ScenarioSet<R, I> scenarioSet);

	public void setOutputFileBuilder(OutputFileBuilder outputFileBuilder);
	public OutputFileBuilder getOutputFileBuilder();
	
}
