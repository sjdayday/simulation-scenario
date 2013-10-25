package org.grayleaves.utility;

import java.util.ArrayList;
import java.util.List;

public class GridParameterIterator implements ParameterIterator
{

	private SimpleParameterIterator currentIterator;
	private FitnessTracker fitnessTracker;
	private int level;
	private List<List<SimpleParameterIterator>> levelList;
	private List<SimpleParameterIterator> processedIterators;
	private int currentLevel;
	private int factor;

	public GridParameterIterator(List<Parameter<?>> list)
	{
		this(list, null, 0, 10); 
	}
	//FIXME  shouldn't have FitnessTracker in the constructor if it's not going to be used.
	public GridParameterIterator(List<Parameter<?>> parameters,
			FitnessTracker fitnessTracker, int level, int factor)
	{
		this.fitnessTracker = fitnessTracker; 
		this.level = level; 
		this.factor = factor; 
		currentLevel = 0; 
		buildLevelLists(level);
		currentIterator = new SimpleParameterIterator(parameters); 
		if (fitnessTracker != null) currentIterator.setFitnessTracker(fitnessTracker);
		levelList.get(currentLevel).add(currentIterator); 
		processedIterators = new ArrayList<SimpleParameterIterator>(); 
	}

	protected void buildLevelLists(int level)
	{
		levelList = new ArrayList<List<SimpleParameterIterator>>(); 
		for (int i = 0; i < level+1; i++)
		{
			levelList.add(new ArrayList<SimpleParameterIterator>()); 
		}
	}

	@Override
	public boolean hasNext()
	{
		if (!currentIterator.hasNext()) 
		{
			currentIterator = getNextIterator();
			if (currentIterator == null) return false;
		}
		return currentIterator.hasNext(); 
	}
	
	private SimpleParameterIterator getNextIterator()
	{
		processedIterators.add(currentIterator); 
		levelList.get(currentLevel).remove(0); 
		if (levelList.get(currentLevel).isEmpty())
		{
			if (currentLevel < level) 
			{
				currentLevel++;
				addIteratorsForNewLevel(); 
				if (!levelList.get(currentLevel).isEmpty())
				{
					return levelList.get(currentLevel).get(0);  
				}
			}
			return null; 
		}
		return levelList.get(currentLevel).get(0);  
	}

	private void addIteratorsForNewLevel()
	{
		for (ParameterPoint parameterPoint : fitnessTracker.getBestParameterPoints())
		{
			levelList.get(currentLevel).add(buildIteratorForParameterPoint(parameterPoint)); 
		}
	}

	@SuppressWarnings("unchecked")
	private SimpleParameterIterator buildIteratorForParameterPoint(
			ParameterPoint parameterPoint)
	{
		List<Parameter<?>> parameters = new ArrayList<Parameter<?>>(); 
		Parameter<Double> parameter = null ;
		DoubleParameterShrinker<Double> shrinker = null;
		for (int i = 0; i < parameterPoint.getParameters().size(); i++)
		{
			try
			{
				shrinker = new DoubleParameterShrinker<Double>((Parameter<Double>) parameterPoint.getParameters().get(i), (Double) parameterPoint.getValues()[i].getValue()); 
				parameter = shrinker.shrink(factor); 
				parameters.add(parameter); 
			}
			catch (Exception e) 
			{ //TODO nice to do this a little less ugly
				parameters.add(buildSingleValueParameter(parameterPoint, i));
			}  
		}
		SimpleParameterIterator iterator = new SimpleParameterIterator(parameters); 
		if (fitnessTracker != null) iterator.setFitnessTracker(fitnessTracker);
		return iterator; 
	}

	//TODO less generic madness with Parameter, please. 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Parameter<?> buildSingleValueParameter(
			ParameterPoint parameterPoint, int i)
	{
		Parameter<?> oldParameter = parameterPoint.getParameters().get(i);  
		Object[] oneObject = new Object[]{ parameterPoint.get(i).getValue() }; 
		Parameter<?> singlePointParameter = new ArrayParameter(oldParameter.getName(), oldParameter.getParameterUpdater(), oneObject);
		return singlePointParameter;
	}

	@Override
	public ParameterPoint next()
	{
		if (hasNext()) return currentIterator.next();
		else return null; 
	}

	public ParameterIterator getParameterIterator()
	{
		return currentIterator;
	}

	@Override
	public void setFitnessTracker(FitnessTracker fitnessTracker)
	{
		this.fitnessTracker = fitnessTracker; 
		currentIterator.setFitnessTracker(fitnessTracker); 
		for (List<SimpleParameterIterator> list : levelList)
		{
			for (SimpleParameterIterator simpleParameterIterator : list)
			{
				simpleParameterIterator.setFitnessTracker(fitnessTracker); 
			}
		}
	}
	protected List<SimpleParameterIterator> getProcessedParameterIteratorsForTesting()
	{
		return processedIterators;
	}

}
