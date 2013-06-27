/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.junit.Test;

public class MikeTest
{
	
	static interface MikeVal
	{
		boolean logicalValue();
		double doubleValue();
		String stringValue();
		public int compareTo(MikeVal rhs);
	}
	
	static class MikeBoolean implements MikeVal
	{
		public Boolean member;
		public boolean logicalValue() { return member.booleanValue(); }
		public double doubleValue() { return member ? 1.0 : 0; }
		public String stringValue() { return member.toString(); }
		public int compareTo(MikeVal rhs)
		{
			return member.compareTo(rhs.logicalValue());
		}		
	}
	
	static class MikeDouble implements MikeVal
	{
		public Double member;
		public boolean logicalValue() { return member.doubleValue()>0; }
		public double doubleValue() { return member.doubleValue(); }
		public String stringValue() { return member.toString(); }
		public int compareTo(MikeVal rhs)
		{
			return member.compareTo(rhs.doubleValue());
		}
	}

	static class MikeString implements MikeVal
	{
		public String member;
		public boolean logicalValue() { return member.equals("true"); }
		public double doubleValue() { return Double.valueOf(member); }
		public String stringValue() { return member.toString(); }
		public int compareTo(MikeVal rhs)
		{
			return member.compareTo(rhs.stringValue());
		}		
	}
	
	static interface AbstractConstraint
	{
		boolean isAllowed();
	}

	static class ExcludeAnd implements AbstractConstraint
	{
		protected MikeVal first;
		protected MikeVal second;
		
		public ExcludeAnd(MikeVal first,MikeVal second)
		{
			this.first = first;
			this.second = second;
		}
		
		public boolean isAllowed()
		{
			return !(this.first.logicalValue() && this.second.logicalValue());
		}
		
	}
	
	static class FancyMess implements AbstractConstraint
	{
		public MikeVal first;
		public MikeVal second;
		public MikeVal third;
		
		public boolean isAllowed()
		{
			if(first.logicalValue()) return (second.compareTo( third) == -1);
			return third.compareTo(second)==-1;
		}
	
	}
	
	@Test
	public void test()
	{
		MikeDouble first = new MikeDouble();
		MikeDouble second = new MikeDouble();
		AbstractConstraint c = new ExcludeAnd(first,second);
	
		first.member = 1.0;
		second.member = 1.0;
		assertFalse(c.isAllowed());

		first.member = 0.0;
		second.member = 1.0;
		assertTrue(c.isAllowed());
		
		FancyMess fm = new FancyMess();
		MikeBoolean fm_first = new MikeBoolean();
		MikeDouble fm_second = new MikeDouble();
		MikeDouble fm_third = new MikeDouble();
		
		fm_first.member = false;
		fm_second.member = 1.0;
		fm_third.member = 0.0;
		
		fm.first = fm_first;
		fm.second = fm_second;
		fm.third = fm_third;
		
		assertTrue(fm.isAllowed());
		
		fm_first.member = true;
		assertFalse(fm.isAllowed());
	}

}
