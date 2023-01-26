package com.atomatus.util;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utils to help to parse, convert,
 * join and captalize strings.
 */
@SuppressWarnings("UnusedReturnValue")
public final class StringUtils {

    /**
     * String util condition to filter.
     * @param <T> input type
     */
    @FunctionalInterface
    public interface Condition<T> {
        /**
         * Apply condition callback.
         * @param t input type
         * @return true, filter, otherwise false.
         */
        boolean apply(T t);
    }

    /**
     * Functional interface for solve input and produce output.
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
    public interface Function<I, O> {
        /**
         * Apply function to convert Input type to Output type.
         * @param i input element target.
         * @return output element generated.
         */
        O apply(I i);
    }

    /**
     * Capitalize Modes
     */
    @SuppressWarnings("unused")
    public enum Capitalize {
        /**
         * All text to lower case.
         */
        NONE,

        /**
         * All text to upper case.
         */
        ALL,

        /**
         * Only first char to upper case.
         */
        ONLY_FIRST,

        /**
         * Only first char for each word.
         */
        FIRST_EACH_WORD
    }

    private enum Direction {
        LEFT,
        RIGHT
    }

    //region require

    /**
     * Require string non null
     * @param str target string
     * @param message message error
     * @return target string
     * @exception NullPointerException throws when string is null.
     */
    public static String requireNonNull(String str, String message) {
        if(str == null) throw new NullPointerException(message);
        return str;
    }

    /**
     * Require string non null
     * @param str target string
     * @return target string
     * @exception NullPointerException throws when string is null.
     */
    public static String requireNonNull(String str) {
        return requireNonNull(str, "Input String is null!");
    }

    /**
     * Require string non null or empty
     * @param str target string
     * @param message message error
     * @return target string
     * @exception NullPointerException throws when string is null or empty.
     */
    public static String requireNonNullOrEmpty(String str, String message) {
        if(isNullOrEmpty(str)) throw new NullPointerException(message);
        return str;
    }

    /**
     * Require string non null or empty
     * @param str target string
     * @return target string
     * @exception NullPointerException throws when string is null or empty.
     */
    public static String requireNonNullOrEmpty(String str) {
        return requireNonNullOrEmpty(str, "Input String is null or empty!");
    }

    /**
     * Require string non null or is only whitespace
     * @param str target string
     * @param message message error
     * @return target string
     * @exception NullPointerException throws when string is null or is only whitespace.
     */
    public static String requireNonNullOrWhitespace(String str, String message) {
        if(isNullOrWhitespace(str)) throw new NullPointerException(message);
        return str;
    }

    /**
     * Require string non null or is only whitespace
     * @param str target string
     * @return target string
     * @exception NullPointerException throws when string is null or is only whitespace.
     */
    public static String requireNonNullOrWhitespace(String str) {
        return requireNonNullOrWhitespace(str, "Input String is null or does not contains a valid value!");
    }
    //endregion

    //region isNullOrEmpty/isNullOrWhitespace
    /**
     * Check string is null or empty
     * @param str target string
     * @return true, string is null or empty, otherwise false.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Check string is null or whitespace
     * @param str target string
     * @return true, string is null or is only whitespace, otherwise false.
     */
    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.trim().length() == 0;
    }
    //endregion

    //region isNonNullAndNonEmpty/isNonNullAndNonWhitespace
    /**
     * Check string is not null and not empty
     * @param str target string
     * @return true, string is not null and not empty, otherwise false.
     */
    public static boolean isNonNullAndNonEmpty(String str) {
        return str != null && str.length() != 0;
    }

    /**
     * Check string is not null and not whitespace
     * @param str target string
     * @return true,string is not null and not whitespace, otherwise false.
     */
    public static boolean isNonNullAndNonWhitespace(String str) {
        return str != null && str.trim().length() != 0;
    }
    //endregion

    //region equals
    /**
     * Compare whether both string are equals
     * @param str0 comparable string
     * @param str1 comparable string
     * @return true, string values are equals, false otherwise
     */
    public static boolean equals(String str0, String str1) {
        return (str0 == null && str1 == null) || (str0 != null && str0.equals(str1));
    }

    /**
     * Compare whether both string are equals ignoring case
     * @param str0 comparable string
     * @param str1 comparable string
     * @return true, string values are equals, false otherwise
     */
    public static boolean equalsIgnoreCase(String str0, String str1) {
        return (str0 == null && str1 == null) || (str0 != null && str0.equalsIgnoreCase(str1));
    }
    //endregion

    //region startsWithIgnoreCase/endsWithIgnoreCase
    /**
     * Check target string in str starts with prefix value ignoring case.
     * @param str target
     * @param prefix prefix target
     * @return true, starts with prefix value, otherwise false.
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if(str == null) {
            throw new NullPointerException("Target String is null!");
        } else if(prefix == null) {
            throw new NullPointerException("Prefix String is null!");
        }

        return str.regionMatches(true, 0, prefix, 0, prefix.length());

    }

    /**
     * Check target string in str ends with suffix value ignoring case.
     * @param str target
     * @param suffix suffix target
     * @return true, ends with suffix value, otherwise false.
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if(str == null) {
            throw new NullPointerException("Target String is null!");
        } else if(suffix == null) {
            throw new NullPointerException("Suffix String is null!");
        }

        int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }
    //endregion

    //region join
    private static void appendValueForJoin(StringBuilder sb, Object value) {
        if(value == null) {
            sb.append("null");
        } else if(value.getClass().isPrimitive())  {
            Class<?> clazz = value.getClass();
            if (Character.TYPE.equals(clazz)) {
                sb.append((char)value);
            } else if(Short.TYPE.equals(clazz) || Integer.TYPE.equals(clazz)) {
                sb.append((int)value);
            } else if(Long.TYPE.equals(clazz)) {
                sb.append((long)value);
            } else if(Float.TYPE.equals(clazz)) {
                sb.append((float)value);
            } else if(Double.TYPE.equals(clazz)) {
                sb.append((double)value);
            } else {
                sb.append(value);
            }
        } else if(value instanceof CharSequence) {
            sb.append((CharSequence) value);
        } else if(value instanceof Character) {
            sb.append(((Character) value).charValue());
        } else if(value instanceof char[]) {
            sb.append((char[]) value);
        } else if(value instanceof Integer || value instanceof Short) {
            sb.append(((Number) value).intValue());
        } else if(value instanceof Long) {
            sb.append(((Long) value).longValue());
        } else if(value instanceof Float) {
            sb.append(((Float) value).floatValue());
        } else if(value instanceof Double) {
            sb.append(((Double) value).doubleValue());
        } else if(value instanceof Boolean) {
            sb.append(((Boolean) value).booleanValue());
        } else {
            sb.append(value);
        }
    }

    /**
     * Join all target arguments values.
     * @param prefix preffix of new string
     * @param suffix suffix of new string
     * @param sep separate char between any argument value
     * @param args target arguments
     * @return new string from arguments.
     */
    public static String join(String prefix, String suffix, String sep, Object... args) {
        requireNonNull(sep, "Separator can not be null!");
        StringBuilder sb = new StringBuilder();

        if(!isNullOrEmpty(prefix)) {
            sb.append(prefix);
        }

        for (int i = 0, c = args.length; i < c; i++) {
            if(i > 0) {
                sb.append(sep);
            }
            appendValueForJoin(sb, args[i]);
        }

        if(!isNullOrEmpty(suffix)) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    /**
     * Join all target arguments values.
     * @param prefix preffix of new string
     * @param suffix suffix of new string
     * @param sep separate char between any argument value
     * @param args target arguments
     * @param <T> target type
     * @return new string from arguments.
     */
    public static <T> String join(String prefix, String suffix, String sep, Iterable<T> args) {
        requireNonNull(sep, "Separator can not be null!");
        StringBuilder sb = new StringBuilder();

        if(!isNullOrEmpty(prefix)) {
            sb.append(prefix);
        }

        boolean useSep = false;
        for(T t : args) {
            if(useSep) {
                sb.append(sep);
            }
            appendValueForJoin(sb, t);
            useSep = true;
        }

        if(!isNullOrEmpty(suffix)) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    /**
     * Join all target arguments values.
     * @param sep separate char between any argument valuee
     * @param args target arguments
     * @return new string from arguments.
     */
    public static String join(String sep, Object... args) {
        return join(null, null, sep, args);
    }

    /**
     * Join all target arguments values.
     * @param sep separate char between any argument valuee
     * @param args target arguments
     * @param <T> target type
     * @return new string from arguments.
     */
    public static <T> String join(String sep, Iterable<T> args) {
        return join(null, null, sep, args);
    }
    //endregion

    //region capitalize
    /**
     * Capitalize an input string.
     * @param input target
     * @param mode capitalize mode
     * @return new string capitalized.
     */
    public static String capitalize(String input, Capitalize mode) {
        if(isNullOrWhitespace(input)){
            return input;
        }

        boolean upper = mode != Capitalize.NONE;
        char[] curr = input.toCharArray();
        char[] result = new char[curr.length];
        for(int i=0, l = curr.length; i < l; i++) {
            char c = curr[i];
            result[i] = upper ? Character.toUpperCase(c) : Character.toLowerCase(c);
            upper = mode == Capitalize.ALL || (mode == Capitalize.FIRST_EACH_WORD && Character.isWhitespace(c));
        }

        return new String(result);
    }

    /**
     * Change target string to upper case.
     * @param input target
     * @return upper case string
     */
    public static String upper(String input) {
        return capitalize(input, Capitalize.ALL);
    }

    /**
     * Change target string to lower case.
     * @param input target
     * @return lower case string
     */
    public static String lower(String input) {
        return capitalize(input, Capitalize.NONE);
    }
    //endregion

    //region pad
    private static String pad(StringBuilder builder, int inputSize, int padLength, int size, Object pad, Direction direction) {
        if(inputSize >= size) {
            return builder.toString();
        } else if(direction == Direction.RIGHT) {
            return pad(builder.append(pad), inputSize + padLength, padLength, size, pad, direction);
        } else {
            return pad(builder.insert(0, pad), inputSize + padLength, padLength, size, pad, direction);
        }
    }

    private static String pad(String input, int size, int padLength, Object pad, Direction direction) {
        return input == null || input.length() + padLength > size ? input :
                pad(new StringBuilder(input),
                        input.length(),
                        padLength,
                        size,
                        pad,
                        direction);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     * <pre>
     *     StringUtils.padLeft("1", 3, '0'); //result =  001
     *     StringUtils.padLeft("1", 4, "xyz"); //result =  xyz1
     * </pre>
     * @param input the String to pad out, may be null
     * @param size the size to pad to
     * @param pad the character to pad with
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padLeft(String input, int size, char pad) {
        return pad(input, size, 1, pad, Direction.LEFT);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     * <pre>
     *     StringUtils.padLeft("1", 3, '0'); //result =  001
     *     StringUtils.padLeft("1", 4, "xyz"); //result =  xyz1
     * </pre>
     * @param input the String to pad out, may be null
     * @param size the size to pad to
     * @param pad the String to pad with
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padLeft(String input, int size, String pad) {
        requireNonNullOrEmpty(pad);
        return pad(input, size, pad.length(), pad, Direction.LEFT);
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     * <pre>
     *     StringUtils.padRight("1", 3, '0'); //result =  100
     *     StringUtils.padRight("1", 3, "xyz"); //result =  1
     *     StringUtils.padRight("1", 4, "xyz"); //result =  1xyz
     * </pre>
     * @param input the String to pad out, may be null
     * @param size the size to pad to
     * @param pad the character to pad with
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padRight(String input, int size, char pad) {
        return pad(input, size, 1, pad, Direction.RIGHT);
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     * <pre>
     *     StringUtils.padRight("1", 3, '0'); //result =  100
     *     StringUtils.padRight("1", 3, "xyz"); //result =  1
     *     StringUtils.padRight("1", 4, "xyz"); //result =  1xyz
     * </pre>
     * @param input the String to pad out, may be null
     * @param size the size to pad to
     * @param pad the String to pad with
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padRight(String input, int size, String pad) {
        requireNonNullOrEmpty(pad);
        return pad(input, size, pad.length(), pad, Direction.RIGHT);
    }
    //endregion

    //region repeat
    /**
     * Create a string with repeated chars n-times by length parameter.
     * @param c character
     * @param length times to repeat the character.
     * @return string with repeated chars n-times by length parameter.
     */
    public static String repeat(char c, int length) {
        return new String(new char[length]).replace('\0', c);
    }
    //endregion

    //region matches
    /**
     * Creates a matcher that will match the given input against this pattern regex
     * and find the next subsequence of the input sequence that matches the pattern.
     *
     * If the match succeeds then more information can be obtained via the start,
     * end, and group methods.
     *
     * @param target target string.
     * @param regex regular expression
     * @param patternFlag Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS} and {@link Pattern#COMMENTS}
     * @return true if, and only if, a subsequence of the input sequence matches this matcher's pattern.
     */
    public static boolean match(String target, String regex, int patternFlag) {
        if(isNonNullAndNonEmpty(target)) {
            Pattern pattern = Pattern.compile(regex, patternFlag);
            Matcher matcher = pattern.matcher(target);
            return matcher.find();
        }
        return false;
    }

    /**
     * Creates a matcher that will match the given input against this pattern regex
     * and find the next subsequence of the input sequence that matches the pattern.
     *
     * If the match succeeds then more information can be obtained via the start,
     * end, and group methods.
     *
     * @param target target string.
     * @param regex regular expression
     * @return true if, and only if, a subsequence of the input sequence matches this matcher's pattern.
     */
    public static boolean match(String target, String regex) {
        return match(target, regex, 0);
    }
    //endregion

    //region regionMatches
    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument {@code other}. The result is {@code true} if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if {@code ignoreCase} is true. The substring of
     * this {@code String} object to be compared begins at index
     * {@code strOffset} and has length {@code len}. The substring of
     * {@code other} to be compared begins at index {@code otherOffset} and
     * has length {@code len}. The result is {@code false} if and only if
     * at least one of the following is true:
     * <ul><li>{@code strOffset} is negative.
     * <li>{@code otherOffset} is negative.
     * <li>{@code strOffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code otherOffset+len} is greater than the length of the other
     * argument.
     * <li>{@code ignoreCase} is {@code false} and there is some non negative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * this.charAt(strOffset+k) != other.charAt(otherOffset+k)
     * </pre></blockquote>
     * <li>{@code ignoreCase} is {@code true} and there is some non negative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(strOffset+k)) !=
     Character.toLowerCase(other.charAt(otherOffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(strOffset+k)) !=
     *         Character.toUpperCase(other.charAt(otherOffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param   ignoreCase   if {@code true}, ignore case when comparing
     *                       characters.
     * @param   strOffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   otherOffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          {@code false} otherwise. Whether the matching is exact
     *          or case insensitive depends on the {@code ignoreCase}
     *          argument.
     */
    @SuppressWarnings("SameParameterValue")
    private static boolean regionMatches(CharSequence str, boolean ignoreCase, int strOffset,
                                         CharSequence other, int otherOffset, int len) {
        int to = strOffset;
        int po = otherOffset;
        // Note: strOffset, otherOffset, or len might be near -1>>>1.
        if ((otherOffset < 0) || (strOffset < 0)
                || (strOffset > (long)str.length() - len)
                || (otherOffset > (long)other.length() - len)) {
            return false;
        }
        while (len-- > 0) {
            char c1 = str.charAt(to++);
            char c2 = other.charAt(po++);
            if (CharSequenceUtils.compareChars(c1, c2, ignoreCase)) {
                continue;
            }
            return false;
        }
        return true;
    }
    //endregion
    
    //region countMatches
    private static int indexOf(String target, String find, int start) {
        return target.indexOf(find, start);
    }

    /**
     * Counts how many times the find string appears in the larger string (text).
     * @param text target
     * @param find find string
     * @return the number of occurrences, 0 if either String is {@code null}
     */
    public static int countMatches(String text, String find) {
        if(isNullOrEmpty(text) || isNullOrEmpty(find)) {
            return 0;
        }

        int count = 0;
        int index = 0;
        while ((index = indexOf(text, find, index)) != -1) {
            count++;
            index += find.length();
        }
        return count;
    }
    //endregion

    //region split by size
    /**
     * Split current string by defined limit size.
     * @param target target string
     * @param size max size for each new string value.
     * @return splitted strings.
     */
    public static String[] splitBySize(String target, int size) {
        String[] arr = new String[0];
        for(int i=0, l = target.length(); i < l; i += size) {
            String e = target.substring(i, Math.min(l, i + size));
            arr = ArrayHelper.add(arr, e);
        }
        return arr;
    }
    //endregion

    //region digitsOnly/digitsLettersOnly
    /**
     * Remove all non digits characters in string.
     * @param target target string
     * @return digits only.
     */
    public static String digitsOnly(String target) {
        return target == null ? "" : target.replaceAll("[^0-9]", "");
    }

    /**
     * Remove all non digits or letters chartacters in string.
     * @param target target string
     * @return digits an letters only.
     */
    public static String digitLettersOnly(String target) {
        return target == null ? "" : target.replaceAll("[^0-9a-zA-Z]", "");
    }
    //endregion

    //region append
    /**
     * Apply append method of StringBuilder if condition is true.
     * @param builder target
     * @param value value to be append
     * @param condition condition
     * @param <T> value type
     * @return true, value was append otherwise false.
     */
    public static <T> boolean appendIf(StringBuilder builder, T value, boolean condition) {
        if(condition) {
            appendValueForJoin(builder, value);
        }
        return condition;
    }

    /**
     * Apply append method of StringBuilder if condition is true.
     * @param builder target
     * @param value value to be append
     * @param condition condition
     * @param <T> value type
     * @return true, value was append otherwise false.
     */
    public static <T> boolean appendIf(StringBuilder builder, T value, Condition<T> condition) {
        return appendIf(builder, value, condition.apply(value));
    }

    /**
     * Apply append method of StringBuilder when condition is true.
     * @param builder target
     * @param function function to procude value to be append.
     * @param condition condition
     * @param value base value to be used in function as input.
     * @param <T> input value type
     * @param <R> output value type
     * @return true, value was append otherwise false.
     */
    public static <T, R> boolean appendIf(StringBuilder builder, Function<T, R> function, Condition<T> condition, T value) {
        boolean c = condition.apply(value);
        if (c) appendValueForJoin(builder, function.apply(value));
        return c;
    }

    /**
     * Apply append method of StringBuilder when condition is true.
     * @param builder target
     * @param function function to procude value to be append.
     * @param condition condition
     * @param value base value to be used in function as input.
     * @param <T> input value type
     * @param <R> output value type
     * @return true, value was append otherwise false.
     */
    public static <T, R> boolean appendIf(StringBuilder builder, Function<T, R> function, T value, boolean condition) {
        if (condition) appendValueForJoin(builder, function.apply(value));
        return condition;
    }

    /**
     * Apply append method of StringBuilder when value is not null.
     * @param builder target
     * @param value value to be append
     * @param <T> value type
     * @return true, value was append otherwise false.
     */
    public static <T> boolean appendIfNonNull(StringBuilder builder, T value) {
        return appendIf(builder, value, Objects::nonNull);
    }

    /**
     * Apply append method of StringBuilder when value is not null.
     * @param function function to procude value to be append.
     * @param builder target
     * @param value value to be append
     * @param <T> value type
     * @param <R> output value type
     * @return true, value was append otherwise false.
     */
    public static <T, R> boolean appendIfNonNull(StringBuilder builder, Function<T, R> function, T value) {
        return appendIf(builder, function, Objects::nonNull, value);
    }

    /**
     * Apply append method of StringBuilder when value is not null and not empty.
     * @param builder target
     * @param value value to be append
     * @return true, value was append otherwise false.
     */
    public static boolean appendIfNonNullNonEmpty(StringBuilder builder, CharSequence value) {
        return appendIf(builder, value, v -> v != null && v.length() != 0);
    }

    /**
     * Apply append method of StringBuilder when value is not null and not empty.
     * @param function function to procude value to be append.
     * @param builder target
     * @param value value to be append
     * @param <R> output value type
     * @return true, value was append otherwise false.
     */
    public static <R> boolean appendIfNonNullNonEmpty(StringBuilder builder, Function<CharSequence, R> function, CharSequence value) {
        return appendIf(builder, function, v -> v != null && v.length() != 0, value);
    }
    //endregion

    //region indexOf
    /**
     * Get the first start index of {@literal searchStr} in {@literal str}
     * @param str target string
     * @param searchStr find value
     * @param startPos start position to find
     * @param ignoreCase ignore string case
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              CharSequence searchStr,
                              int startPos,
                              boolean ignoreCase) {
        if(str == null || searchStr == null) {
            return -1;
        }

        startPos = Math.max(startPos, 0);

        int strLen = str.length();
        int searchLen = searchStr.length();
        int endLimit = strLen - searchLen + 1;

        if (startPos > endLimit) {
            return -1;
        } else if (searchLen == 0) {
            return startPos;
        } else {
            for (int i = startPos; i < endLimit; i++) {
                if (regionMatches(str, ignoreCase, i, searchStr, 0, searchLen)) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (case sensitive)
     * @param str target string
     * @param searchStr find value
     * @param startPos start position to find
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              CharSequence searchStr,
                              int startPos) {
        return indexOf(str, searchStr, startPos, false);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str}
     * @param str target string
     * @param searchStr find value
     * @param ignoreCase ignore string case
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              CharSequence searchStr,
                              boolean ignoreCase) {
        return indexOf(str, searchStr, 0, ignoreCase);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (ignore case)
     * @param str target string
     * @param searchStr find value
     * @param startPos start position to find
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOfIgnoreCase(final CharSequence str,
                                        final CharSequence searchStr,
                                        int startPos) {
        return indexOf(str, searchStr, startPos, true);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (ignore case)
     * @param str target string
     * @param searchStr find value
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOfIgnoreCase(final CharSequence str,
                                        final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }
    //endregion

    //region indexOf char
    /**
     * Get the first start index of {@literal searchStr} in {@literal str}
     * @param str target string
     * @param charCode find value
     * @param startPos start position to find
     * @param ignoreCase ignore string case
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              char charCode,
                              int startPos,
                              boolean ignoreCase) {
        return indexOf(str, CharSequenceUtils.from(charCode), startPos, ignoreCase);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (case sensitive)
     * @param str target string
     * @param charCode find value
     * @param startPos start position to find
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              char charCode,
                              int startPos) {
        return indexOf(str, charCode, startPos, false);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str}
     * @param str target string
     * @param charCode find value
     * @param ignoreCase ignore string case
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOf(CharSequence str,
                              char charCode,
                              boolean ignoreCase) {
        return indexOf(str, charCode, 0, ignoreCase);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (ignore case)
     * @param str target string
     * @param charCode find value
     * @param startPos start position to find
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOfIgnoreCase(final CharSequence str,
                                        final char charCode,
                                        int startPos) {
        return indexOf(str, charCode, startPos, true);
    }

    /**
     * Get the first start index of {@literal searchStr} in {@literal str} (ignore case)
     * @param str target string
     * @param charCode find value
     * @return first start index of {@literal searchStr} in {@literal str}, or -1 when not found.
     */
    public static int indexOfIgnoreCase(final CharSequence str,
                                        final char charCode) {
        return indexOfIgnoreCase(str, charCode, 0);
    }
    //endregion

    //region replace
    /**
     * Fastest Replace {@literal searchString} (if exists) by {@literal replacement} in {@literal text}.
     * @param text target
     * @param searchString search string to be replaced
     * @param replacement replacement string value
     * @param max max times that search string exists in target {@literal text}
     * @param ignoreCase true, ignore string {@literal text} case to find {@literal searchString}, false otherwise.
     * @return string result with new format after replacement or same string if not found.
     */
    public static String replace(String text,
                                 String searchString,
                                 String replacement,
                                 int max,
                                 boolean ignoreCase) {
        if (isNullOrEmpty(text) || isNullOrEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }

        int start = 0;
        int end = indexOf(text, searchString, start, ignoreCase);
        if (end == -1) {
            return text;
        }

        int searchLength = searchString.length();
        int increase = Math.max(replacement.length() - searchLength, 0);
        increase *= max < 0 ? 16 : Math.min(max, 64);
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + searchLength;
            if (--max == 0) {
                break;
            }
            end = indexOf(text, searchString, start, ignoreCase);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    /**
     * Fastest Replace {@literal searchString} (if exists unless one time) by {@literal replacement} in {@literal text}.
     * @param text target
     * @param searchString search string to be replaced
     * @param replacement replacement string value
     * @param ignoreCase true, ignore string {@literal text} case to find {@literal searchString}, false otherwise.
     * @return string result with new format after replacement or same string if not found.
     */
    public static String replace(String text,
                                 String searchString,
                                 String replacement,
                                 boolean ignoreCase) {
        return replace(text, searchString, replacement, 1, ignoreCase);
    }

    /**
     * Fastest Replace {@literal searchString} (if exists unless one time and case sensitive) by {@literal replacement} in {@literal text}.
     * @param text target
     * @param searchString search string to be replaced
     * @param replacement replacement string value
     * @return string result with new format after replacement or same string if not found.
     */
    public static String replace(String text,
                                 String searchString,
                                 String replacement) {
        return replace(text, searchString, replacement, false);
    }

    /**
     * Fastest Replace all {@literal searchString} (if exists) by {@literal replacement} in {@literal text}.
     * @param text target
     * @param searchString search string to be replaced
     * @param replacement replacement string value
     * @param ignoreCase true, ignore string {@literal text} case to find {@literal searchString}, false otherwise.
     * @return string result with new format after replacement or same string if not found.
     */
    public static String replaceAll(String text,
                                    String searchString,
                                    String replacement,
                                    boolean ignoreCase) {
        return replace(text, searchString, replacement, -1, ignoreCase);
    }

    /**
     * Fastest Replace all {@literal searchString} (if exists and case sensitive) by {@literal replacement} in {@literal text}.
     * @param text target
     * @param searchString search string to be replaced
     * @param replacement replacement string value
     * @return string result with new format after replacement or same string if not found.
     */
    public static String replaceAll(String text,
                                    String searchString,
                                    String replacement) {
        return replace(text, searchString, replacement, -1, false);
    }

    //endregion

    //region remove
    /**
     * Remove each {@literal toRemove} apparition in target {@literal text}
     * @param text target text
     * @param ignoreCase true, ignore text case, false otherwise.
     * @param toRemove target values to be removed
     * @return new string without {@literal toRemove} values.
     */
    public static String remove(String text,
                                         boolean ignoreCase,
                                         String... toRemove) {
        if(StringUtils.isNullOrEmpty(text)) {
            return empty();
        }

        for(String search : toRemove) {
            text = replaceAll(text, search, "", ignoreCase);
        }
        return text;
    }

    /**
     * Remove each {@literal toRemove} apparition (case sensitive) in target {@literal text}
     * @param text target text
     * @param toRemove target values to be removed
     * @return new string without {@literal toRemove} values.
     */
    public static String remove(String text,
                                         String... toRemove) {
        return remove(text, false, toRemove);
    }
    //endregion

    //region empty
    /**
     * Empty string
     * @return empty string
     */
    public static String empty() {
        return "";
    }
    //endregon

    //region flat
    /**
     * Flat string array to a single joined string.
     * @param arr target
     * @param offset start index
     * @param count elements count
     * @return joined string.
     */
    public static String flat(String[] arr, int offset, int count) {
        if(count > arr.length) {
            throw new ArrayIndexOutOfBoundsException("Invalid count!");
        }
        offset = Math.max(offset, 0);
        StringBuilder sb = new StringBuilder(arr.length * 512);
        for(int i=offset; i < count; i++) {
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Flat string array to a single joined string.
     * @param arr target
     * @return joined string.
     */
    public static String flat(String[] arr) {
        return flat(arr, 0, arr.length);
    }

    /**
     * Flat string array to a single joined string.
     * @param args target
     * @return joined string.
     */
    public static String[] flatArray(String[]... args) {
        int total = 0;
        for(String[] arr : args) total += arr.length;

        if(total == 0) {
            return new String[0];
        } else {
            int i=0;
            String[] aux = new String[total];
            for(String[] arr : args) {
                for (String s : arr) {
                    aux[i++] = s;
                }
            }
            return aux;
        }
    }

    /**
     * Flat string array to a single joined string.
     * @param args target
     * @return joined string.
     */
    public static String flat(String[]... args) {
        if(args.length == 0) {
            return empty();
        } else if(args.length == 1) {
            return flat(args[0]);
        } else {
            return flat(flatArray(args));
        }
    }
    //endregion

    //#region string case
    /**
     * Converts all of the characters in this String to upper case
     * using the rules of the given locale.
     * @param target target string
     * @param locale target locale
     * @return new string in upper case
     */
    public static String toUpperCase(String target, Locale locale) {
        return isNullOrEmpty(target) ? empty() : target.toUpperCase(locale);
    }

    /**
     * Converts all of the characters in this String to upper case
     * using the rules of the given default Locale by {@link LocaleHelper#getDefaultLocale()}.
     * @param target target string
     * @return new string in upper case
     */
    public static String toUpperCase(String target) {
        return toUpperCase(target, LocaleHelper.getDefaultLocale());
    }

    /**
     * Converts all of the characters in this String to lower case
     * using the rules of the given locale.
     * @param target target string
     * @param locale target locale
     * @return new string in lower case
     */
    public static String toLowerCase(String target, Locale locale) {
        return isNullOrEmpty(target) ? empty() : target.toLowerCase(locale);
    }

    /**
     * Converts all of the characters in this String to lower case
     * using the rules of the given default Locale by {@link LocaleHelper#getDefaultLocale()}.
     * @param target target string
     * @return new string in lower case
     */
    public static String toLowerCase(String target) {
        return toLowerCase(target, LocaleHelper.getDefaultLocale());
    }

    /**
     * Converts all of the characters in this String to title case
     * using the rules of the given locale.
     * @param target target string
     * @return new string in title case
     */
    public static String toTitleCase(String target) {
        if (isNullOrEmpty(target)) {
            return empty();
        }

        int len = target.length();
        int idx = 0;
        if (!Character.isLowerCase(target.charAt(idx))) {
            for (idx = 1; idx < len; idx++) {
                if (Character.isUpperCase(target.charAt(idx))) {
                    break;
                }
            }
        }
        if (idx == len) {
            return target;
        }

        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            char c = target.charAt(i);
            if (i == 0 && idx == 0) {
                buf[i] = Character.toUpperCase(c);
            } else if (i < idx) {
                buf[i] = c;
            } else {
                buf[i] = Character.toLowerCase(c);
            }
        }
        return new String(buf);
    }
    //#endregion

    //#region distinct
    /**
     * Remove duplicity character apparition in text.
     * @param target target text
     * @param ignoreCase true ignore distinct characters by case, false otherwise.
     * @return text without duplicity char values.
     */
    public static String distinct(String target, boolean ignoreCase) {
        return CharSequenceUtils.distinct(target, ignoreCase).toString();
    }
    //#endregion

    //#region compareParity
    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     * @param str0 checking parity
     * @param str1 checker parity
     * @param ignoreCase true, ignore characters case, false otherwise
     * @param distinct true, remove duplicity characters apparition before comparing.
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(String str0,
                                      String str1,
                                      boolean ignoreCase,
                                      boolean distinct) {
        return CharSequenceUtils.compareParity(str0, str1, ignoreCase, distinct);
    }

    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     * @param str0 checking parity
     * @param str1 checker parity
     * @param ignoreCase true, ignore characters case, false otherwise
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(String str0,
                                      String str1,
                                      boolean ignoreCase) {
        return compareParity(str0, str1, ignoreCase, false);
    }

    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     * @param str0 checking parity
     * @param str1 checker parity
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(String str0,
                                      String str1) {
        return compareParity(str0, str1, false, false);
    }
    //#endregion

}
