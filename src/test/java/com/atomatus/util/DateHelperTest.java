package com.atomatus.util;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class DateHelperTest extends TestCase {

    public void testParseCalendar() {
        Calendar fullDate   = DateHelper.getInstance().parseCalendar("09/11/2020 17:52:33");
        Calendar onlyDate   = DateHelper.getInstance().parseCalendar("09/11/2020");
        Calendar onlyTime   = DateHelper.getInstance().parseCalendar("17:52:33");

        assertEquals("FullDate and OnlyDate received equals date values, but conversion generate different result!",
                fullDate.get(Calendar.DATE), onlyDate.get(Calendar.DATE));

        assertEquals("FullDate and OnlyDate received equals month values, but conversion generate different result!",
                fullDate.get(Calendar.MONTH), onlyDate.get(Calendar.MONTH));

        assertEquals("FullDate and OnlyDate received equals year values, but conversion generate different result!",
                fullDate.get(Calendar.YEAR), onlyDate.get(Calendar.YEAR));

        assertEquals("FullDate and OnlyTime received equals date values, but conversion generate different result!",
                fullDate.get(Calendar.HOUR), onlyTime.get(Calendar.HOUR));

        assertEquals("FullDate and OnlyTime received equals month values, but conversion generate different result!",
                fullDate.get(Calendar.MINUTE), onlyTime.get(Calendar.MINUTE));

        assertEquals("FullDate and OnlyTime received equals year values, but conversion generate different result!",
                fullDate.get(Calendar.SECOND), onlyTime.get(Calendar.SECOND));
    }

    @SuppressWarnings("deprecation")
    public void testDiff() {
        Date initialDate = new Date(2020, Calendar.OCTOBER, 31, 15, 30, 0);
        Date finalDate = new Date(2021, Calendar.NOVEMBER, 24, 16, 35, 47);

        DateHelper.TimeDiff diff = DateHelper.getInstance().diff(initialDate, finalDate);
        assertEquals(1, diff.getYear());
        assertEquals(0, diff.getMonth());
        assertEquals(24, diff.getDay());
        assertEquals(1, diff.getHour());
        assertEquals(5, diff.getMin());
        assertEquals(47, diff.getSec());

        System.out.println(diff.formatTime());
        System.out.println(diff.formatTimeShort());
    }
}