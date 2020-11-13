package com.atomatus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateHelper {

    private static final String FUSE_REGEX;
    private static final DateHelper instance;

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

    public static DateHelper getInstance() {
        return instance;
    }

    private DateHelper() { }

    //region Current Date TimeZone
    public String getDate(TimeZone timeZone, Locale locale){
        return getDate(new Date(), timeZone, locale);
    }

    public String getDate(TimeZone timeZone) {
        return getDate(timeZone, LocaleHelper.getDefaultLocale());
    }

    public String getSmallDate(TimeZone timeZone, Locale locale){
        return getSmallDate(new Date(), timeZone, locale);
    }

    public String getSmallDate(TimeZone timeZone) {
        return getSmallDate(timeZone, LocaleHelper.getDefaultLocale());
    }

    public String getTime(TimeZone timeZone, Locale locale){
        return getTime(new Date(), timeZone, locale);
    }

    public String getTime(TimeZone timeZone) {
        return getTime(timeZone, LocaleHelper.getDefaultLocale());
    }

    public String getSmallTime(TimeZone timeZone, Locale locale){
        return getSmallTime(new Date(), timeZone, locale);
    }

    public String getSmallTime(TimeZone timeZone) {
        return getSmallTime(timeZone, LocaleHelper.getDefaultLocale());
    }

    //endregion

    //region Current Date Default TimeZone
    public String getDate(Locale locale){
        return getDate(TimeZone.getDefault(), locale);
    }

    public String getDate(){
        return getDate(LocaleHelper.getDefaultLocale());
    }

    public String getSmallDate(Locale locale){
        return getSmallDate(TimeZone.getDefault(), locale);
    }

    public String getSmallDate(){
        return getSmallDate(LocaleHelper.getDefaultLocale());
    }

    public String getTime(Locale locale){
        return getTime(TimeZone.getDefault(), locale);
    }

    public String getTime(){
        return getTime(LocaleHelper.getDefaultLocale());
    }

    public String getSmallTime(Locale locale){
        return getSmallTime(TimeZone.getDefault(), locale);
    }

    public String getSmallTime(){
        return getSmallTime(LocaleHelper.getDefaultLocale());
    }
    //endregion

    //region Any Date
    public String getDate(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.DATE);
    }

    public String getSmallDate(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.SMALL_DATE);
    }

    public String getTime(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.TIME);
    }

    public String getSmallTime(Date date, TimeZone timeZone, Locale locale){
        return getFormattedDate(date, timeZone, locale, FormatMode.SMALL_TIME);
    }
    //endregion

    //region Any Date Default TimeZone
    public String getDate(Date date, Locale locale){
        return getDate(date, TimeZone.getDefault(), locale);
    }

    public String getDate(Date date){
        return getDate(date, LocaleHelper.getDefaultLocale());
    }

    public String getSmallDate(Date date, Locale locale){
        return getSmallDate(date, TimeZone.getDefault(), locale);
    }

    public String getSmallDate(Date date){
        return getSmallDate(date, LocaleHelper.getDefaultLocale());
    }

    public String getTime(Date date, Locale locale){
        return getTime(date, TimeZone.getDefault(), locale);
    }

    public String getTime(Date date){
        return getTime(date, LocaleHelper.getDefaultLocale());
    }

    public String getSmallTime(Date date, Locale locale) {
        return getSmallTime(date, TimeZone.getDefault(), locale);
    }

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

    public Calendar parseCalendar(String date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        return cal;
    }

    private String getFormattedDate(String date, Locale locale, FormatMode mode) {
        return getFormattedDate(parseDate(Objects.requireNonNull(date)), null, locale, mode);
    }

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

    public String getFullDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.FULL);
    }

    public String getFullDate(String date) {
        return getFullDate(date, LocaleHelper.getDefaultLocale());
    }

    public String getDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.DATE);
    }

    public String getDate(String date) {
        return getDate(date, LocaleHelper.getDefaultLocale());
    }

    public String getSmallDate(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.SMALL_DATE);
    }

    public String getSmallDate(String date) {
        return getSmallDate(date, LocaleHelper.getDefaultLocale());
    }

    public String getTime(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.TIME);
    }

    public String getTime(String date) {
        return getTime(date, LocaleHelper.getDefaultLocale());
    }

    public String getSmallTime(String date, Locale locale){
        return getFormattedDate(date, locale, FormatMode.SMALL_TIME);
    }

    public String getSmallTime(String date) {
        return getSmallTime(date, LocaleHelper.getDefaultLocale());
    }
    //endregion
}
