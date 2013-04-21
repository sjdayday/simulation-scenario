package org.grayleaves.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.grayleaves.utility.Input;
import org.grayleaves.utility.ListResult;
import org.grayleaves.utility.ModelException;
import org.grayleaves.utility.PersistentModel;
import org.grayleaves.utility.Result;

public class TestingModel<R> extends PersistentModel<R>
{
	
	private int counter;
	private static Logger logger = Logger.getLogger(TestingModel.class);
	private transient BufferedReader br;
	private transient ByteArrayOutputStream bos;
	private byte[] bytes; 
	private transient Appender appender;
	public TestingModel()
	{
		
	}

	public void initializeLogger()
	{
		bos = new ByteArrayOutputStream(); 
		appender = new WriterAppender(new EnhancedPatternLayout("%p %c{2}:  %m %n"), bos);
		appender.setName(this.toString()); 
		BasicConfigurator.configure(appender); 
		logger.info("Testing Model initializing");
	}

	private void prepareLog(ByteArrayOutputStream bos)
	{
		bytes = bos.toByteArray();
		br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
		appender.close();
		LogManager.getRootLogger().removeAppender(appender); // logger.removeAppender() doesn't work 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result<R> run() throws ModelException
	{
		initializeLogger(); 
		if (counter < 0) 
		{
			String msg = "TestingModel.run: negative input is invalid."; 
			logger.error(msg);
			prepareLog(bos);
			throw new ModelException(msg); 
		}
//		logger.info("TestingModel.run: "+this.toString());
		Integer output = counter+TestingBean.INT_PARM; 
		Result<R> result = new ListResult<R>(); 
		StringBuffer sb = new StringBuffer(); 
		sb.append("TestingModel from input "); 
		sb.append(counter); 
		result.add((R) sb.toString());
		logger.info("First output row written."); 
		sb = new StringBuffer(); 
		sb.append("parameters string ");
		sb.append(TestingBean.STRING_PARM);
		sb.append(", int ");
		sb.append(TestingBean.INT_PARM); 
		sb.append(", boolean ");
		sb.append(TestingBean.BOOLEAN_PARM); 
		result.add((R) sb.toString());
		logger.info("Second output row written."); 
		sb = new StringBuffer(); 
		sb.append("output:  ");
		sb.append(output); 
		result.add((R) sb.toString()); 
		logger.info("Last output row written."); 
		result.setSummaryData("some custom data="+output.toString()+", another=true, last=random string"); 
		prepareLog(bos);
		return result;
	}

	@Override
	public void setInput(Input input)
	{
		this.counter = ((TestingInput) input).getNum(); 
	}

	@Override
	public BufferedReader getLog()
	{
		return br;
	}
}