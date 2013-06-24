
package org.grayleaves.utility;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class SimpleScenario<R, I> implements Scenario<R, I>
{
//	private static Logger logger = Logger.getLogger(SimpleScenario.class);
	
	protected Model<R> model;
	private int id;
	private Calendar calendar;
	private String name = "Default scenario name";
	private static final String MODEL_EXCEPTION = "ModelException: "; 
	private ParameterPoint parameterPoint;
	private Input input;
	private ScenarioLog<R, I> log;
	private ScenarioSet<R, I> scenarioSet;
	private OutputFileBuilder outputFileBuilder;
	private static final String SCENARIO = "SimpleScenario.run: ";
	private static final String MUST_HAVE = SCENARIO+"Scenario must have "; 
	private static final String BEFORE_RUN = " before it can be run.";
	//TODO Figure out how to use Logger in this class; ScenarioLog resets the log4j configuration. 
	public SimpleScenario()
	{
	}
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(id);
		sb.append(" ");
		sb.append(name); 
		return sb.toString();
	}
	@Override
	public ScenarioResult<R> run() throws ScenarioException
	{
		validate();
//		logger.debug("run; finished validate, about to update model parameters for parameter point: "+parameterPoint.verboseToString());
		parameterPoint.updateModelParameters(); 
		buildInput();
		cloneOutputFileBuilder();
		buildLog(); 
		log.log("Parameter point: "+parameterPoint.verboseToString()); 
		Result<R> result = null;
		try
		{
//			logger.debug("run; about to run model");
			result = model.run();
			logResult(result); 
			log.close();  
		} 
		catch (ModelException e)
		{
			String msg = SCENARIO+MODEL_EXCEPTION+e.getMessage();
			log.error(msg);
			throw new ScenarioException(msg); 
		}
		finally
		{
			log.close();
		}
		return new SimpleScenarioResult<R, I>(this, result);
	}
	private void cloneOutputFileBuilder() 
	{
		if (outputFileBuilder.getId() != id) outputFileBuilder = outputFileBuilder.cloneWithId(id); 
		model.setOutputFileBuilder(outputFileBuilder);
	}
	private void logResult(Result<R> result)
	{
		List<R> list = result.getList(); 
		for (R item : list)
		{
			log.log(item.toString()); 
		}
		log.trailer(result.getSummaryData());
	}
	private void buildInput() throws ScenarioException 
	{
		if (input.getFilename() != null)
		{
			Persister<Input> persister = new SimplePersister<Input>();  
			Input newInput;
			try
			{
				newInput = persister.load(Input.class, new File(input.getFilename()));
			} 
			catch (PersistenceException e)
			{
				throw new ScenarioException(SCENARIO+"FileNotFoundException attempting to build input from persisted Input filename: "+input.getFilename()); 
			}
			model.setInput(newInput);  
		}
		else model.setInput(this.input);
	}

	private void validate() throws ScenarioException
	{
		StringBuffer sb = new StringBuffer(); 
		if (model == null) sb.append("Model"); 
		if (input == null)
		{
			if (!sb.toString().equals("")) sb.append(", "); 
			sb.append("Input"); 
		}
		if (parameterPoint == null)
		{
			if (!sb.toString().equals("")) sb.append(", "); 
			sb.append("ParameterPoint"); 
		}
		if (outputFileBuilder == null)
		{
			if (!sb.toString().equals("")) sb.append(", "); 
			sb.append("OutputFileBuilder"); 
		}
		if (!sb.toString().equals("")) throw new ScenarioException(MUST_HAVE+sb.toString()+BEFORE_RUN); 
	}
	protected void buildLog() throws ScenarioException
	{
		if (log == null)
		log = new ScenarioLog<R, I>(this);
	}
	@Override
	public void setModel(Model<R> model)
	{
		this.model = model; 
	}
	@Override
	public int getId()
	{
		return id;
	}
	@Override
	public void setId(int id)
	{
		this.id = id;
	}
	@Override
	public Calendar getCalendar()
	{
		return calendar;
	}
	@Override
	public void setCalendar(Calendar calendar)
	{
		this.calendar = calendar; 
	}
	@Override
	public String getName()
	{
		return name;
	}
	@Override
	public void setName(String name)
	{
		this.name  = name;
	}
	public Model<R> getModel()
	{
		return model;
	}
	@Override
	public ParameterPoint getParameterPoint()
	{
		return parameterPoint;
	}
	@Override
	public void setParameterPoint(ParameterPoint point)
	{
		this.parameterPoint = point; 
	}
	@Override
	public Input getInput()
	{
		return input;
	}
	@Override
	public void setInput(Input input)
	{
		this.input = input; 
	}
	@Override
	public ScenarioLog<R, I> getLog()
	{
		return log;
	}
	@Override
	public void setLog(ScenarioLog<R, I> log)
	{
		this.log = log; 
	}
	@Override
	public ScenarioSet<R, I> getScenarioSet()
	{
		return scenarioSet;
	}
	@Override
	public void setScenarioSet(ScenarioSet<R, I> scenarioSet)
	{
		this.scenarioSet = scenarioSet;
	}
	@Override
	public void setOutputFileBuilder(OutputFileBuilder outputFileBuilder)
	{
		this.outputFileBuilder = outputFileBuilder;
	}
	@Override
	public OutputFileBuilder getOutputFileBuilder()
	{
		return outputFileBuilder;
	}
}
