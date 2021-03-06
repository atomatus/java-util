package com.atomatus.util;

/**
 * String utils to help to parse, convert,
 * join and captalize strings.
 */
@SuppressWarnings("UnusedReturnValue")
public final class StringUtils {

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
}
