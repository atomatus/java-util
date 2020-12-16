package com.atomatus.util;

import java.util.Calendar;
import java.util.Date;

/**
 * An object that contains information about difference between two Dates.
 * @author Carlos Matos
 *
 */
public final class Duration {

	private long totalMilliss, totalSec, totalMin, totalHour, totalDay, totalMonth, totalYear;
	private int milliss, sec, min, hour, day, month, year;
	private boolean isEmpty;

	private Duration() { isEmpty = true; }

	public boolean isEmpty() {
		return isEmpty;
	}

	public long getMilliss() {
		return milliss;
	}

	public int getSec() {
		return sec;
	}

	public int getMin() {
		return min;
	}

	public int getHour() {
		return hour;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public long getTotalMilliss() {
		return totalMilliss;
	}

	public long getTotalSec() {
		return totalSec;
	}

	public long getTotalMin() {
		return totalMin;
	}

	public long getTotalHour() {
		return totalHour;
	}

	public long getTotalDay() {
		return totalDay;
	}

	public long getTotalMonth() {
		return totalMonth;
	}

	public long getTotalYear() {
		return totalYear;
	}

	private String printTimeSec() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dS", sec);
	}

	private String printTimeMinSec() {
		if(min <= 0){
			return printTimeSec();
		}

		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dMin%2$02ds", min, sec);
	}

	/**
	 * Print time.
	 * @return formatted value in 00H00M00S, 00M00S or 00S
	 */
	public String printTime() {
		if(hour <= 0) {
			return printTimeMinSec();
		}

		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dH%2$02dM%3$02dS", hour, min, sec);
	}

	/**
	 * Print time short.
	 * @return formatted value in 00H00Min, 00Min00S or 00S
	 */
	public String printTimeShort() {
		if(hour <= 0) {
			return printTimeMinSec();
		}

		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dH%2$02dM", hour, min);
	}

	/**
	 * Print time full.
	 * @return formatted value in 00H00M00S
	 */
	public String printTimeFull() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dH%2$02dM%3$02dS", hour, min, sec);
	}

	/**
	 * Print full duration.
	 * @return formatted value in 00D00H00M00S
	 */
	public String printFull() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dD%2$02dH%3$02dM%4$02dS", totalDay, hour, min, sec);
	}

	/**
	 * Calculate diff between two milliseconds values.
	 * @param oldTime millisecond older value
	 * @param newTime millisecond newer value
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(long oldTime, long newTime) {
		Duration d	   = new Duration();
		d.totalMilliss = newTime - oldTime;
		d.totalSec     = d.totalMilliss / 1000L;
		d.totalMin     = d.totalSec     / 60L;
		d.totalHour    = d.totalMin     / 60L;
		d.totalDay     = d.totalHour    / 24L;
		d.totalMonth   = d.totalDay     / 30L;
		d.totalYear    = d.totalMonth   / 12L;

		d.milliss      = (int) d.totalMilliss   % 1000;
		d.sec          = (int) d.totalSec       % 60;
		d.min          = (int) d.totalMin       % 60;
		d.hour         = (int) d.totalHour      % 24;

		d.day          = (int) (d.totalDay      % 365) % 30;
		d.month        = (int) d.totalMonth     % 12;
		d.year         = (int) d.totalYear;
		d.isEmpty	   = false;
		return d;
	}

	/**
	 * Diff between Dates.
	 * @param oldDate date older value.
	 * @param newDate date newer value.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(Date oldDate, Date newDate) {
		return diff(oldDate.getTime(), newDate.getTime());
	}

	/**
	 * Diff between Calendars.
	 * @param oldCal date older value.
	 * @param newCal date newer value.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(Calendar oldCal, Calendar newCal) {
		return diff(oldCal.getTimeInMillis(), newCal.getTimeInMillis());
	}

	/**
	 * Diff between Dates in string.
	 * @param oldDateTime date older value.
	 * @param newDateTime date newer value.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(String oldDateTime, String newDateTime) {
		Date oldDate = DateHelper.getInstance().parseDate(oldDateTime);
		Date newDate = DateHelper.getInstance().parseDate(newDateTime);
		return diff(oldDate, newDate);
	}

	/**
	 * Diff between input time millis and current date.
	 * @param time time in millis.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(long time) {
		return diff(time, System.currentTimeMillis());
	}

	/**
	 * Diff between input date time and current date.
	 * @param newDate date value.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(Date newDate) {
		return diff(newDate.getTime());
	}

	/**
	 * Diff between input date time and current date.
	 * @param newCal calendar value.
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(Calendar newCal) {
		return diff(newCal.getTimeInMillis());
	}

	/**
	 * Diff between input date time and current date.
	 * @param newDateTime date time in string
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(String newDateTime) {
		return diff(DateHelper.getInstance().parseDate(newDateTime));
	}

	/**
	 * Empty result.
	 * @return an empty instance of Duration.
	 */
	public static Duration empty() {
		return new Duration();
	}

}