package org.grayleaves.utility;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.grayleaves.utility.MockClock;
import org.junit.Before;
import org.junit.Test;


public class MockClockTest 
{
	@Before
	public void setUp()
    {
        MockClock.resetClock();
    }
	@Test
    public void verifyMockClock() throws Exception
    {
        Calendar cal = MockClock.getCalendar();
        Thread.sleep(10); 
        Calendar cal2 = MockClock.getCalendar();
        assertTrue(cal2.after(cal));
        MockClock.setDateForTesting("10/15/2005 12:00:14 PM");
        assertEquals("2005_10_15__12_00_14PM",MockClock.getFormattedDateString());
        Calendar calmock = MockClock.getCalendar();
        assertTrue(cal.after(calmock));
        MockClock.resetClock();
        Thread.sleep(10);
        Calendar cal3 = MockClock.getCalendar();
        assertTrue(cal3.after(cal2));
    }
}
