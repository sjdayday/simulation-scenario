/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.grayleaves.utility.DiffFormatter;
import org.junit.Before;
import org.junit.Test;


public class DiffFormatterTest
{
	private static final String BASELINE_TEXT_HERE = "baseline text here";
	private LinkedList<Diff> diffs;
	private diff_match_patch dmp;
	private DiffFormatter formatter;

	@Before
	public void setUp() throws Exception
	{
		dmp = new diff_match_patch(new StandardBreakScorer()); 
		formatter = new DiffFormatter(); 
		
	}
	
	@Test
	public void verifyDiffFormat() throws Exception
	{
		diffs = dmp.diff_main(BASELINE_TEXT_HERE, BASELINE_TEXT_HERE);
		assertEquals("entire context shown, which is the whole text when there are no diffs", BASELINE_TEXT_HERE, formatter.format(diffs));
		diffs = dmp.diff_main(BASELINE_TEXT_HERE, "baselin test there");
		assertEquals("entire context shown with the diffs", "expected:  baselin[e] te[x]t here\n"+
				"encountered:  baselin te[s]t [t]here\n", formatter.format(diffs));
		
//		assertEquals()
//		System.out.println(dmp.diff_prettyHtml(diffs));
	}
}
