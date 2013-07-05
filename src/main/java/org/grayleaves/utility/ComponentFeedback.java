/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.Map;


public interface ComponentFeedback<F, O>
{
	public Map<Integer, F> getMismatches(ComponentOutput<O> output) throws ArrayLengthMismatchException;

	public void configureFeedbackPoints(Pattern pattern);

	public void setFeedback(F[] expected);

}
