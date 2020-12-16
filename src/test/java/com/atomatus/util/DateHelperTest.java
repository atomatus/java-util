package com.atomatus.util;

import junit.framework.TestCase;

import java.util.Calendar;

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
}