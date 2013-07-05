/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;


/**
 * Interface to wrap an arbitrary component such that it can be placed in a {@link ComponentHarness} 
 * Each implementation provides a constructor to accept the specific component being wrapped.  
 * 
 * @author Steve Doubleday
 */
public interface ComponentAdapter<C, I, O, F>
{
	/**
	 * Input takes an array of objects and passes them to the component under test, 
	 * through an implementation of the ComponentInput interface ({e.g., @see {@link ArrayComponentInput}}).  
	 * The caller is responsible for knowing what type(s) the wrapped component is expecting.  
	 *  
	 * @param inputs
	 */
	public void input(ComponentInput<I> inputs);

	/**
	 * Process ComponentInput to generate ComponentOutput, possibly modified by ComponentFeedback from prior run 
	 * @return
	 */
	
	public ComponentOutput<O> process();
	
	/**
	 * ComponentFeedback is presented to the adapter  
	 * @param feedback
	 * @param results
	 */
	public void feedback(ComponentFeedback<F, O> feedback);

	/**
	 * Configures the component under test to receive information regarding the results of component processing {@see {@link process()}} 
	 * The points in the structure of the component under test where {@link ComponentFeedback} is presented are defined by the {@link Pattern}. 
	 * @param feedback
	 * @param pattern 
	 */
	public void configureFeedbackPoints(ComponentFeedback<F, O> feedback, Pattern pattern);

	/**
	 *  
	 * @return true if feedback points have been configured 
	 */
	public boolean feedbackConfigured();

	/**
	 * Return the component under test
	 * @return Component 
	 */
	
	public C getComponent();
}
