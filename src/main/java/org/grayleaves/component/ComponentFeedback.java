/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.component;

import java.util.Map;

import org.grayleaves.utility.Pattern;

public interface ComponentFeedback<F, O>
{
	public Map<Integer, F> getMismatches(ComponentOutput<O> output) throws ArrayLengthMismatchException;

	public void configureFeedbackPoints(Pattern pattern);

	public void setFeedback(F[] expected);

}
