package org.grayleaves.utility;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.Statement;


public class ParameterSpacePersister<P> extends SimplePersister<P> 
{
//	public ParameterSpacePersister(P target)
//	{
//		super(target);
//    	setPersistenceFilename(((ParameterSpace) target).getFilename());
//    	
//	}
	public ParameterSpacePersister()
	{
		super(); 
	}
	@Override
	protected void setupDelegates()
	{
		encoder.setPersistenceDelegate(ParameterSpace.class, new ParameterSpaceDefaultPersistenceDelegate()); 
		encoder.setPersistenceDelegate(PropertiesParameterUpdater.class, new PropertiesParameterUpdaterPersistenceDelegate());
		encoder.setPersistenceDelegate(StaticParameterUpdater.class, new StaticParameterUpdaterPersistenceDelegate());

	}
	class ParameterSpaceDefaultPersistenceDelegate extends DefaultPersistenceDelegate
	{
		@Override
		protected void initialize(Class<?> type, Object oldInstance,
				Object newInstance, Encoder out)
		{
			super.initialize(type, oldInstance, newInstance, out);
			ParameterSpace space = (ParameterSpace) oldInstance;
			out.writeStatement(
				new Statement(oldInstance, "loadProperties", new Object[] {space.getPropertiesLocation()})) ;
		}
	}
	class PropertiesParameterUpdaterPersistenceDelegate extends DefaultPersistenceDelegate
	{
		@Override
		protected Expression instantiate(Object oldInstance, Encoder out)
		{
			PropertiesParameterUpdater<?> updater = (PropertiesParameterUpdater<?>) oldInstance; 
			return new Expression(oldInstance, oldInstance.getClass(), "new",
					new Object[]{updater.getParameterClass(), updater.getInternalName()});
		}
	}
	class StaticParameterUpdaterPersistenceDelegate extends DefaultPersistenceDelegate
	{
		@Override
		protected Expression instantiate(Object oldInstance, Encoder out)
		{
			StaticParameterUpdater<?> updater = (StaticParameterUpdater<?>) oldInstance; 
			return new Expression(oldInstance, oldInstance.getClass(), "new",
					new Object[]{updater.getParameterClass(), updater.getFieldName(), updater.getClassName()});
		}
	}
}
