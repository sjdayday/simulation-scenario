package org.grayleaves.utility;

import static org.junit.Assert.*;

import org.grayleaves.utility.NameValuePair;
import org.grayleaves.utility.NameValuePairParser;
import org.junit.Test;


public class NameValuePairParserTest
{
	private NameValuePairParser parser;

	@Test
	public void verifyParsesValueStringToNameValuePairs() throws Exception
	{
		parser = new NameValuePairParser(new NameValuePair<String>("text field", String.class), new NameValuePair<Integer>("int field", Integer.class), 
				new NameValuePair<Boolean>("bool field", Boolean.class)); 
		NameValuePair<?>[] pairs = parser.parse("text field=fred, int field=7, bool field=true");
		assertEquals(3, pairs.length);
		assertEquals("fred",pairs[0].getValue());
		assertEquals(7,pairs[1].getValue());
		assertEquals(true,pairs[2].getValue());
	}
	@Test
	public void verifyIllegalConstructorDetected() throws Exception
	{
		tryConstructingInvalidParserExpectingException(null, "list of name value pairs may not be null or empty"); 
		tryConstructingInvalidParserExpectingException(new NameValuePair<?>[]{}, "list of name value pairs may not be null or empty"); 
	}
	
	private void tryConstructingInvalidParserExpectingException(NameValuePair<?>[] pairs, String exception)
	{
		try
		{
			parser = new NameValuePairParser(pairs); 
			fail("expecting exception "+exception);
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("NameValuePairParser.constructor:  "+exception, e.getMessage());
		}
	}
}
