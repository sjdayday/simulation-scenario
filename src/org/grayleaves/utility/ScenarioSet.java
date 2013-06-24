package org.grayleaves.utility;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ScenarioSet<R, I> 
{
//	public static boolean DEBUG = true;
	private static Logger logger = Logger.getLogger(ScenarioSet.class);
	private static final String BUILD_SUMMARY_HEADER_REQUIRED_BEFORE_BATCH_RUN = "build summary header required before batch run.";
	private static final String PARAMETER_SPACE_REQUIRED_BEFORE_BATCH_RUN = "set parameter space required before batch run.";
	private static final String VALIDATE = "validate:  ";
	private static final String EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE = "exception encountered attempting to write statistics file:";
	private static final String BUILD_SUMMARY = "buildSummary:  ";
	private static final String PERSISTENCE_EXCEPTION = "PersistenceException ";
	private static final String ATTEMPTING_TO_BUILD_PARAMETER_SPACE_FROM_PERSISTED_PARAMETER_SPACE_FILENAME = "attempting to build parameter space from persisted ParameterSpace filename: ";
	private static final String BUILD_PARAMETER_SPACE = "buildParameterSpace";
	private static final String SCENARIO_SET = "ScenarioSet.";
	private static final String EXCEPTION_HEADER = SCENARIO_SET+"runScenario:  ScenarioException encountered for Scenario ";
	private static final Object COMMA = ",";
	private static final String QUOTE = "\"";
	private int id; 
	private String name; 
	private Model<R> model;
	private ParameterSpace parameterSpace;
	private List<ScenarioResult<R>> scenarioResults;
	protected List<Scenario<R, I>> scenarios;
	private Input input;
	private Calendar calendar;
	private boolean listLog;
	private int scenarioIdForTesting = 0;
	private Map<Integer, Scenario<R, I>> scenarioMap; 
	protected Session session;
	protected Transaction tx;
	protected List<HibernateRetrievable> retrievableScenarios;
	private NameValuePair<?>[] summaryDataNameValuePairs;
	private NameValuePairParser parser;
	private OutputFileBuilder outputFileBuilder;

	private String summaryHeader;
	private BufferedWriter writer;

	//TODO one to many to Scenario.  consider giving a scenario a key of its parameter point, so that instead of a list we have a set of scenarios
	//TODO cascading deletes
	//TODO Scenario implements many to one with ScenarioSet.  can navigate from Scenario to its ScenarioSet.  Normal lifecycle of scenario is that scenarioSet is created first.  
	
	// convert id int to long.  buildAndRunScenarios, driven off parameterspace iterator
	// outputFileBuilder does the writing? open file, write until new file needed (32K), flush after each record
//	static 
//	{
//		if (DEBUG)
//		{
//			System.err.println("ScenarioSet configure appender");
//			BasicConfigurator.configure();
//			Logger.getRootLogger().setLevel(Level.DEBUG);
//		}
//	}
	public ScenarioSet()
	{
	}
	public ScenarioSet(boolean listLog)
	{
		scenarioResults = new ArrayList<ScenarioResult<R>>(); 
		scenarios = new ArrayList<Scenario<R, I>>(); 
		this.listLog = listLog; 
	}
	public void run() throws ScenarioException
	{
		logger.debug("run"); 
		if (parameterSpace.getFilename() != null) buildParameterSpace(); 
//		logger.debug("0th parameter at build time: "+parameterSpace.getParameter(0).getName()+" with value "+parameterSpace.getParameter(0).nextParameterValue().toString());
		buildScenarios();
		setScenarioIdsForTesting(); 
		runScenarios(); 
//		buildSummary(); // add parameter of NameValuePairParser
	}
	public void batchRun() throws ScenarioException
	{
		logger.debug("run"); 
		validate(); 
		if (parameterSpace.getFilename() != null) buildParameterSpace(); 
		buildOutputFileBuilder();
		ParameterIterator iterator = parameterSpace.iterator();
		Scenario<R, I> scenario = null;
		ParameterPoint point = null;
		ScenarioResult<R> result = null; 
		ResourceManager.registerScenarioSet(this);
		buildSummaryFile(); 
		while (iterator.hasNext())
		{
			scenario = new SimpleScenario<R, I>();
			scenario.setScenarioSet(this); 
			scenario.setInput(input);
			scenario.setModel(model);
			scenario.setCalendar(calendar);
			if (listLog) scenario.setLog(new ListScenarioLog<R,I>(scenario)); 
			point = iterator.next(); 
			scenario.setParameterPoint(point); 
			if (scenario.getId() == 0) scenario.setId(++scenarioIdForTesting); 
			logger.debug("run scenario");
			scenario.setOutputFileBuilder(outputFileBuilder.cloneWithId(scenario.getId())); 
			ResourceManager.run(this);
			logger.debug("run scenarios; about to run scenario"+scenario.getId());
			result = runScenario(scenario); 
			writeLine(buildSummaryDataLine(scenario, result));
			//TODO move the data writing logic elsewhere so DebugScenarioTest can also use it.
		}
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} 
//		throw new ScenarioException("dump logs: \n"+sb.toString()); //
		logger.debug("built scenarios; size="+scenarios.size());
	}
	public void validate() throws ScenarioException
	{
		if (parameterSpace == null) throw new ScenarioException(SCENARIO_SET+VALIDATE+PARAMETER_SPACE_REQUIRED_BEFORE_BATCH_RUN);
		if (summaryHeader == null) throw new ScenarioException(SCENARIO_SET+VALIDATE+BUILD_SUMMARY_HEADER_REQUIRED_BEFORE_BATCH_RUN);
	}

	private void setScenarioIdsForTesting()
	{
		for (Scenario<R, I> scenario : scenarios)
		{
			if (scenario.getId() == 0) scenario.setId(++scenarioIdForTesting); 
		}
	}
	protected void buildScenarios()	throws ScenarioException
	{
		buildOutputFileBuilder();
		ParameterIterator iterator = parameterSpace.iterator();
		Scenario<R, I> scenario = null;
		ParameterPoint point = null;
		while (iterator.hasNext())
		{
			scenario = buildScenario(); 
			scenario.setInput(input);
			scenario.setModel(model);
			scenario.setCalendar(calendar);
			if (listLog) scenario.setLog(new ListScenarioLog<R,I>(scenario)); 
			point = iterator.next(); 
			scenario.setParameterPoint(point); 
		}
		logger.debug("built scenarios; size="+scenarios.size());
	}
	public void buildOutputFileBuilder() throws ScenarioException
	{
		try
		{
			outputFileBuilder = new OutputFileBuilder("ScenarioSet_"+getId());
		}
		catch (OutputFileBuilderException e)
		{
			throw new ScenarioException(SCENARIO_SET+"buildScenarios:  "+e.getMessage()); 
		}
	}
	protected void runScenarios() throws ScenarioException
	{
		ResourceManager.registerScenarioSet(this);
		logger.debug("run scenarios");
		mapScenarios(); 
		ScenarioResult<R> result = null; 
//		StringBuffer sb = new StringBuffer(); // 
		for (Scenario<R, I> scenario : scenarios)
		{
			scenario.setOutputFileBuilder(outputFileBuilder.cloneWithId(scenario.getId())); 
			ResourceManager.run(this);
			logger.debug("run scenarios; about to run scenario"+scenario.getId());
			result = runScenario(scenario); 
			scenarioResults.add(result); 
		}
//		throw new ScenarioException("dump logs: \n"+sb.toString()); //
	}
	//TODO persist
	private void mapScenarios()
	{
		scenarioMap = new HashMap<Integer, Scenario<R, I>>(); 
		for (Scenario<R, I> scenario : scenarios)
		{
			scenarioMap.put(scenario.getId(), scenario);
		}
	}
	protected SimpleScenario<R, I> buildScenario()
	{
		SimpleScenario<R, I> scenario = new SimpleScenario<R, I>();
		scenario.setScenarioSet(this); 
		scenarios.add(scenario); 
		return scenario; 
	}
	private ScenarioResult<R> runScenario(Scenario<R, I> scenario)	throws ScenarioException
	{
		ScenarioResult<R> result = null;
		try
		{
			result = scenario.run();
		}
		catch (ScenarioException e)
		{
			throw new ScenarioException(EXCEPTION_HEADER+scenario.getId()+":  "+e.getMessage()); 
		}
		return result;
	}
	protected void buildParameterSpace() throws ScenarioException 
	{
		//TODO test exception paths
		String filename = parameterSpace.getFilename(); 
		if (filename != null)
		{
			Persister<ParameterSpace> persister = new ParameterSpacePersister<ParameterSpace>();  
			try
			{
				parameterSpace = persister.load(ParameterSpace.class, new File(filename));
			} 
			catch (PersistenceException e)
			{
				throw new ScenarioException(SCENARIO_SET+BUILD_PARAMETER_SPACE+PERSISTENCE_EXCEPTION+ATTEMPTING_TO_BUILD_PARAMETER_SPACE_FROM_PERSISTED_PARAMETER_SPACE_FILENAME+filename+'\n'+
						e.getMessage()); 
			}
		}
		else 
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_PARAMETER_SPACE+"Null filename found attempting to build Parameter Space.");
		}
	}
	public void buildSummaryHeader(NameValuePair<?>... summaryDataNameValuePairs)
	{
		this.summaryDataNameValuePairs = summaryDataNameValuePairs; 
		parser = new NameValuePairParser(summaryDataNameValuePairs); 
		buildHeader(); 
	}
	public void buildSummary(NameValuePair<?>... summaryDataNameValuePairs) throws ScenarioException
	{
		this.summaryDataNameValuePairs = summaryDataNameValuePairs; 
		parser = new NameValuePairParser(summaryDataNameValuePairs); 
		buildHeader(); 
		buildAndWriteSummaryFile(); 
	}
	public void buildSummaryFile() throws ScenarioException
	{
		File file = null;  
		writer = null;
		try
		{
			file = outputFileBuilder.buildSummaryFile(buildSummaryFilename());
			writer = new BufferedWriter(new FileWriter(file));
			writeLine(summaryHeader); 
		} catch (IOException e)
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+"IO "+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		} 
		catch (OutputFileBuilderException e)
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+"OutputFileBuilder "+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		}
		catch (Exception e)
		{
			try
			{
				writer.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		}
	}
	private void writeLine(String line) throws ScenarioException
	{
		try
		{
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e)
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+"IO "+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		} 
		catch (Exception e)
		{
			try
			{
				writer.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		}
	}
	public void buildAndWriteSummaryFile() throws ScenarioException
	{
		File file = null;  
		BufferedWriter writer = null;
		try
		{
			file = outputFileBuilder.buildSummaryFile(buildSummaryFilename());
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(summaryHeader);
			writer.newLine();
			for (String line : buildSummaryDataLines())
			{
				writer.write(line);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e)
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+"IO "+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		} 
		catch (OutputFileBuilderException e)
		{
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+"OutputFileBuilder "+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		}
		catch (Exception e)
		{
			try
			{
				writer.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			throw new ScenarioException(SCENARIO_SET+BUILD_SUMMARY+EXCEPTION_ENCOUNTERED_ATTEMPTING_TO_WRITE_STATISTICS_FILE+Constants.CRLF+e.getMessage());
		}
	}
	private List<String> buildSummaryDataLines()
	{
		List<String> lines = new ArrayList<String>(); 
		for (int i = 0; i < scenarios.size(); i++)
		{
			lines.add(buildSummaryDataLine(scenarios.get(i), scenarioResults.get(i))); 
		}
		return lines;
	}
	private String buildSummaryDataLine(Scenario<R, I> scenario, ScenarioResult<R> scenarioResult)
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append(scenario.getId());
		sb.append(COMMA); 
		appendSummaryDataFields(scenarioResult, sb);
		appendParameterPointValues(scenario, sb); 
		return sb.toString(); 
	}
	public void appendSummaryDataFields(ScenarioResult<R> scenarioResult, StringBuffer sb)
	{
		NameValuePair<?>[] pairs = parser.parse(scenarioResult.getResult().getSummaryData());
		String value = null; 
		for (int i = 0; i < pairs.length; i++)
		{
			value = pairs[i].getValue().toString();
			sb.append((pairs[i].getFieldClass().equals(String.class)) ? escapedString(value) : value);
			sb.append(COMMA); 
		}
	}
	private void appendParameterPointValues(Scenario<R, I> scenario, StringBuffer sb)
	{
		ParameterPoint point = scenario.getParameterPoint(); 
		ParameterValue<?>[] values = point.getValues();
		String value = null; 
		@SuppressWarnings("rawtypes")
		Class valueClass = null;
		for (int i = 0; i < values.length; i++)
		{
			value = values[i].toString(); 
			valueClass = point.getParameters().get(i).getParameterUpdater().getParameterClass();
			sb.append((valueClass.equals(String.class)) ? escapedString(value) : value);
			if (i < values.length-1) sb.append(COMMA); 
		}
	}
	public void buildHeader()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(escapedString("scenario"));
		sb.append(COMMA); 
		appendCustomData(sb); 
		appendParameterNames(sb); 
		summaryHeader = sb.toString();  
	}
	protected void appendParameterNames(StringBuffer sb)
	{
		List<Parameter<?>> parameters = parameterSpace.getParameters();
		int size = parameters.size();
		for (int i = 0; i < size; i++)
		{
			sb.append(escapedString(parameters.get(i).getName())); 
			if (i < size-1) sb.append(COMMA); 
		}
	}
	private void appendCustomData(StringBuffer sb)
	{
		for (int i = 0; i < summaryDataNameValuePairs.length; i++)
		{
			sb.append(escapedString(summaryDataNameValuePairs[i].getFieldName()));
			sb.append(COMMA); 
		}
	}
	private String escapedString(String string)
	{
		return QUOTE+string+QUOTE;
	}
	private String buildSummaryFilename()
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("scenario_set_summary_");
		sb.append(getId()); 
		sb.append("_test scenario set.csv");
		return sb.toString();
	}

	public List<ScenarioResult<R>> getScenarioResults()
	{
		return scenarioResults;
	}
	public void setInput(Input input)
	{
		this.input = input;
	}
	public void setModel(Model<R> model)
	{
		this.model = model; 
	}
	public void setParameterSpace(ParameterSpace parameterSpace)
	{
		this.parameterSpace = parameterSpace; 
	}
	public void setCalendar(Calendar calendar)
	{
		this.calendar = calendar; 
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public Model<R> getModel()
	{
		return model;
	}
	public ParameterSpace getParameterSpace()
	{
		return parameterSpace;
	}
	public Input getInput()
	{
		return input;
	}
	public Calendar getCalendar()
	{
		return calendar;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public List<Scenario<R, I>> getScenarios()
	{
		return scenarios;
	}
	public void setScenarios(List<Scenario<R, I>> scenarios)
	{
		this.scenarios = scenarios;
	}
	@SuppressWarnings("unchecked")
	public Scenario<R, I> getScenario(int scenarioId)
	{
		Scenario<R, I> scenario = null; 
		tx = getTx(); 
		scenario = (Scenario<R, I>) getSession().get(Scenario.class, scenarioId); 
		tx.commit();
		if (scenario == null) return null; 
		if (this != scenario.getScenarioSet()) return null; 
		else return scenario;  
	}
	protected Transaction getTx()
	{
		return getSession().beginTransaction(); 
	}
	protected Session getSession()
	{
		if (session == null)
		{
			session = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		return session;
	}
	public String getSummaryHeader()
	{
		return summaryHeader;
	}
	public OutputFileBuilder getOutputFileBuilder()
	{
		return outputFileBuilder;
	}
}
