package com.atomatus.util;

import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents a duration of time.
 */
public final class TimeSpan implements Comparable<TimeSpan>, Serializable {

    /** The number of ticks per millisecond. */
    public static final long TICKS_PER_MILLISECOND = 10000L;
    private static final double MILLISECONDS_PER_TICK = 1D / TICKS_PER_MILLISECOND;

    /** The number of ticks per second. */
    public static final long TICKS_PER_SECOND = TICKS_PER_MILLISECOND * 1000L;
    private static final double SECONDS_PER_TICK = 1D / TICKS_PER_SECOND;

    /** The number of ticks per minute. */
    public static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60L;
    private static final double MINUTES_PER_TICK = 1D / TICKS_PER_MINUTE;

    /** The number of ticks per hour. */
    public static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
    private static final double HOURS_PER_TICK = 1D / TICKS_PER_HOUR;

    /** The number of ticks per day. */
    public static final long TICKS_PER_DAY = TICKS_PER_HOUR * 24;
    private static final double DAYS_PER_TICK = 1D / TICKS_PER_DAY;

    private static final int MILLIS_PER_SECOND = 1000;
    private static final int MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    private static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
    private static final int MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

    private static final long MAX_SECONDS = Long.MAX_VALUE / TICKS_PER_SECOND;
    private static final long MIN_SECONDS = Long.MIN_VALUE / TICKS_PER_SECOND;

    private static final long MAX_MILLISECONDS = Long.MAX_VALUE / TICKS_PER_MILLISECOND;
    private static final long MIN_MILLISECONDS = Long.MIN_VALUE / TICKS_PER_MILLISECOND;

    private static final int BIT_SIGN_POSITION = Long.SIZE - 1;

    /** Represents a TimeSpan with zero ticks. */
    public static final TimeSpan ZERO = new TimeSpan(0);

    /** Represents the maximum possible TimeSpan value. */
    public static final TimeSpan MAX_VALUE = new TimeSpan(Long.MAX_VALUE);

    /** Represents the minimum possible TimeSpan value. */
    public static final TimeSpan MIN_VALUE = new TimeSpan(Long.MIN_VALUE);

    private final long ticks;

    /**
     * Initializes a new instance of the TimeSpan class with the specified number of ticks.
     * @param ticks The number of ticks.
     */
    public TimeSpan(long ticks) {
        this.ticks = ticks;
    }

    /**
     * Initializes a new instance of the TimeSpan class with the specified hours, minutes, and seconds.
     * @param hours The hours component.
     * @param minutes The minutes component.
     * @param seconds The seconds component.
     */
    public TimeSpan(int hours, int minutes, int seconds) {
        this.ticks = timeToTicks(hours, minutes, seconds);
    }

    /**
     * Initializes a new instance of the TimeSpan class with the specified days, hours, minutes, and seconds.
     * @param days The days component.
     * @param hours The hours component.
     * @param minutes The minutes component.
     * @param seconds The seconds component.
     */
    public TimeSpan(int days, int hours, int minutes, int seconds) {
        this(days, hours, minutes, seconds, 0);
    }

    /**
     * Initializes a new instance of the TimeSpan class with the specified days, hours, minutes, seconds, and milliseconds.
     * @param days The days component.
     * @param hours The hours component.
     * @param minutes The minutes component.
     * @param seconds The seconds component.
     * @param milliseconds The milliseconds component.
     */
    public TimeSpan(int days, int hours, int minutes, int seconds, int milliseconds) {
        long totalMilliseconds = (days * 3600L * 24L + hours * 3600L + minutes * 60L + seconds) * 1000L + milliseconds;
        if (totalMilliseconds > MAX_MILLISECONDS) {
            throw new IllegalArgumentException("TimeSpan is too long. Total milliseconds is larger than MAX (" + MAX_MILLISECONDS + ")!");
        } else if (totalMilliseconds < MIN_MILLISECONDS) {
            throw new IllegalArgumentException("TimeSpan is too long. Total milliseconds is smaller than MIN (" + MIN_MILLISECONDS + ")");
        }
        this.ticks = totalMilliseconds * TICKS_PER_MILLISECOND;
    }

    /**
     * Gets the total number of ticks represented by this TimeSpan.
     * @return The total number of ticks.
     */
    public long getTicks() {
        return ticks;
    }

    /**
     * Gets the days component of this TimeSpan.
     * @return The days component.
     */
    public int getDays() {
        return (int) (ticks / TICKS_PER_DAY);
    }

    /**
     * Gets the hours component of this TimeSpan.
     * @return The hours component.
     */
    public int getHours() {
        return (int) ((ticks / TICKS_PER_HOUR) % 24);
    }

    /**
     * Gets the milliseconds component of this TimeSpan.
     * @return The milliseconds component.
     */
    public int getMilliseconds() {
        return (int) ((ticks / TICKS_PER_MILLISECOND) % 1000);
    }

    /**
     * Gets the minutes component of this TimeSpan.
     * @return The minutes component.
     */
    public int getMinutes() {
        return (int) ((ticks / TICKS_PER_MINUTE) % 60);
    }

    /**
     * Gets the seconds component of this TimeSpan.
     * @return The seconds component.
     */
    public int getSeconds() {
        return (int) ((ticks / TICKS_PER_SECOND) % 60);
    }

    /**
     * Gets the total duration in days.
     * @return The total duration in days.
     */
    public double getTotalDays() {
        return (double) ticks * DAYS_PER_TICK;
    }

    /**
     * Gets the total duration in hours.
     * @return The total duration in hours.
     */
    public double getTotalHours() {
        return (double) ticks * HOURS_PER_TICK;
    }

    /**
     * Gets the total duration in milliseconds.
     * @return The total duration in milliseconds.
     */
    public double getTotalMilliseconds() {
        double temp = (double) ticks * MILLISECONDS_PER_TICK;
        if (temp > MAX_MILLISECONDS) {
            return (double) MAX_MILLISECONDS;
        }
        if (temp < MIN_MILLISECONDS) {
            return (double) MIN_MILLISECONDS;
        }
        return temp;
    }

    /**
     * Gets the total duration in minutes.
     * @return The total duration in minutes.
     */
    public double getTotalMinutes() {
        return (double) ticks * MINUTES_PER_TICK;
    }

    /**
     * Gets the total duration in seconds.
     * @return The total duration in seconds.
     */
    public double getTotalSeconds() {
        return (double) ticks * SECONDS_PER_TICK;
    }

    /**
     * Adds the specified TimeSpan to this TimeSpan.
     * @param ts The TimeSpan to add.
     * @return A new TimeSpan that represents the result of the addition.
     * @throws ArithmeticException If the result is too long to be represented.
     */
    public TimeSpan add(TimeSpan ts) {
        long result = ticks + ts.ticks;
        if ((ticks >> BIT_SIGN_POSITION == ts.ticks >> BIT_SIGN_POSITION) && (ticks >> BIT_SIGN_POSITION != result >> BIT_SIGN_POSITION)) {
            throw new ArithmeticException("The duration of the TimeSpan result is too long to be represented.");
        }
        return new TimeSpan(result);
    }

    /**
     * Compares this TimeSpan with another TimeSpan.
     * @param ts The TimeSpan to compare with.
     * @return A negative value if this TimeSpan is less than the provided TimeSpan,
     *         zero if they are equal, or a positive value if this TimeSpan is greater.
     */
    @Override
    public int compareTo(TimeSpan ts) {
        return Long.compare(ticks, ts.ticks);
    }

    /**
     * Checks if this TimeSpan is equal to another object.
     * @param obj The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TimeSpan && ticks == ((TimeSpan) obj).ticks;
    }

    /**
     * Computes a hash code for this TimeSpan.
     * @return A hash code value for this TimeSpan.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(ticks);
    }

    /**
     * Negates the value of this TimeSpan.
     * @return A new TimeSpan representing the negated value.
     * @throws ArithmeticException If negating this TimeSpan would result in a value exceeding the minimum representable value.
     */
    public TimeSpan negate() {
        if (ticks == MIN_VALUE.ticks) {
            throw new ArithmeticException("The operation would result in a TimeSpan value exceeding the minimum representable value.");
        }
        return new TimeSpan(-ticks);
    }

    /**
     * Subtracts the specified TimeSpan from this TimeSpan.
     * @param ts The TimeSpan to subtract.
     * @return A new TimeSpan that represents the result of the subtraction.
     * @throws ArithmeticException If the result is too long to be represented.
     */
    public TimeSpan subtract(TimeSpan ts) {
        long result = ticks - ts.ticks;
        if ((ticks >> BIT_SIGN_POSITION != ts.ticks >> BIT_SIGN_POSITION) && (ticks >> BIT_SIGN_POSITION != result >> BIT_SIGN_POSITION)) {
            throw new ArithmeticException("The duration of the TimeSpan result is too long to be represented.");
        }
        return new TimeSpan(result);
    }

    /**
     * Converts a time represented in hours, minutes, and seconds into ticks.
     * @param hour The hours component.
     * @param minute The minutes component.
     * @param second The seconds component.
     * @return The equivalent number of ticks.
     * @throws ArithmeticException If the resulting number of ticks is too long to be represented.
     */
    private long timeToTicks(int hour, int minute, int second) {
        long totalSeconds = (long) hour * 3600 + (long) minute * 60 + second;
        if (totalSeconds > MAX_SECONDS || totalSeconds < MIN_SECONDS) {
            throw new ArithmeticException("The duration of the TimeSpan result is too long to be represented.");
        }
        return totalSeconds * TICKS_PER_SECOND;
    }
    /**
     * Creates a TimeSpan from a specified numeric value scaled by a given factor.
     *
     * @param value The numeric value to convert.
     * @param scale The scaling factor to apply to the value.
     * @return A TimeSpan representing the specified duration based on the scaled value.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    private static TimeSpan interval(double value, int scale) {
        double tmp = value * scale;
        double millis = tmp + (value >= 0 ? 0.5D : -0.5D);

        if ((millis > ((double) Long.MAX_VALUE / TICKS_PER_MILLISECOND)) || (millis < ((double) Long.MIN_VALUE / TICKS_PER_MILLISECOND))) {
            throw new ArithmeticException("The duration of the TimeSpan result is too long to be represented.");
        }

        return new TimeSpan((long) (millis * TICKS_PER_MILLISECOND));
    }

    /**
     * Creates a TimeSpan from a specified number of days.
     * @param value The number of days.
     * @return A TimeSpan representing the specified duration in days.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromDays(double value) {
        return interval(value, MILLIS_PER_DAY);
    }

    /**
     * Creates a TimeSpan from a specified number of hours.
     * @param value The number of hours.
     * @return A TimeSpan representing the specified duration in hours.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromHours(double value) {
        return interval(value, MILLIS_PER_HOUR);
    }

    /**
     * Creates a TimeSpan from a specified number of minutes.
     * @param value The number of minutes.
     * @return A TimeSpan representing the specified duration in minutes.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromMinutes(double value) {
        return interval(value, MILLIS_PER_MINUTE);
    }

    /**
     * Creates a TimeSpan from a specified number of seconds.
     * @param value The number of seconds.
     * @return A TimeSpan representing the specified duration in seconds.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromSeconds(double value) {
        return interval(value, MILLIS_PER_SECOND);
    }

    /**
     * Creates a TimeSpan from a specified number of ticks.
     * @param value The number of ticks.
     * @return A TimeSpan representing the specified number of ticks.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromTicks(long value) {
        return new TimeSpan(value);
    }

    /**
     * Creates a TimeSpan from a specified number of milliseconds.
     * @param value The number of millis.
     * @return A TimeSpan representing the specified duration in millis.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan fromMillis(long value) {
        return interval(value, 1);
    }

    /**
     * Calculates the time difference between two Calendar instances.
     *
     * @param oldest The older Calendar instance.
     * @param newest The newer Calendar instance.
     * @return A TimeSpan representing the time difference between the two Calendars.
     * @throws NullPointerException if either oldest or newest is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diff(Calendar oldest, Calendar newest) {
        long o = Objects.requireNonNull(oldest, "Time oldest value is null!").getTimeInMillis();
        long n = Objects.requireNonNull(newest, "Time newest value is null!").getTimeInMillis();
        return fromMillis(n - o);
    }

    /**
     * Calculates the time difference between two Date instances.
     *
     * @param oldest The older Date instance.
     * @param newest The newer Date instance.
     * @return A TimeSpan representing the time difference between the two Dates.
     * @throws NullPointerException if either oldest or newest is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diff(Date oldest, Date newest) {
        long o = Objects.requireNonNull(oldest, "Time oldest value is null!").getTime();
        long n = Objects.requireNonNull(newest, "Time newest value is null!").getTime();
        return fromMillis(n - o);
    }

    /**
     * Calculates the time difference between two FileTime instances.
     *
     * @param oldest The older FileTime instance.
     * @param newest The newer FileTime instance.
     * @return A TimeSpan representing the time difference between the two FileTime.
     * @throws NullPointerException if either oldest or newest is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diff(FileTime oldest, FileTime newest) {
        long o = Objects.requireNonNull(oldest, "Time oldest value is null!").to(TimeUnit.MILLISECONDS);
        long n = Objects.requireNonNull(newest, "Time newest value is null!").to(TimeUnit.MILLISECONDS);
        return fromMillis(n - o);
    }

    /**
     * Calculates the time difference between the provided Calendar instance and the current time.
     *
     * @param oldest The Calendar instance to calculate the difference from.
     * @return A TimeSpan representing the time difference between the provided Calendar and the current time.
     * @throws NullPointerException if the provided Calendar instance is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diffNow(Calendar oldest) {
        return diff(oldest, Calendar.getInstance());
    }

    /**
     * Calculates the time difference between the provided Date instance and the current time.
     *
     * @param oldest The Date instance to calculate the difference from.
     * @return A TimeSpan representing the time difference between the provided Date and the current time.
     * @throws NullPointerException if the provided Date instance is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diffNow(Date oldest) {
        return diff(oldest, new Date());
    }

    /**
     * Calculates the time difference between the provided FileTime instance and the current time.
     *
     * @param oldest The FileTime instance to calculate the difference from.
     * @return A TimeSpan representing the time difference between the provided FileTime and the current time.
     * @throws NullPointerException if the provided FileTime instance is null.
     * @throws ArithmeticException If the resulting TimeSpan value is too long to be represented.
     */
    public static TimeSpan diffNow(FileTime oldest) {
        return diff(oldest, FileTime.fromMillis(Calendar.getInstance().getTimeInMillis()));
    }

}
