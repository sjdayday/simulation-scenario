package org.grayleaves.component;

public class ComponentHarness<C, I, O, F>
{

	private ComponentAdapter<C, I, O, F> adapter;

	public ComponentHarness(ComponentAdapter<C, I, O, F> adapter) throws NullComponentAdapterException
	{
		if (adapter == null) throw new NullComponentAdapterException("ComponentHarness constructor requires a non-null ComponentAdapter."); 
		this.adapter = adapter;
	}

	public void input(ComponentInput<I> input)
	{
		adapter.input(input); 	
	}
	public ComponentOutput<O> process()
	{
		return adapter.process();
	}

}
