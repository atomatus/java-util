package com.atomatus.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Local class to format {@link TimeSpan}.
 */
final class TimeSpanFormatter {

    private static final String FORMAT_REGEX = "(?<day>d+)|(?<tday>D+)|(?<hour>h+)|(?<thour>H+)|(?<min>m+)|(?<tmin>M+)|(?<sec>s+)|(?<tsec>S+)|(?<millis>t+)|(?<tmillis>T+)";
    private static final String DAY_REGEX = "^[d|D]+$";
    private static final String HOUR_REGEX = "^[h|H]+$";
    private static final String MIN_REGEX = "^[m|M]+$";
    private static final String SEC_REGEX = "^[s|S]+$";
    private static final String MILLIS_REGEX = "^[t|T]+$";

    private TimeSpanFormatter() { }

    /**
     * Formats the TimeSpan using the provided format.
     *
     * @param timeSpan The target time span.
     * @param format The format string with placeholders "DD" for total days, "HH" for total hours,
     *               "MM" for total minutes "SS" for total seconds and "TT" for total millis;
     *               "dd" for days, "hh" for hours, "mm" for minutes, "ss" for seconds, "tt" for millis;
     * @return The formatted TimeSpan as a string.
     */
    static String format(TimeSpan timeSpan, String format) {
        Map<String, Supplier<Number>> solver = new HashMap<>();
        solver.put("day", timeSpan::getDays);
        solver.put("tday", timeSpan::getTotalDays);
        solver.put("hour", timeSpan::getHours);
        solver.put("thour", timeSpan::getTotalHours);
        solver.put("min", timeSpan::getMinutes);
        solver.put("tmin", timeSpan::getTotalMinutes);
        solver.put("sec", timeSpan::getSeconds);
        solver.put("tsec", timeSpan::getTotalSeconds);
        solver.put("millis", timeSpan::getMilliseconds);
        solver.put("tmillis", timeSpan::getTotalMilliseconds);
        return format(new StringBuilder(format), FORMAT_REGEX, solver).toString();
    }

    private static StringBuilder format(StringBuilder format, String regex, Map<String, Supplier<Number>> values) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(format);

        while (matcher.find()) {
            for(String name : values.keySet()) {
                String mask = matcher.group(name);
                if(mask != null) {
                    Number number = values.get(name).get();
                    return format(format.replace(matcher.start(), matcher.end(),
                                    formatNumberWithZeroLeading(number, mask.length())),
                            regex, values);
                }
            }
        }
        return format;
    }

    private static String formatNumberWithZeroLeading(Number number, Integer zeroLeading) {
        return String.format("%0" + zeroLeading + "d", number.longValue());
    }

    /**
     * Convert back a formatted time span string to TimeSpan object.
     * @param value formatted value
     * @param format base format that genered string.
     *               The format string with placeholders "DD" for total days, "HH" for total hours,
     *               "MM" for total minutes "SS" for total seconds and "TT" for total millis;
     *               "dd" for days, "hh" for hours, "mm" for minutes, "ss" for seconds, "tt" for millis;
     * @return The time span from formatted value.
     */
    static TimeSpan fromFormat(String value, String format) {
        TimeSpan timeSpan = TimeSpan.fromMillis(0L);
        if(StringUtils.isNonNullAndNonWhitespace(value)) {
            Map<String, Function<Long, TimeSpan>> solver = new HashMap<>();
            solver.put(DAY_REGEX, TimeSpan::fromDays);
            solver.put(HOUR_REGEX, TimeSpan::fromHours);
            solver.put(MIN_REGEX, TimeSpan::fromMinutes);
            solver.put(SEC_REGEX, TimeSpan::fromSeconds);
            solver.put(MILLIS_REGEX, TimeSpan::fromMillis);

            int INVALID_INDEX = -1;
            int fOffset = 0;
            int vOffset = 0;

            Map<String, String> groupBy = new HashMap<>();

            for(int i=0, l = value.length(); i < l; i++) {
                char c = value.charAt(i);
                if(!Character.isDigit(c)) {
                    int j = format.indexOf(c, fOffset);
                    if (j == INVALID_INDEX) {
                        throw new StringIndexOutOfBoundsException("Input Format and value are not compatibles!");
                    }

                    String f = format.substring(fOffset, fOffset = j);
                    String v = value.substring(vOffset, vOffset = i);
                    fOffset++;
                    vOffset++;
                    groupBy.put(f, v);
                }
            }

            String f = fOffset == 0 ? format : format.substring(fOffset);
            String v = vOffset == 0 ? value  : value.substring(vOffset);
            groupBy.put(f, v);
            int solvedGroup = 0;

            nextGroupBy:
            for(String key : groupBy.keySet()) {
                for(String regexKey : solver.keySet()) {
                    if(key.matches(regexKey)) {
                        Long number = Long.valueOf(groupBy.get(key));
                        timeSpan = timeSpan.add(solver.get(regexKey).apply(number));
                        solvedGroup++;
                        continue nextGroupBy;
                    }
                }
            }

            if(solvedGroup != groupBy.size()) {
                throw new StringIndexOutOfBoundsException("Input format is not a valid TimeSpan format (use HH, hh, MM, mm, SS, ss, TT or tt)!");
            }
        }
        return timeSpan;
    }

}
