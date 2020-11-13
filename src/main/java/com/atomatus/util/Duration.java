package com.atomatus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * An object that contains information about difference between two Dates.
 * @author Carlos Matos
 *
 */
public final class Duration {

	private long totalMillis;
	private long totalSecond;
	private long totalMinute;
	private long totalHour;
	private int totalDays;
	private int second;
	private int minute;
	private int hour;
	private boolean isEmpty;

	private Duration() { isEmpty = true; }

	public long getTotalMillis() {
		return totalMillis;
	}

	public long getTotalSecond() {
		return totalSecond;
	}

	public long getTotalMinute() {
		return totalMinute;
	}

	public long getTotalHour() {
		return totalHour;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public int getSecond() {
		return second;
	}

	public int getMinute() {
		return minute;
	}

	public int getHour() {
		return hour;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * Print time short.
	 * @return formatted value in 00H00M
	 */
	public String printTimeShort() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dH%2$02dM", hour, minute);
	}

	/**
	 * Print time.
	 * @return formatted value in 00H00M00S
	 */
	public String printTime() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dH%2$02dM%3$02dS", hour, minute, second);
	}

	/**
	 * Print full duration.
	 * @return formatted value in 00D00H00M00S
	 */
	public String printFull() {
		return String.format(LocaleHelper.getDefaultLocale(), "%1$02dD%2$02dH%3$02dM%4$02dS", totalDays, hour, minute, second);
	}

	/**
	 * Calculate diff between two milliseconds values.
	 * @param oldTime millisecond older value
	 * @param newTime millisecond newer value
	 * @return an instance of Duration with diff result.
	 */
	public static Duration diff(long oldTime, long newTime) {

		Duration d = new Duration();

		d.totalMillis	= newTime - oldTime;
		d.totalSecond	= d.totalMillis / 1000;
		d.totalMinute	= d.totalSecond / 60;
		d.totalHour		= d.totalHour 	/ 60;
		d.totalDays 	= (int) (d.totalMillis / (24 * 60 * 60 * 1000));

		d.second 		= (int) (d.totalSecond % 60);
		d.minute 		= (int) (d.totalMinute % 60);
		d.hour 			= (int) (d.totalHour 	% 24);
		d.isEmpty		= false;

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
	public static Duration diff(String newDateTime) throws ParseException {
		return diff(DateFormat.getInstance().parse(newDateTime));
	}

	/**
	 * Empty result.
	 * @return an empty instance of Duration.
	 */
	public static Duration empty() {
		return new Duration();
	}

}