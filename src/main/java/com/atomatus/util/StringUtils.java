package com.atomatus.util;

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
    public static String requireNonNull(String str, String message) {
        if(str == null) throw new NullPointerException(message);
        return str;
    }

    public static String requireNonNull(String str) {
        return requireNonNull(str, "Input String is null!");
    }

    public static String requireNonNullOrEmpty(String str, String message) {
        if(isNullOrEmpty(str)) throw new NullPointerException(message);
        return str;
    }

    public static String requireNonNullOrEmpty(String str) {
        return requireNonNullOrEmpty(str, "Input String is null or empty!");
    }

    public static String requireNonNullOrWhitespace(String str, String message) {
        if(isNullOrWhitespace(str)) throw new NullPointerException(message);
        return str;
    }

    public static String requireNonNullOrWhitespace(String str) {
        return requireNonNullOrWhitespace(str, "Input String is null or does not contains a valid value!");
    }
    //endregion

    //region isNullOrEmpty/isNullOrWhitespace
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.trim().length() == 0;
    }
    //endregion

    //region join
    @SafeVarargs
    public static <T> String join(String prefix, String suffix, String sep, T... args) {
        requireNonNull(sep, "Separator can not be null!");
        StringBuilder sb = new StringBuilder();

        if(!isNullOrEmpty(prefix)) {
            sb.append(prefix);
        }

        for (int i = 0, c = args.length; i < c; i++) {
            if(i > 0) {
                sb.append(sep);
            }
            sb.append(args[i]);
        }

        if(!isNullOrEmpty(suffix)) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    @SafeVarargs
    public static <T> String join(String sep, T... args) {
        return join(null, null, sep, args);
    }
    //endregion

    //region captalize

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

}
