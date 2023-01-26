package com.atomatus.util;

import java.util.Locale;

/**
 * <p>
 * CharSequence Utils to help developer to manage and solve CharSequence operations fastly.
 * </p>
 *
 * <i>Created by chcmatos (cmatos) on 18, october, 2021</i>
 *
 * @author Carlos Matos @author Carlos Matos  {@literal chcmatos}
 */
public final class CharSequenceUtils {

    private CharSequenceUtils() {
    }

    public static boolean isNullOrEmpty(CharSequence seq) {
        return seq == null || seq.length() == 0;
    }

    public static boolean isNullOrWhitespace(CharSequence seq) {
        if (seq != null) {
            for (int i = 0, l = seq.length(); i < l; i++) {
                if (seq.charAt(i) != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean equals(CharSequence seq0, CharSequence seq1, boolean ignoreCase) {
        if (seq0 == seq1) {
            return true;
        }

        int l;
        if (seq0 != null && seq1 != null && (l = seq0.length()) == seq1.length()) {
            int i = 0;
            while (i < l) {
                if (!compareChars(seq0.charAt(i), seq1.charAt(i++), ignoreCase)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public static boolean equals(CharSequence seq0, CharSequence seq1) {
        return equals(seq0, seq1, false);
    }

    public static boolean equalsIgnoreCase(CharSequence seq0, CharSequence seq1) {
        return equals(seq0, seq1, true);
    }

    public static boolean containsUpperCase(CharSequence seq) {
        if (seq != null) {
            for (int i = 0, l = seq.length(); i < l; i++) {
                if (Character.isUpperCase(seq.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsLowerCase(CharSequence seq) {
        if (seq != null) {
            for (int i = 0, l = seq.length(); i < l; i++) {
                if (Character.isLowerCase(seq.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isUpperCase(CharSequence seq) {
        if (seq != null) {
            for (int i = 0, l = seq.length(); i < l; i++) {
                if (!Character.isUpperCase(seq.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isLowerCase(CharSequence seq) {
        if (seq != null) {
            for (int i = 0, l = seq.length(); i < l; i++) {
                if (!Character.isLowerCase(seq.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isDigits(CharSequence seq) {
        if (!isNullOrEmpty(seq)) {
            for(int i=0, l=seq.length(); i < l; i++) {
                if (!Character.isDigit(seq.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isDigitsOrWhitespace(CharSequence seq) {
        if (!isNullOrEmpty(seq)) {
            for(int i=0, l=seq.length(); i < l; i++) {
                char c = seq.charAt(i);
                if (!Character.isDigit(seq.charAt(i)) && !Character.isWhitespace(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Tests if this char sequence starts with the specified prefix.
     *
     * @param   seq char sequence compared
     * @param   prefix   the prefix.
     * @return  {@code true} if the character sequence represented by the
     *          argument is a prefix of the character sequence represented by
     *          this string; {@code false} otherwise.
     *          Note also that {@code true} will be returned if the
     *          argument is an empty string or is equal to this
     *          {@code String} object as determined by the
     *          {@link #equals(Object)} method.
     * @since   1. 0
     */
    public static boolean startsWith(CharSequence seq, CharSequence prefix) {
        return startsWith(seq, prefix, 0);
    }

    /**
     * Tests if the substring of this string beginning at the
     * specified index starts with the specified prefix.
     *
     * @param   prefix    the prefix.
     * @param   offset   where to begin looking in this string.
     * @return  {@code true} if the character sequence represented by the
     *          argument is a prefix of the substring of this object starting
     *          at index {@code toffset}; {@code false} otherwise.
     *          The result is {@code false} if {@code toffset} is
     *          negative or greater than the length of this
     *          {@code String} object; otherwise the result is the same
     *          as the result of the expression
     *          <pre>
     *          this.substring(toffset).startsWith(prefix)
     *          </pre>
     */
    public static boolean startsWith(CharSequence seq, CharSequence prefix, int offset) {
        int to = offset;
        int po = 0;
        int pc = prefix.length();
        // Note: offset might be near -1>>>1.
        if ((offset < 0) || (offset > seq.length() - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (seq.charAt(to++) != prefix.charAt(po++)) {
                return false;
            }
        }
        return true;
    }

    public static int getLengthNullSafe(CharSequence seq) {
        return seq == null ? 0 : seq.length();
    }

    public static CharSequence requireNonNullOrEmpty(CharSequence seq) {
        if (isNullOrEmpty(seq)) {
            throw new NullPointerException("CharSequence is null or empty!");
        }
        return seq;
    }

    public static CharSequence requireNonNullOrWhitespace(CharSequence seq) {
        if (isNullOrWhitespace(seq)) {
            throw new NullPointerException("CharSequence is null, empty or whitespace only!");
        }
        return seq;
    }

    public static String toStringNullSafe(CharSequence seq) {
        return seq == null ? empty().toString() : seq.toString();
    }

    public static CharSequence empty() {
        return CharSeqForArray.empty();
    }

    public static CharSequence toUpperCase(CharSequence seq) {
        return toUpperCase(seq, LocaleHelper.getDefaultLocale());
    }

    public static CharSequence toUpperCase(CharSequence seq, Locale locale) {
        return isNullOrEmpty(seq) ? empty() : seq.toString().toUpperCase(locale);
    }

    public static CharSequence toLowerCase(CharSequence seq) {
        return toLowerCase(seq, LocaleHelper.getDefaultLocale());
    }

    public static CharSequence toLowerCase(CharSequence seq, Locale locale) {
        return isNullOrEmpty(seq) ? empty() : seq.toString().toLowerCase(locale);
    }

    public static CharSequence join(CharSequence separator, Object... args) {
        StringBuilder sb = new StringBuilder(args.length);
        if (args.length > 0) {
            int i = 0;
            sb.append(args[i]);
            while (++i < args.length) {
                sb.append(separator).append(args[i]);
            }
        }
        return sb;
    }

    /**
     * Returns a CharSequence whose value is this CharSequence, with any leading and trailing whitespace removed.
     * <p>
     * Note: This method does not change the original CharSequence.
     * </p>
     *
     * @param seq   target char sequence
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     * @return A CharSequence value, which is a copy of the original, without leading and trailing whitespace
     */
    public static CharSequence trim(CharSequence seq, int start, int end) {
        return seq == null ? empty() : trim(seq.subSequence(start, end));
    }

    /**
     * Returns a CharSequence whose value is this CharSequence, with any leading and trailing whitespace removed.
     * <p>
     * Note: This method does not change the original CharSequence.
     * </p>
     *
     * @param seq target char sequence
     * @return A CharSequence value, which is a copy of the original, without leading and trailing whitespace
     */
    public static CharSequence trim(CharSequence seq) {
        if (seq == null) {
            return empty();
        }

        int len = seq.length();
        int st = 0;
        char c = '\u0020';

        while ((st < len) && (seq.charAt(st) <= c)) st++;
        while ((st < len) && (seq.charAt(len - 1) <= c)) len--;
        return ((st > 0) || (len < seq.length())) ? seq.subSequence(st, len) : seq;
    }

    public static CharSequence ellipsize(CharSequence seq) {
        if (seq == null) {
            return empty();
        } else {
            return new CharSequenceEllipsize(seq);
        }
    }

    public static CharSequence ellipsize(CharSequence seq, int start, int end) {
        if (seq == null) {
            return empty();
        } else {
            return new CharSequenceEllipsize(seq, start, end);
        }
    }

    public static CharSequence copy(CharSequence seq) {
        return seq == null ? empty() : seq.subSequence(0, seq.length());
    }

    public static char[] toCharArray(CharSequence seq) {
        if (seq == null) {
            return new char[0];
        } else if (seq instanceof String) {
            return ((String) seq).toCharArray();
        } else {
            char[] arr = new char[seq.length()];
            for (int i=0, l = arr.length; i < l; i++) {
                arr[i] = seq.charAt(i);
            }
            return arr;
        }
    }

    public static CharSequence from(char... args) {
        return new CharSeqForArray(args, args.length);
    }

    //#region distinct

    /**
     * Compare chars
     *
     * @param c0         char value
     * @param c1         char value
     * @param ignoreCase true, ignore char case, false otherwise
     * @return true, chars are equals, false otherwise
     */
    static boolean compareChars(char c0, char c1, boolean ignoreCase) {
        return c0 == c1 || (ignoreCase &&
                (Character.toLowerCase(c0) == Character.toLowerCase(c1) ||
                        Character.toUpperCase(c0) == Character.toUpperCase(c1)));
    }

    /**
     * Remove duplicity character apparition in text.
     *
     * @param target     target text
     * @param ignoreCase true ignore distinct characters by case, false otherwise.
     * @return text without duplicity char values.
     */
    public static CharSequence distinct(CharSequence target, boolean ignoreCase) {

        int len;
        if (target == null || (len = target.length()) == 0) {
            return CharSeqForArray.empty();
        }

        char[] arr = new char[len];
        int offset = 0;

        out:
        for (int i = 0; i < len; i++) {
            char aux = target.charAt(i);
            for (int j = 0; j < offset; j++) {
                char c = arr[j];
                if (compareChars(c, aux, ignoreCase)) {
                    continue out;
                }
            }
            arr[offset++] = aux;
        }

        return new CharSeqForArray(arr, offset);
    }
    //#endregion

    //#region compareParity

    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     *
     * @param str0       checking parity
     * @param str1       checker parity
     * @param ignoreCase true, ignore characters case, false otherwise
     * @param distinct   true, remove duplicity characters apparition before comparing.
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(CharSequence str0,
                                      CharSequence str1,
                                      boolean ignoreCase,
                                      boolean distinct) {
        if (str0 == str1) {
            return 1f;
        } else if (str0 != null ^ str1 != null) {
            return 0f;
        } else {
            if (distinct) {
                str0 = distinct(str0, ignoreCase);
                str1 = distinct(str1, ignoreCase);
            }

            int count = 0;
            out:
            for (int i = 0, il = str0.length(); i < il; i++) {
                char c0 = str0.charAt(i);
                for (int j = count, jl = str1.length(); j < jl; j++) {
                    char c1 = str1.charAt(j);
                    if (compareChars(c0, c1, ignoreCase)) {
                        count++;
                        continue out;
                    }
                }
            }

            return count == 0 ? 0f : (float) count / Math.max(str0.length(), str1.length());
        }
    }

    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     *
     * @param str0       checking parity
     * @param str1       checker parity
     * @param ignoreCase true, ignore characters case, false otherwise
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(CharSequence str0,
                                      CharSequence str1,
                                      boolean ignoreCase) {
        return compareParity(str0, str1, ignoreCase, false);
    }

    /**
     * Compare how much of str0 is including on str1, returning a percentage result value between 0-1,
     * where 1f is (100%) one hundred percent.
     *
     * @param str0 checking parity
     * @param str1 checker parity
     * @return strings parity result in percentage, result between 0 and 1, where 1 is (100%) one hundred percent.
     */
    public static float compareParity(CharSequence str0,
                                      CharSequence str1) {
        return compareParity(str0, str1, false, false);
    }
    //#endregion

    /**
     * Internal CharSequence implementation for char array with offset.
     */
    private static class CharSeqForArray implements CharSequence {

        private final char[] arr;
        private final int length;

        static CharSeqForArray empty() {
            return new CharSeqForArray(new char[0], 0);
        }

        CharSeqForArray(char[] arr, int length) {
            this.arr = arr;
            this.length = length;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public char charAt(int index) {
            if (index < 0 || index >= length) {
                throw new IndexOutOfBoundsException();
            }
            return arr[index];
        }

            @Override
        public CharSequence subSequence(int start, int end) {
            return this.toString().subSequence(start, end);
        }

            @Override
        public String toString() {
            return new String(arr, 0, length);
        }
    }

    /**
     * <p>
     * Internal CharSequence to apply ellipsize on target input char sequence.
     * </p>
     */
    private static class CharSequenceEllipsize implements CharSequence {

        static final char ELLIPSIS = '\u2026';//...

            final CharSequence original;
        final int originalLen;
        final int len;

        public CharSequenceEllipsize(CharSequence original, int start, int end) {
            this.original = original.subSequence(start, end);
            this.originalLen = (end - start);
            this.len = originalLen + 1;
        }

        public CharSequenceEllipsize(CharSequence original) {
            this(original, 0, original.length());
        }

        @Override
        public int length() {
            return len;
        }

        @Override
        public char charAt(int index) {
            return index == originalLen ? ELLIPSIS : original.charAt(index);
        }

            @Override
        public CharSequence subSequence(int start, int end) {
            return end == len ? new CharSequenceEllipsize(original.subSequence(start, originalLen)) : original.subSequence(start, end);
        }

            @Override
        public String toString() {
            return original.toString() + ELLIPSIS;
        }
    }

}
