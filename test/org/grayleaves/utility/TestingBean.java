/* Copyright (c) 2013, Regents of the University of California.  See License.txt for details */

package org.grayleaves.utility;

import org.grayleaves.utility.Pattern;



public class TestingBean 
{

	public static String STRING_PARM = "fred"; 
	public static int INT_PARM = 3;
	public static Integer INTEGER_PARM = 2; 
	public static boolean BOOLEAN_PARM = true;
	public static boolean ANOTHER_BOOLEAN_PARM = true; 
	public static Boolean YET_ANOTHER_BOOLEAN_PARM = true;
	public static Boolean BOOLEAN_BOXED_PARM = true;
	public static double DOUBLE_PARM = 0.01d;
	public static Double DOUBLE_BOXED_PARM = 0.01d;
	public static float FLOAT_PARM = 0.01f;
	public static Float FLOAT_BOXED_PARM = 0.01f;
	public static short SHORT_PARM = 6;
	public static Short SHORT_BOXED_PARM = 6;
	public static long LONG_PARM = 6l;
	public static Long LONG_BOXED_PARM = 6l;
	public static char CHAR_PARM = 'a';
	public static Character CHARACTER_PARM = 'a';
	public static byte BYTE_PARM = 7;
	public static Byte BYTE_BOXED_PARM = 7;
	public static final String FINAL_STRING_PARM = "cant be updated"; 
	@SuppressWarnings("unused")
	private String NON_PUBLIC_STRING_PARM; 
	public String nonStaticStringParm; 
	public static Pattern PATTERN_PARM = new Pattern(new Boolean[]{true}); 
	private int num;
	private NotSerializableClass notSerializable; 
	
	public TestingBean()
	{
	}
	public TestingBean(int num)
	{
		this.num = num;
	}
	

	public static void resetForTesting()
	{
		STRING_PARM = "fred"; 
		INT_PARM = 3;
		BOOLEAN_PARM = true;
		DOUBLE_PARM = 0.01d;
		INTEGER_PARM = 2; 
		ANOTHER_BOOLEAN_PARM = true; 
		YET_ANOTHER_BOOLEAN_PARM = true; 
		PATTERN_PARM = new Pattern(new Boolean[]{true});
	}
	public int getNum()
	{
		return num;
	}
	public void setNum(int num)
	{
		this.num = num;
	}
	public NotSerializableClass getNotSerializable()
	{
		return notSerializable;
	}
	public void setNotSerializable(NotSerializableClass notSerializable)
	{
		this.notSerializable = notSerializable;
	}
	
}
