package com.atomatus.util;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class DurationTest extends TestCase {

    @SuppressWarnings("deprecation")
    public void testDiff() {
        Date initialDate = new Date(2020, Calendar.OCTOBER, 31, 15, 30, 0);
        Date finalDate = new Date(2021, Calendar.NOVEMBER, 24, 16, 35, 47);

        Duration diff = Duration.diff(initialDate, finalDate);
        assertEquals(1, diff.getYear());
        assertEquals(0, diff.getMonth());
        assertEquals(24, diff.getDay());
        assertEquals(1, diff.getHour());
        assertEquals(5, diff.getMin());
        assertEquals(47, diff.getSec());

        System.out.println(diff.printFull());
        System.out.println(diff.printTime());
        System.out.println(diff.printTimeShort());
    }
}