/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


public class TestingModel<R> extends PersistentModel<R>
{
	
	private int counter;
	@SuppressWarnings("unchecked")
	@Override
	public Result<R> run() throws ModelException
	{
		Result<R> result = new ListResult<R>(); 
		if (counter < 0) 
		{
			String msg = "TestingModel.run: negative input is invalid."; 
			throw new ModelException(msg); 
		}
		Integer output = counter+TestingBean.INT_PARM; 
		StringBuffer sb = new StringBuffer(); 
		sb.append("TestingModel from input "); 
		sb.append(counter); 
		result.add((R) sb.toString());
		sb = new StringBuffer(); 
		sb.append("parameters string ");
		sb.append(TestingBean.STRING_PARM);
		sb.append(", int ");
		sb.append(TestingBean.INT_PARM); 
		sb.append(", boolean ");
		sb.append(TestingBean.BOOLEAN_PARM); 
		result.add((R) sb.toString());
		sb = new StringBuffer(); 
		sb.append("output:  ");
		sb.append(output); 
		result.add((R) sb.toString()); 
		result.setSummaryData("some custom data="+output.toString()+", another=true, last=random string"); 
		return result;
	}

	@Override
	public void setInput(Input input)
	{
		this.counter = ((TestingInput) input).getNum(); 
	}

}