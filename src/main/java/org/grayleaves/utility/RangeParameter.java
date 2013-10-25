/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;



public class RangeParameter<P> extends AbstractParameter<P> 
{
	

	//TODO figure out the right way to do this
	private Integer minimum;
	private Integer maximum;
	private Integer interval;
	private Integer defaultValue;
	private Integer index;
	private boolean hasNext = false;
	private String RANGE_PARAMETER = "RangeParameter constructor:  ";
	private String MIN_MUST_BE_LESS_THAN_MAX = "Minimum must be less than maximum"; 
	private String DEFAULT_NOT_IN_RANGE = "Default must be within range of minimum to maximum";
	private String INTERVAL_TOO_GREAT = "Interval must be less than maximum - minimum";
	public RangeParameter(String name,	ParameterUpdater<P> parameterUpdater, P  minimum, P maximum, P interval, P defaultValue) throws UnsupportedParameterException
	{
		
		//FIXME casts to type variables not valid
		setName(name);
		this.parameterUpdater = parameterUpdater; 
		this.minimum = validate(minimum); 
		this.maximum = validate(maximum);
		this.interval = validate(interval);
		this.defaultValue = validate(defaultValue); 
		validate(); 
		reset(); 
//		index = this.minimum - this.interval; 
//		hasNext = true; 
	}
	
	private Integer validate(P value)
	{
		if (!((value.getClass().equals(Integer.class)) || (value.getClass().equals(int.class)))) 
			throw new IllegalArgumentException("RangeParameter currently only supports integer ranges.");
		return (Integer) value;
	}

	private void validate() throws UnsupportedParameterException
	{
		if (minimum >= maximum) throw new UnsupportedParameterException(RANGE_PARAMETER+MIN_MUST_BE_LESS_THAN_MAX); 
		if ((defaultValue < minimum) || (defaultValue > maximum) ) throw new UnsupportedParameterException(RANGE_PARAMETER+DEFAULT_NOT_IN_RANGE);
		if (interval >= (maximum - minimum)) throw new UnsupportedParameterException(RANGE_PARAMETER+INTERVAL_TOO_GREAT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public P defaultValue()
	{
		return (P) defaultValue;
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	@SuppressWarnings("unchecked")
	public  P  next()
	{
		if (index+interval >= maximum) 	hasNext = false; 
		bump();
		if (index > maximum) 
		{
			reset(); 
			bump();
		}
		return (P) index;
	}

	public void bump()
	{
		index=index+interval;
	}

	@Override
	public void reset()
	{
		index = minimum - interval;
		hasNext = true;
	}
	@Override
	public ParameterValue<P> nextParameterValue()
	{
		return new ParameterValue<P>(next());
	}
	@SuppressWarnings("unchecked")
	@Override
	public void updateModel()
	{
//		if (!value.equals(index)) throw new RuntimeException("RangeParameter.equals:Passed value: "+value+" not same as current internal value: "+index); 
		parameterUpdater.setParameter((P) index); 
	}
	@Override
	public int size()
	{
		return ((maximum-minimum)/interval) + 1;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public RangeParameter()
	{
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public Integer getMinimum()
	{
		return minimum;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setMinimum(Integer minimum)
	{
		this.minimum = minimum;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public Integer getMaximum()
	{
		return maximum;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setMaximum(Integer maximum)
	{
		this.maximum = maximum;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public Integer getInterval()
	{
		return interval;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setInterval(Integer interval)
	{
		this.interval = interval;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public Integer getDefaultValue()
	{
		return defaultValue;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setDefaultValue(Integer defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public Integer getIndex()
	{
		return index;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setIndex(Integer index)
	{
		this.index = index;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public boolean isHasNext()
	{
		return hasNext;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setHasNext(boolean hasNext)
	{
		this.hasNext = hasNext;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public ParameterUpdater<P> getParameterUpdater()
	{
		return parameterUpdater;
	}
	/**
	 * 	 Bean-compatible method to support XMLEncoder.  Not present in the {@link Parameter} interface
	 */
	public void setParameterUpdater(ParameterUpdater<P> parameterUpdater)
	{
		this.parameterUpdater = parameterUpdater;
	}
}
