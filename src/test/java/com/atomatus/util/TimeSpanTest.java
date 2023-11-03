package com.atomatus.util;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Calendar;

public class TimeSpanTest extends TestCase {

    public void testCompareInstanceByConstructorsSuccessfully() {
        TimeSpan t0 = new TimeSpan(TimeSpan.TICKS_PER_HOUR * 49 + TimeSpan.TICKS_PER_MINUTE + TimeSpan.TICKS_PER_SECOND * 2);
        TimeSpan t1 = new TimeSpan(49, 1, 2);
        TimeSpan t2 = new TimeSpan(2, 1, 1, 2);
        TimeSpan t3 = new TimeSpan(1, 25, 1, 1, 1000);

        Assert.assertEquals(t0.compareTo(t1), 0);
        Assert.assertEquals(t1.compareTo(t2), 0);
        Assert.assertEquals(t2.compareTo(t3), 0);
    }

    public void testFromSecondsSuccessfully() {
        TimeSpan t0 = TimeSpan.fromSeconds(2);
        TimeSpan t1 = TimeSpan.fromMillis(2000);
        Assert.assertEquals(t0.compareTo(t1), 0);
    }

    public void testFromMinutesSuccessfully() {
        TimeSpan t0 = TimeSpan.fromMinutes(2);
        TimeSpan t1 = TimeSpan.fromSeconds(120);
        Assert.assertEquals(t0.compareTo(t1), 0);
    }

    public void testFromHourSuccessfully() {
        TimeSpan t0 = TimeSpan.fromHours(2);
        TimeSpan t1 = TimeSpan.fromMinutes(120);
        Assert.assertEquals(t0.compareTo(t1), 0);
    }

    public void testFromDaysSuccessfully() {
        TimeSpan t0 = TimeSpan.fromDays(2);
        TimeSpan t1 = TimeSpan.fromHours(48);
        Assert.assertEquals(t0.compareTo(t1), 0);
    }

    public void testDiffSuccessfully() throws InterruptedException {
        long time = 350;
        Calendar cal0 = Calendar.getInstance();
        Thread.sleep(time);
        Calendar cal1 = Calendar.getInstance();
        TimeSpan diff = TimeSpan.diff(cal0, cal1);
        Assert.assertTrue(diff.getTotalMilliseconds() >= time);
    }

    public void testDiffNowSuccessfully() throws InterruptedException {
        long time = 150;
        Calendar cal = Calendar.getInstance();
        Thread.sleep(time);
        TimeSpan diff = TimeSpan.diffNow(cal);
        Assert.assertTrue(diff.getTotalMilliseconds() >= time);
    }
}