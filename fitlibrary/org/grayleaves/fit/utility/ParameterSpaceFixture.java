package org.grayleaves.fit.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.grayleaves.utility.ArrayParameter;
import org.grayleaves.utility.Input;
import org.grayleaves.utility.Model;
import org.grayleaves.utility.ParameterSpace;
import org.grayleaves.utility.ParameterSpacePersister;
import org.grayleaves.utility.ParameterUpdater;
import org.grayleaves.utility.Pattern;
import org.grayleaves.utility.PersistenceException;
import org.grayleaves.utility.RangeParameter;
import org.grayleaves.utility.Result;
import org.grayleaves.utility.StaticParameterUpdater;
import org.grayleaves.utility.TestingModel;

import fit.Fixture;
import fit.TypeAdapter;
import fitlibrary.ArrayFixture;
import fitlibrary.DoFixture;

public class ParameterSpaceFixture extends DoFixture
{
	private static Logger logger = Logger.getLogger(ParameterSpaceFixture.class);
	@SuppressWarnings("unused")
	private String packageName;
	Model<String> model = new TestingModel<String>();
	private ParameterSpace space;
	private String className;
	private ParameterUpdater<?> parameterUpdater;
	public ArrayParameter<?> arrayParameter = null; 
	public Object[] array = null;
	private String arrayValues;
	private boolean parameterIsArray;
	private String parameterName; 
	public int lower;
	public int upper; 
	public int increment;
	private int defaultValue;
	private List<Pattern> patternArrayList;
	private ParameterSpace rebuiltSpace;

	
	@Override
	public Object getSystemUnderTest()
	{
		return model; 
	}
	
	public void buildParameterSpaceNamed(String name)
	{
		space = new ParameterSpace(); 
		space.setName(name); 
	}
	public void rebuildParameterSpaceFromFile(String filename) throws PersistenceException
	{
		ParameterSpacePersister<ParameterSpace> spacePersister = new ParameterSpacePersister<ParameterSpace>();
		rebuiltSpace = spacePersister.load(ParameterSpace.class, new File(filename));
		rebuiltSpace.setFilename(filename); 
	}
	public void currentPackageIs(String packageName)
	{
		this.packageName = packageName; 
	}
	public void currentClassIs(String className)
	{
		this.className = className; 
	}
	public void staticIntegerFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Integer.class, Integer[].class);
	}

	public void staticBooleanFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Boolean.class, Boolean[].class);
	}
	public void staticLongFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Long.class, Long[].class);
	}
	public void staticShortFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Short.class, Short[].class);
	}
	public void staticCharacterFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Character.class, Character[].class);
	}
	public void staticByteFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Byte.class, Byte[].class);
	}
	public void staticDoubleFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Double.class, Double[].class);
	}
	public void staticFloatFieldToUpdateIs(String field) throws  Exception
	{
		buildParameter(field, Float.class, Float[].class);
	}
	public void staticStringFieldToUpdateIs(String field) throws Exception
	{
		buildParameter(field, String.class, String[].class);
	}
	public <P> void buildParameter(String field, Class<P> clazz, Class<?> arrayClass) throws Exception
	{
		this.parameterUpdater = new StaticParameterUpdater<P>(clazz, field, this.className);
		if (parameterIsArray)
		{
			P[] values = parseStringToArrayValues(this.arrayValues, arrayClass);
			arrayParameter = new ArrayParameter<P>(parameterName, (ParameterUpdater<P>) this.parameterUpdater, values);
			space.addParameter(arrayParameter);
			for (int i = 0; i < values.length; i++)
			{
				logger.debug(arrayParameter.getName()+" "+i+": "+values[i]); 
			}
		}
		else 
		{
			space.addParameter(new RangeParameter<Integer>(parameterName, (ParameterUpdater<Integer>) parameterUpdater, lower, upper, increment, defaultValue));
		}
		
	}

	public <P> P[] parseStringToArrayValues(String array, Class<?> arrayClass) throws Exception
	{
		TypeAdapter adapter  = TypeAdapter.on(this, arrayClass);
		P[] values = (P[]) adapter.parse(array);
		return values;
	}
	public void patternIs(String arrayValues) throws Exception
	{
		Pattern pattern = new Pattern((Boolean[]) parseStringToArrayValues(arrayValues, Boolean[].class));
		logger.debug("parameter space fixture: building one pattern: "+pattern.toString()+" array values: "+arrayValues); 
		patternArrayList.add(pattern);
	}
	public Integer showArray()
	{
		Object[] stuff = arrayParameter.getValues();
		return (Integer) stuff[1]; 
	}
	public String showStringArray()
	{
		Object[] stuff = arrayParameter.getValues();
		return (String) stuff[1]; 
	}
	public Fixture parameters()
	{
		return new ArrayFixture(space.getParameters()); 
	}
	public void rangeParameterNamedFromToBy(String name, int lower, int upper, int increment)
	{
		this.parameterName = name;
		this.lower = lower;
		this.upper = upper; 
		this.increment = increment;
		this.defaultValue = lower; // default the default until there's a use case for it
		this.parameterIsArray = false; 
	}
	public void arrayParameterNamedWithValues(String name, String arrayValues) 
	{  
		this.parameterName = name; 
		this.arrayValues = arrayValues; 
		this.parameterIsArray = true;
	}
	public void arrayPatternParameterNamed(String name)
	{
		this.parameterName = name; 
		this.patternArrayList = new ArrayList<Pattern>();  
		this.parameterIsArray = true;
	}
	public void staticPatternFieldToUpdateIs(String field) throws Exception
	{
		Pattern[] patterns = new Pattern[patternArrayList.size()]; 
		for (int i = 0; i < patternArrayList.size(); i++)
		{
			patterns[i] = patternArrayList.get(i); 
		}
		arrayParameter = new ArrayParameter<Pattern>(parameterName, new StaticParameterUpdater<Pattern>(Pattern.class, field, this.className), patterns);
		space.addParameter(arrayParameter);

	}
	public int parameterSpaceSize()
	{
		return getParameterSpace().size();
	}
	public String parameterSpaceSource() 
	{
		if (rebuiltSpace != null) 
		{
			StringBuffer sb = new StringBuffer(); 
			sb.append(rebuiltSpace.getFilename());
			sb.append(": ");
			sb.append((rebuiltSpace.getName() != null) ? rebuiltSpace.getName() : ""); 
			return sb.toString(); 
		}
		else return space.getName(); 
	}
	public Input input() 
	{
		return null; 
	}
	public Result output() 
	{
		return null; 
	}
	
	public ParameterSpace getParameterSpace()
	{
		return (rebuiltSpace != null) ? rebuiltSpace : space;
	}

}
