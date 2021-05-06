package com.atomatus.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper to analyze and convert wrapper or decimal types to BigDecimal, currency or decimal
 * by discover Locale automatically or locale set it.
 * @author Carlos Matos
 */
public final class DecimalHelper {

    //region default decimals
    /**
     * Zero for big decimal.
     */
    public static final BigDecimal ZERO;

    /**
     * One for big decimal.
     */
    public static final BigDecimal ONE;

    /**
     * Ten for big decimal.
     */
    public static final BigDecimal TEN;

    /**
     * One Hundred for big decimal.
     */
    public static final BigDecimal ONE_HUNDRED;

    /**
     * One Thousand for big decimal.
     */
    public static final BigDecimal ONE_THOUSAND;

    /**
     * One Tenth for big decimal.
     */
    public static final BigDecimal ONE_TENTH;

    /**
     * One Hundredth for big decimal.
     */
    public static final BigDecimal ONE_HUNDREDTH;

    /**
     * One Thousandth for big decimal.
     */
    public static final BigDecimal ONE_THOUSANDTH;
    //endregion

    private static Locale lastLocaleByCurrency;
    private static final String CURRENCY_SYMBOL_REGEX;
    private static final MathContext DEFAULT_MATH_CONTEXT;
    private static final int DEFAULT_SCALE, MAX_SCALE;

    /**
     * Decimal symbol informations, how like,
     * decimal symbol, group symbol and currency symbol.
     */
    public static class Info {

        private char decimalSymbol;
        private char groupSymbol;
        private String currencySymbol;

        private Info() { }

        /**
         * Decimal Symbol
         * @return decimal symbol
         */
        public char getDecimalSymbol() {
            return decimalSymbol;
        }

        /**
         * Currency Symbol
         * @return currency symbol
         */
        public String getCurrencySymbol() {
            return currencySymbol;
        }

        /**
         * Group Symbol
         * @return group symbol
         */
        public char getGroupSymbol() {
            return groupSymbol;
        }

        private static Info getInstance(Locale locale) {
            DecimalFormat dFormat           = (DecimalFormat) NumberFormat.getInstance(locale);
            DecimalFormatSymbols symbols    = dFormat.getDecimalFormatSymbols();
            Info i = new Info();
            i.currencySymbol    = symbols.getCurrencySymbol();
            i.decimalSymbol     = symbols.getDecimalSeparator();
            i.groupSymbol       = symbols.getGroupingSeparator();
            return i;
        }
    }

    static {
        lastLocaleByCurrency    = LocaleHelper.getDefaultLocale();
        CURRENCY_SYMBOL_REGEX   = "(?<=^\\-)([^\\d\\s\\.\\,]+)|(^[^\\d\\s\\.\\,\\-]+)|([^\\d\\s\\.\\-]+$)";
        DEFAULT_MATH_CONTEXT    = MathContext.DECIMAL128;
        DEFAULT_SCALE           = 2;
        MAX_SCALE               = 3;

        ZERO                    = BigDecimal.ZERO;
        TEN                     = BigDecimal.TEN;
        ONE                     = BigDecimal.ONE;
        ONE_HUNDRED             = new BigDecimal(100);
        ONE_THOUSAND            = new BigDecimal(1000);
        ONE_TENTH               = new BigDecimal("0.1");
        ONE_HUNDREDTH           = new BigDecimal("0.01");
        ONE_THOUSANDTH          = new BigDecimal("0.001");
    }

    private DecimalHelper() { }

    /**
     * Check if current String is null, empty or whitespace.
     * @param value target string
     * @return true when target string is null, empty or whitespace.
     */
    private static boolean isNullOrWhitespace(String value){
        return value == null || value.trim().isEmpty();
    }

    private static Locale getLocaleByCurrency(String value, Iterable<Locale> locales){
        if(isNullOrWhitespace(value)){
            return null;
        }

        Pattern p = Pattern.compile(CURRENCY_SYMBOL_REGEX);
        Matcher m = p.matcher(value);
        int invalidIndex  = -1;
        boolean hasSymbol = m.find();
        String curSymbol  = hasSymbol ? m.group() : null;
        Locale candidate  = null;

        for (Locale locale : locales) {
            Info i = Info.getInstance(locale);
            if(!hasSymbol || (i.currencySymbol != null && i.currencySymbol.equalsIgnoreCase(curSymbol))){
                int dsi = value.indexOf(i.decimalSymbol);
                int gsi = value.indexOf(i.groupSymbol);

                if(dsi > invalidIndex){
                    if(gsi == invalidIndex) {
                        if(!hasSymbol){
                            return locale;
                        } else{
                            candidate = locale;
                        }
                    } else if(gsi < dsi){
                        return locale;
                    }
                }
            }
        }

        return candidate;
    }

    private static int getScaleFromStrDoubleValue(String value) {
        if(value.indexOf('.') != -1) {
            String[] arr = value.split("\\.");
            int digits = arr[arr.length - 1].length();
            return Math.min(Math.max(digits, DEFAULT_SCALE), MAX_SCALE);
        }
        return DEFAULT_SCALE;
    }

    /**
     * Get informations, about monetary symbols from locale.
     * @param locale target locale.
     * @return monetary informations.
     */
    public static Info getLocaleInfo(Locale locale) {
        return Info.getInstance(locale);
    }

    /**
     * Get informations, about monetary symbols from current locale.
     * @return monetary informations.
     */
    public static Info getLocaleInfo() {
        return Info.getInstance(lastLocaleByCurrency);
    }

    /**
     * Get appropriate Locale to input (decimal or currency) value.
     * @param value decimal or currency value.
     * @return appropriate Locale found.
     */
    public static synchronized Locale getLocaleByCurrency(String value){
        Locale aux;
        return lastLocaleByCurrency =
                (aux = getLocaleByCurrency(value, LocaleHelper.getLocales(lastLocaleByCurrency))) != null ?
                        aux : lastLocaleByCurrency;
    }

    /**
     * Convert generic type to BigDecimal.
     * @param value target value
     * @param <T> type of target
     * @return a new bigDecimal from value.
     */
    public static <T> BigDecimal toBigDecimal(T value){
        if(value == null){
            return BigDecimal.ZERO;
        } else if(value instanceof String){
            return toBigDecimal((String) value);
        } else if(value instanceof BigDecimal){
            return ((BigDecimal) value).add(BigDecimal.ZERO, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof BigInteger){
            return new BigDecimal((BigInteger) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Integer){
            return new BigDecimal((Integer) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Long){
            return new BigDecimal((Long) value, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof Double || value instanceof Float) {
            String v = String.valueOf(value);
            return new BigDecimal(v, DEFAULT_MATH_CONTEXT)
                    .setScale(DEFAULT_SCALE, DEFAULT_MATH_CONTEXT.getRoundingMode());
        } else if(value instanceof Boolean) {
            return new BigDecimal((Boolean) value ? 1L : 0L, DEFAULT_MATH_CONTEXT);
        } else if(value instanceof char[]) {
            char[] cArr = (char[]) value;
            if(cArr.length == 0) {
                return BigDecimal.ZERO;
            }
            for(char c : cArr) {
                if(!Character.isDigit(c)) {
                    return toBigDecimal(new String(cArr));
                }
            }
            return toBigDecimal(Long.parseLong(new String(cArr)));
        } else if(value.getClass().isArray()) {
            Object[] arr = ArrayHelper.toArray(value);
            return toBigDecimal(
                    ArrayHelper.reduce(arr,
                            StringBuilder::append,
                            new StringBuilder())
                            .toString());
        } else {
            return toBigDecimal(value.toString());
        }
    }

    private static BigDecimal toBigDecimal(double value) {
        String str = String.valueOf(value);
        return new BigDecimal(str, DEFAULT_MATH_CONTEXT)
                .setScale(getScaleFromStrDoubleValue(str), DEFAULT_MATH_CONTEXT.getRoundingMode());
    }

    private static BigDecimal toBigDecimalNS(double value) {
        String str = String.valueOf(value);
        return new BigDecimal(str, DEFAULT_MATH_CONTEXT);
    }

    /**
     * Convert input value to bigDecimal using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new bigDecimal from value and locale.
     */
    public static BigDecimal toBigDecimal(String value, Locale locale) {
        return toBigDecimal(toDouble(value, locale));
    }

    /**
     * Convert input value to bigDecimal using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimal(String value) {
        return toBigDecimal(toDouble(value));
    }

    /**
     * Convert input value to bigDecimal using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimal(Number value) {
        return toBigDecimal(value.doubleValue());
    }

    /**
     * Convert input value to bigDecimal (not scaling) using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new bigDecimal from value and locale.
     */
    public static BigDecimal toBigDecimalNS(String value, Locale locale) {
        return toBigDecimalNS(toDouble(value, locale));
    }

    /**
     * Convert input value to bigDecimal (not scaling) using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimalNS(String value) {
        return toBigDecimalNS(toDouble(value));
    }

    /**
     * Convert input value to bigDecimal (not scaling) using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new bigDecimal from value and locale (autodiscovery).
     */
    public static BigDecimal toBigDecimalNS(Number value) {
        return toBigDecimalNS(value.doubleValue());
    }

    /**
     * Convert input value to number using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new number from value and locale.
     */
    private static Number toNumber(String value, Locale locale){
        if(isNullOrWhitespace(value)){
            return 0;
        }

        try {
            value = value.replaceAll("\\s", "");
            Pattern p = Pattern.compile(CURRENCY_SYMBOL_REGEX);
            Matcher m = p.matcher(value);
            NumberFormat nFormat = NumberFormat.getInstance(locale);
            return nFormat.parse(m.find() ? value.replace(m.group(), "") : value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert input value to double using locale.
     * @param value target decimal or currency value
     * @param locale locale target
     * @return new double from value and locale.
     */
    public static double toDouble(String value, Locale locale) {
        return toNumber(value, locale).doubleValue();
    }

    /**
     * Convert input value to double using autodiscovery locale.
     * @param value target decimal or currency value.
     * @return new double from value and locale (autodiscovery).
     */
    public static double toDouble(String value) {
        return toDouble(value, getLocaleByCurrency(value));
    }

    /**
     * Convert input value to string from bigDecimal using locale.
     * @param value target value
     * @param locale locale target
     * @return currency string from value and locale.
     */
    public static String toCurrency(BigDecimal value, Locale locale) {
        value = value == null ? BigDecimal.ZERO : value;
        NumberFormat cFormat = NumberFormat.getCurrencyInstance(locale);
        return cFormat.format(value.doubleValue());
    }

    /**
     * Convert input value to string from bigDecimal using autodiscovery locale.
     * @param value target value
     * @return currency string from value and locale (autodiscovery).
     */
    public static String toCurrency(BigDecimal value) {
        return toCurrency(value, lastLocaleByCurrency);
    }

    /**
     * Convert input value to string from bigDecimal using locale.
     * @param value target value
     * @param locale locale target
     * @return decimal string from value and locale.
     */
    public static String toDecimal(BigDecimal value, Locale locale) {
        value = value == null ? BigDecimal.ZERO : value;
        DecimalFormat dFormat = (DecimalFormat)NumberFormat.getInstance(locale);
        dFormat.setMinimumFractionDigits(value.scale());
        return dFormat.format(value.doubleValue());
    }

    /**
     * Convert input value to string from bigDecimal using autodiscovery locale.
     * @param value target value
     * @return decimal string from value and locale (autodiscovery).
     */
    public static String toDecimal(BigDecimal value) {
        return toDecimal(value, lastLocaleByCurrency);
    }

    @SuppressWarnings("unchecked")
    private static <T> T fromBigDecimal(BigDecimal value, Class<T> clazz) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(clazz);
        if(clazz == BigDecimal.class) {
            return (T) value;
        } else if(clazz == BigInteger.class) {
            return (T) value.toBigInteger();
        } else if(clazz == String.class) {
            return (T) toDecimal(value);
        } else if(clazz == Integer.class) {
            return (T) Integer.valueOf(value.intValue());
        } else if(clazz == Long.class) {
            return (T) Long.valueOf(value.longValue());
        } else if(clazz == Short.class) {
            return (T) Short.valueOf(value.shortValue());
        } else if(clazz == Float.class) {
            return (T) Float.valueOf(value.floatValue());
        } else if(clazz == Double.class) {
            return (T) Double.valueOf(value.doubleValue());
        } else if(clazz == Boolean.class) {
            return (T) Boolean.valueOf(value.intValue() == 1);
        } else {
            throw new UnsupportedOperationException("Request class \""+clazz.getName()+"\" is not a number type!");
        }
    }

    private static <T, R> R multiply(T value, Class<R> clazz, BigDecimal multiplicand, int scale, RoundingMode roundingMode) {
        if(Objects.requireNonNull(value) instanceof BigDecimal) {
            return fromBigDecimal(((BigDecimal)value)
                    .multiply(multiplicand)
                    .setScale(scale, roundingMode), clazz);
        } else {
            return fromBigDecimal(toBigDecimal(value)
                    .multiply(multiplicand)
                    .setScale(scale, roundingMode), clazz);
        }
    }

    /**
     * Convert any common number object to any common number type to cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param clazz result class type.
     * @param <T> target value type
     * @param <R> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T, R> R toCents(T value, int newScale, Class<R> clazz) {
        return multiply(value, clazz, DecimalHelper.ONE_HUNDRED, newScale, RoundingMode.HALF_EVEN);
    }

    /**
     * Convert any common number object to any common number type to cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param clazz result class type.
     * @param <T> target value type
     * @param <R> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T, R> R toCents(T value, Class<R> clazz) {
        return multiply(value, clazz, DecimalHelper.ONE_HUNDRED, DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * Convert any common number object to BigDecimal in cents.
     * @param value target value
     * @param newScale new scale precision.
     * @param <T> target type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> BigDecimal toCents(T value, int newScale) {
        return toCents(value, newScale, BigDecimal.class);
    }

    /**
     * Convert any common number object to BigDecimal in cents.
     * @param value target value
     * @param <T> target type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> BigDecimal toCents(T value) {
        return toCents(value, BigDecimal.class);
    }

    /**
     * Convert any common number object to any common number type.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param <T> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> int toCentsAsInt(T value, int newScale) {
        return toCents(value, newScale, Integer.class);
    }

    /**
     * Convert any common number object to any common number type.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param <T> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> int toCentsAsInt(T value) {
        return toCents(value, DEFAULT_SCALE, Integer.class);
    }

    /**
     * Convert any common number object to any common number type.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param <T> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> long toCentsAsLong(T value, int newScale) {
        return toCents(value, newScale, Long.class);
    }

    /**
     * Convert any common number object to any common number type.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param <T> result value type
     * @return result value generated from conversion of target origin value to cents.
     */
    public static <T> long toCentsAsLong(T value) {
        return toCents(value, Long.class);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param clazz result class type.
     * @param <T> target value type
     * @param <R> result value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T, R> R fromCents(T value, int newScale, Class<R> clazz) {
        return multiply(value, clazz, DecimalHelper.ONE_HUNDREDTH, newScale, RoundingMode.HALF_EVEN);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param clazz result class type.
     * @param <T> target value type
     * @param <R> result value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T, R> R fromCents(T value, Class<R> clazz) {
        return fromCents(value, DEFAULT_SCALE, clazz);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> BigDecimal fromCents(T value, int newScale) {
        return fromCents(value, BigDecimal.class).setScale(newScale, RoundingMode.HALF_EVEN);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> BigDecimal fromCents(T value) {
        return fromCents(value, DEFAULT_SCALE);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> int fromCentsAsInt(T value, int newScale) {
        return fromCents(value, newScale, Integer.class);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> int fromCentsAsInt(T value) {
        return fromCents(value, DEFAULT_SCALE, Integer.class);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param newScale new scale precision.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> long fromCentsAsLong(T value, int newScale) {
        return fromCents(value, newScale, Long.class);
    }

    /**
     * Convert any common number object to any common number type from cents.<br>
     * <i>Common number types: Short, Integer, Long, Float, Double, BigInteger or BigDecimal.</i>
     * @param value target origin value to be converted to result type.
     * @param <T> result target value type
     * @return result value generated from conversion of target origin value from cents.
     */
    public static <T> long fromCentsAsLong(T value) {
        return fromCents(value, Long.class);
    }
}
