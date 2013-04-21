package org.grayleaves.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class StaticParameterUpdater<P> implements ParameterUpdater<P>
{


	private static final String STATIC_PARAMETER_UPDATER = "StaticParameterUpdater";
	private static HashMap<Class<?>, Class<?>> BOXED_MAP;
	private String fieldName;
	private String className;
	private Class<?> staticParmClass;
	private transient Field field;
	private Class<P> parameterClass;

	static 
	{
		BOXED_MAP = new HashMap<Class<?>, Class<?>>();  
		BOXED_MAP.put(int.class, Integer.class); 
		BOXED_MAP.put(short.class, Short.class); 
		BOXED_MAP.put(long.class, Long.class); 
		BOXED_MAP.put(char.class, Character.class); 
		BOXED_MAP.put(double.class, Double.class); 
		BOXED_MAP.put(float.class, Float.class); 
		BOXED_MAP.put(boolean.class, Boolean.class); 
		BOXED_MAP.put(byte.class, Byte.class); 
	}
	public StaticParameterUpdater()
	{
	}
	public StaticParameterUpdater(Class<P> parameterClass, String fieldName, String className) throws InvalidStaticParameterException
	{
		this.fieldName = fieldName; 
		this.className = className; 
		this.parameterClass = parameterClass; 
		createClass(); 
		verifyField(); 
	}


	private void verifyField() throws InvalidStaticParameterException
	{
		try
		{
			field = staticParmClass.getDeclaredField(fieldName);
			//TODO consider adding support for automatic boxing of primitive types:  field.getType().isPrimitive());
			//http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6176992
		} 
		catch (SecurityException e)
		{
			//TODO test for SecurityException  
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".verifyField:  Security Exception accessing: "+fieldName+" in class "+className+"; "+e.getMessage());
		} 
		catch (NoSuchFieldException e)
		{
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".verifyField:  Could not find field: "+fieldName+" in class "+className);
		}
		checkFieldsTypeMatchesExpectedType();
		int mods = field.getModifiers(); 
		if (!( (Modifier.isPublic(mods)) && (Modifier.isStatic(mods)) ) ) 
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".verifyField:  field: "+fieldName+", in class "+className+" must be public static");
		if (Modifier.isFinal(mods)) 
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".verifyField:  field: "+fieldName+", in class "+className+" must not be final");
	}
	public void checkFieldsTypeMatchesExpectedType() throws InvalidStaticParameterException
	{
		Class<?> type = field.getType(); 
		if (type.isPrimitive())
		{
			type = getBoxedClass(type); 
		}
		if (!type.equals(parameterClass))
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".verifyField:  field: "+fieldName+" is of type "+field.getType()+" but expected type "+parameterClass.getName()+", in class "+className);
	}

	private Class<?> getBoxedClass(Class<?> type)
	{
		return BOXED_MAP.get(type);
	}

	private void createClass() throws InvalidStaticParameterException
	{
		try
		{
			staticParmClass = Class.forName(className);
		} 
		catch (ClassNotFoundException e)
		{
			throw new InvalidStaticParameterException(STATIC_PARAMETER_UPDATER+".createClass:  Could not create class for: "+className); 
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public P getParameter()
	{
		//TODO StaticParameterUpdater.getParameter test exception paths
		P returnValue = null; 
		try
		{
			returnValue =  (P) field.get(null);  // null is valid for static fields
		} 
		catch (IllegalArgumentException e)
		{
			System.out.println(STATIC_PARAMETER_UPDATER+".getParameter: unexpected IllegalArgumentException for field "+field.getName()+" in class "+staticParmClass.getName()+": "+e.getMessage());
		} 
		catch (IllegalAccessException e)
		{
			System.out.println(STATIC_PARAMETER_UPDATER+".getParameter: unexpected IllegalAccessException for field "+field.getName()+" in class "+staticParmClass.getName()+": "+e.getMessage());
		}
		return returnValue;
	}

	@Override
	public void setParameter(ParameterValue<?> parameterValue)
	{
		//TODO StaticParameterUpdater.setParameter test exception paths
		try
		{
			field.set(null, parameterValue.getValue());  // null is valid for static fields
		} 
		catch (IllegalArgumentException e)
		{
			System.out.println(STATIC_PARAMETER_UPDATER+".setParameter: unexpected IllegalArgumentException setting value "+parameterValue.getValue()+" for field "+field.getName()+" in class "+staticParmClass.getName()+": "+e.getMessage());	
		} 
		catch (IllegalAccessException e)
		{
			System.out.println(STATIC_PARAMETER_UPDATER+".setParameter: unexpected IllegalAccessException setting value "+parameterValue.getValue()+" for field "+field.getName()+" in class "+staticParmClass.getName()+": "+e.getMessage());	
		}
	}
	@Override
	public <T> void setParameter(T value)
	{
		setParameter(new ParameterValue<T>(value));
	}
	// no no-argument constructor because instantiation handled by ParameterSpacePersister encoder delegate
//	public StaticParameterUpdater()
//	{
//	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public String getFieldName()
	{
		return fieldName;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public String getClassName()
	{
		return className;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public Class<?> getStaticParmClass()
	{
		return staticParmClass;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public void setStaticParmClass(Class<?> staticParmClass)
	{
		this.staticParmClass = staticParmClass;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public Field getField()
	{
		return field;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	public void setField(Field field)
	{
		this.field = field;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	@Override
	public Class<P> getParameterClass()
	{
		return parameterClass;
	}
	/**
	 * Bean-compatible method to support XMLEncoder.  Not present in the {@link ParameterUpdater} interface
	 */
	@Override
	public void setParameterClass(Class<P> parameterClass)
	{
		this.parameterClass = parameterClass;
	}
//  private void readObject(ObjectInputStream inStr) throws IOException, ClassNotFoundException 
//  {
//     inStr.defaultReadObject();
//     try
//     {
//  	   createClass();
//  	   verifyField(); 
//     }
//     catch (InvalidStaticParameterException e)
//     {
//  	   e.printStackTrace();
//     } 
//  }


}
