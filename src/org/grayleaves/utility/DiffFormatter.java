/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;

public class DiffFormatter
{

	public String format(LinkedList<Diff> diffs)
	{
		StringBuffer sb = new StringBuffer(); 
		if ((diffs.size() == 1) && (diffs.get(0).operation.equals(Operation.EQUAL)))
		{
			sb.append(diffs.get(0).text); 
		}
		else
		{
			StringBuffer sbexpected = new StringBuffer();
			sbexpected.append("expected:  ");
			StringBuffer sbencountered = new StringBuffer();
			sbencountered.append("encountered:  ");
			for (Diff diff : diffs)
			{
				switch(diff.operation)
				{
				case EQUAL: 
				{
					sbexpected.append(diff.text);
					sbencountered.append(diff.text); 
					break;
				}
				case DELETE:  
				{
					sbexpected.append("[");
					sbexpected.append(diff.text); 
					sbexpected.append("]");
					break;
				}
				case INSERT: 
				{ 
					sbencountered.append("[");
					sbencountered.append(diff.text);
					sbencountered.append("]");
					break; 
				}
				}
			}
			sb.append(sbexpected.toString());
			sb.append("\n");
			sb.append(sbencountered.toString()); 
			sb.append("\n");
		}	
		return sb.toString();
	}

}
