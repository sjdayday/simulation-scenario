package org.grayleaves.utility;



public class PersistentModel<R> implements Model<R>
{

	private int id;
	protected String name;
	private String clazz;
	protected Model<R> model;
	protected Input input; 
	private final String where = "PersistentModel.buildModel Model Exception instantiating ";
	protected OutputFileBuilder outputFileBuilder; 
	

	public PersistentModel()
	{
	}
	
	@Override
	public Result<R> run() throws ModelException
	{
		if (model == null) buildModel(); 
		model.setInput(input); 
		return model.run();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildModel() throws ModelException 
	{
		try
		{
			Class classObj = Class.forName(clazz);
			model = (Model<R>) classObj.newInstance(); 
		} 
		catch (Exception e)
		{
			throw new ModelException(where+clazz+": "+e.toString()); 
		}
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
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getClazz()
	{
		return clazz;
	}
	public void setClazz(String clazz)
	{
		this.clazz = clazz;
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
