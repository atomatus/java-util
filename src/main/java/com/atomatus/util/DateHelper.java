package com.atomatus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <strong>Date Helper</strong>
 * <p>
 *     To help to parse and convert date time to another time zone, location,
 *     convert any date time string format to Calendar or Date object.
 * </p>
 * <p>
 *     Allow to generate a string date, time, or date and time, following the locale
 *     format and time zone. So the same date or calendar can be formatted to Locale.US, Locale.UK, etc.
 * </p>
 * @author Carlos Matos
 */
public final class DateHelper {

    private static final String FUSE_REGEX;
    private static final DateHelper instance;

    /**
     * Format mode.
     */
    private enum FormatMode {
        FULL,
        DATE,
        SMALL_DATE,
        TIME,
        SMALL_TIME
    }

    static {
        FUSE_REGEX = "([-+]\\d{1,2}:\\d{2}$)";
        instance = new DateHelper();
    }

    /**
     * Date helper singleton instance.
     * @return singleton instance.
     */
    public static DateHelper getInstance() {
        return instance;
    }

    private DateHelper() { }

    //region Current Date TimeZone

    /**
     * Default format current date time string to time zone and locale.
     * @param timeZone target time zone
     * @param locale target locale
     * @return date time string value format.
     */
    public String getDate(TimeZone timeZone, Locale locale){
        return getDate(new Date(), timeZone, locale);
    }

    /**
     * Default format current date time string to time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param timeZone target time zone
     * @return date time string value format.
     */
    public String getDate(TimeZone timeZone) {
        return getDate(timeZone, LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a short date format to current date, time zone and locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param timeZone target time zone.
     * @param locale target locale
     * @return short date format.
     */
    public String getSmallDate(TimeZone timeZone, Locale locale){
        return getSmallDate(new Date(), timeZone, locale);
    }

    /**
     * Generate a short date format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param timeZone target time zone.
     * @return short date format.
     */
    public String getSmallDate(TimeZone timeZone) {
        return getSmallDate(timeZone, LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a time format to current date, time zone and locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param timeZone target time zone.
     * @param locale target locale
     * @return default time format.
     */
    public String getTime(TimeZone timeZone, Locale locale){
        return getTime(new Date(), timeZone, locale);
    }

    /**
     * Generate a time format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and default locale rule.</i>
     * @param timeZone target time zone.
     * @return default time format.
     */
    public String getTime(TimeZone timeZone) {
        return getTime(timeZone, LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a time format to current date, time zone and locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param timeZone target time zone.
     * @param locale target locale
     * @return short time format.
     */
    public String getSmallTime(TimeZone timeZone, Locale locale){
        return getSmallTime(new Date(), timeZone, locale);
    }

    /**
     * Generate a time format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and default locale rule.</i>
     * @param timeZone target time zone.
     * @return short time format.
     */
    public String getSmallTime(TimeZone timeZone) {
        return getSmallTime(timeZone, LocaleHelper.getDefaultLocale());
    }

    //endregion

    //region Current Date Default TimeZone
    /**
     * Default format date time string to current date, time zone and locale.
     * @param locale target locale
     * @return date time string value format.
     */
    public String getDate(Locale locale){
        return getDate(TimeZone.getDefault(), locale);
    }

    /**
     * Default format date time string to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @return date time string value format.
     */
    public String getDate(){
        return getDate(LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a short date format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param locale target locale
     * @return short date format.
     */
    public String getSmallDate(Locale locale){
        return getSmallDate(TimeZone.getDefault(), locale);
    }

    /**
     * Generate a short date format to current date, zone and default locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @return short date format.
     */
    public String getSmallDate(){
        return getSmallDate(LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a time format to current date, time zone and locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param locale target locale
     * @return default time format.
     */
    public String getTime(Locale locale){
        return getTime(TimeZone.getDefault(), locale);
    }

    /**
     * Generate a time format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and default locale rule.</i>
     * @return default time format.
     */
    public String getTime(){
        return getTime(LocaleHelper.getDefaultLocale());
    }

    /**
     * Generate a time format to current date, time zone and locale targets.<br>
     * <i>The format will be choose by time zone and locale rule.</i>
     * @param locale target locale
     * @return short time format.
     */
    public String getSmallTime(Locale locale){
        return getSmallTime(TimeZone.getDefault(), locale);
    }

    /**
     * Generate a time format to current date, time zone and default locale targets.<br>
     * <i>The format will be choose by time zone and default locale rule.</i>
     * @return short time format.
     */
    public String getSmallTime(){
        return getSmallTime(LocaleHelper.getDefaultLocale());
    }
    //endregion

    //region Any Date
    /**
     * Default format date time string from input date, time zone and locale.
     * @param date target date
     * @param timeZone target time zone
     * @param locale target locale
     * @return date time string value format.
     */
    public String getDate(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.DATE);
    }

    /**
     * Default format date time string from input date, time zone and locale.
     * @param date target date
     * @param timeZone target time zone
     * @param locale target locale
     * @return small date format.
     */
    public String getSmallDate(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.SMALL_DATE);
    }

    /**
     * Default format date time string from input date, time zone and locale.
     * @param date target date
     * @param timeZone target time zone
     * @param locale target locale
     * @return time format.
     */
    public String getTime(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.TIME);
    }

    /**
     * Default format date time string from input date, time zone and locale.
     * @param date target date
     * @param timeZone target time zone
     * @param locale target locale
     * @return small time format.
     */
    public String getSmallTime(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.SMALL_TIME);
    }
    //endregion

    //region Any Date Default TimeZone
    /**
     * Default format date time string from input date and locale.
     * @param date target date
     * @param locale target locale
     * @return date time format.
     */
    public String getDate(Date date, Locale locale){
        return getDate(date, TimeZone.getDefault(), locale);
    }

    /**
     * Default format date time string from input date, using current time zone and locale.
     * @param date target date
     * @return date time format.
     */
    public String getDate(Date date){
        return getDate(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Default format date string from input date and locale.
     * @param date target date
     * @param locale target locale
     * @return small date format.
     */
    public String getSmallDate(Date date, Locale locale){
        return getSmallDate(date, TimeZone.getDefault(), locale);
    }

    /**
     * Default format date string from input date, using current time zone and locale.
     * @param date target date
     * @return small date format.
     */
    public String getSmallDate(Date date){
        return getSmallDate(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Default format time string from input date and locale.
     * @param date target date
     * @param locale target locale
     * @return time format.
     */
    public String getTime(Date date, Locale locale){
        return getTime(date, TimeZone.getDefault(), locale);
    }

    /**
     * Default format time string from input date and default locale.
     * @param date target date
     * @return time format.
     */
    public String getTime(Date date){
        return getTime(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Default format small time string from input date and locale.
     * @param date target date
     * @param locale target locale
     * @return small time format.
     */
    public String getSmallTime(Date date, Locale locale) {
        return getSmallTime(date, TimeZone.getDefault(), locale);
    }

    /**
     * Default format time string from input date and default locale.
     * @param date target date
     * @return small time format.
     */
    public String getSmallTime(Date date){
        return getSmallTime(date, LocaleHelper.getDefaultLocale());
    }
    //endregion

    //region Any Date
    private Date parseDate(String date, String pattern, TimeZone timeZone) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleHelper.getDefaultLocale());
            sdf.setTimeZone(timeZone == null ? sdf.getTimeZone() : timeZone);
            return sdf.parse(date);
        }catch (ParseException ex){
            return null;
        }
    }

    private Date parseDate(String date, TimeZone timeZone, int index, String... patterns){
        Date r = parseDate(date, patterns[index++], timeZone);
        return index == patterns.length || r != null ? r : parseDate(date, timeZone, index, patterns);
    }

    private Date parseDate(String date, TimeZone timeZone, String... patterns){
        return parseDate(date, timeZone, 0 , patterns);
    }

    /**
     * Parse and convert target date, date and time
     * or only time string (<i>in any most format commonly knowledge</i>) to object Date.
     * @param date target string date, date and time or only time <i>in any most format commonly knowledge</i>.
     * @return object date generated from date/date and time/time string
     */
    public Date parseDate(String date) {
        Matcher matcher = Pattern.compile(FUSE_REGEX).matcher(Objects.requireNonNull(date));
        TimeZone timeZone = TimeZone.getDefault();

        if(matcher.find()){
            String fuso = matcher.group();
            fuso = fuso.substring(0, fuso.indexOf(':')).replace("0", "");
            TimeZone utcTimeZone = new SimpleTimeZone(Integer.parseInt(fuso) * (60 * 60 * 1000), "UTC" + fuso);
            String cT = date.contains("T") ? "'T'" : " ";
            return parseDate(date, utcTimeZone, "yyyy-MM-dd"+cT+"HH:mm:ss Z",
                    "yyyy-MM-dd"+cT+"HH:mm:ss.SSS Z",
                    "yyyy-MM-dd"+cT+"HH:mm:ss",
                    "yyyy-MM-dd"+cT+"HH:mm:ss.SSS");
        } else if(date.contains("T")) {
            timeZone = date.endsWith("Z") ? TimeZone.getTimeZone("UTC") : TimeZone.getTimeZone("PST");
            return parseDate(date, timeZone,  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss");
        } else {
            return parseDate(date, timeZone, "dd/MM/yyyy HH:mm:ss.SSS",
                    "dd/MM/yyyy HH:mm:ss",
                    "dd/MM/yyyy HH:mm",
                    "dd/MM/yyyy",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd",
                    "HH:mm:ss.SSS",
                    "HH:mm:ss",
                    "HH:mm");
        }
    }

    /**
     * Parse and convert target date, date and time
     * or only time string (<i>in any most format commonly knowledge</i>) to object Calendar.
     * @param date target string date, date and time or only time <i>in any most format commonly knowledge</i>.
     * @return object calendar generated from date/date and time/time string
     */
    public Calendar parseCalendar(String date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        return cal;
    }

    /**
     * Apply date format to target string date (as date, date and time or only time <i>in any most format commonly knowledge</i>).
     * The format to be applied is defined in FormatMode parameter and follow the locale format rule.
     * @param date target string date, date and time or only time <i>in any most format commonly knowledge</i>.
     * @param locale target locale
     * @param mode format mode
     * @return date formated to, full date and time, only date, only time, small date or small time
     * following the Locale rule for each format.
     */
    private String getFormattedDate(String date, Locale locale, FormatMode mode) {
        return getFormattedDate(parseDate(Objects.requireNonNull(date)), null, locale, mode);
    }

    /**
     * Apply date format to target string date (as date, date and time or only time <i>in any most format commonly knowledge</i>).
     * The format to be applied is defined in FormatMode parameter and follow the time zone and locale format rule.
     * @param date target string date, date and time or only time <i>in any most format commonly knowledge</i>.
     * @param timeZone target time zone
     * @param locale target locale
     * @param mode format mode
     * @return date formated to, full date and time, only date, only time, small date or small time
     * following the Locale and time zone rule for each format.
     */
    private String getFormattedDate(Date date, TimeZone timeZone, Locale locale, FormatMode mode) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(locale);
        DateFormat df;
        switch (mode) {
            case FULL:
                df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, locale);
                break;
            case DATE:
                df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
                break;
            case SMALL_DATE:
                df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
                break;
            case TIME:
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
                break;
            case SMALL_TIME:
                df = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
                break;
            default:
                throw new IllegalArgumentException();
        }

        if(timeZone != null) {
            df.setTimeZone(timeZone);
        }

        return df.format(date);
    }

    /**
     * Format target object date using pattern and time zone defined.
     * @param date date object
     * @param pattern date format pattern
     * @param timeZone time zone
     * @return date formated to pattern following the current locale and time zone.
     */
    public String getFormattedDate(Date date, String pattern, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleHelper.getDefaultLocale());
        sdf.setTimeZone(timeZone == null ? sdf.getTimeZone() : timeZone);
        return sdf.format(date);
    }

    /**
     * Format target object date using pattern and default time zone defined.
     * @param date date object
     * @param pattern date format pattern
     * @return date formated to pattern following the current locale and time zone.
     */
    public String getFormattedDate(Date date, String pattern) {
        return getFormattedDate(date, pattern, TimeZone.getDefault());
    }

    /**
     * Format target object date to full date and time format using pattern of target locale.
     * @param date date object.
     * @param locale target locale.
     * @return date formated to pattern of locale.
     */
    public String getFullDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.FULL);
    }

    /**
     * Format target object date to full date and time format using pattern of current locale.
     * @param date date object.
     * @return date formated to pattern of locale.
     */
    public String getFullDate(String date) {
        return getFullDate(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Format target object date to date format using pattern of target locale.
     * @param date date object.
     * @param locale target locale.
     * @return date formated to pattern of locale.
     */
    public String getDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.DATE);
    }

    /**
     * Format target object date to date format using pattern of current locale.
     * @param date date object.
     * @return date formated to pattern of locale.
     */
    public String getDate(String date) {
        return getDate(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Format target object date to small date format using pattern of target locale.
     * @param date date object.
     * @param locale target locale.
     * @return small date formated to pattern of locale.
     */
    public String getSmallDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.SMALL_DATE);
    }

    /**
     * Format target object date to small date format using pattern of current locale.
     * @param date date object.
     * @return small date formated to pattern of locale.
     */
    public String getSmallDate(String date) {
        return getSmallDate(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Format target object date to time format using pattern of target locale.
     * @param date date object.
     * @param locale target locale.
     * @return time formated to pattern of locale.
     */
    public String getTime(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.TIME);
    }

    /**
     * Format target object date to time format using pattern of current locale.
     * @param date date object.
     * @return time formated to pattern of locale.
     */
    public String getTime(String date) {
        return getTime(date, LocaleHelper.getDefaultLocale());
    }

    /**
     * Format target object date to small time format using pattern of target locale.
     * @param date date object.
     * @param locale target locale.
     * @return small time formated to pattern of locale.
     */
    public String getSmallTime(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.SMALL_TIME);
    }

    /**
     * Format target object date to small time format using pattern of current locale.
     * @param date date object.
     * @return small time formated to pattern of locale.
     */
    public String getSmallTime(String date) {
        return getSmallTime(date, LocaleHelper.getDefaultLocale());
    }
    //endregion
}
