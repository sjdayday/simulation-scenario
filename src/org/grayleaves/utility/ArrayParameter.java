package org.grayleaves.utility;


public class ArrayParameter<P> extends AbstractParameter<P> 
{

	private P[] values;
	private int index;
	private boolean hasNext = false;
	public ArrayParameter(String name, ParameterUpdater<P> parameterUpdater, P[] values)
	{
		this.name = name; 
		this.parameterUpdater = parameterUpdater;  
		this.values = values; 
		index = -1; 
		if (this.values != null) hasNext  = true;
	}
	@Override
	public P defaultValue()
	{
		if (this.values == null) return null;
		else return values[0]; 
	}
	@Override
	public P next()
	{
		if (this.values == null) return null;
		else
		{
			index++;
			if (index+1 == values.length) hasNext = false; 
			if (index == values.length) 
			{
				reset();
				index++;
			}
			return values[index];
		}
	}
	@Override
	public void reset()
	{
		index = -1; 
		hasNext = (values.length == 1) ? false : true;  // reset
	}
	@Override
	public boolean hasNext()
	{
		return hasNext;
	}
	@Override
	public ParameterValue<P> nextParameterValue()
	{
		return new ParameterValue<P>(next());
	}
	@Override
	public void updateModel()
	{
//		if (!value.equals(index)) throw new RuntimeException("RangeParameter.equals:Passed value: "+value+" not same as current internal value: "+index); 
		parameterUpdater.setParameter(values[index]); 
	}
	@Override
	public int size()
	{
		return values.length;
	}
	
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 * @param values:  array of values parameter can take
	 */
	public void setValues(P[] values)
	{
		this.values = values; 
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public ArrayParameter()
	{
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public P[] getValues()
	{
		return values;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public int getIndex()
	{
		return index;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface 
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface 
	 */
	public boolean isHasNext()
	{
		return hasNext;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface 
	 */
	public void setHasNext(boolean hasNext)
	{
		this.hasNext = hasNext;
	}
	public ParameterUpdater<P> getParameterUpdater()
	{
		return parameterUpdater;
	}
	public void setParameterUpdater(ParameterUpdater<P> parameterUpdater)
	{
		this.parameterUpdater = parameterUpdater;
	}
}
