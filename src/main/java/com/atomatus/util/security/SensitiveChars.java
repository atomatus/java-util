package com.atomatus.util.security;

import com.atomatus.util.ArrayHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Objects;

/**
 * Sensitive Chars.<br>
 * Implements a bytes matrix manager encrypted using Cipher and DESede algorithm.<br>
 * Each sensitive bytes class instance will generate an unique private key.
 * @author Carlos Matos {@literal @chcmatos}
 * @see SensitiveBytes
 * @see CharSequence
 */
public class SensitiveChars extends SensitiveBytes implements CharSequence,
        Comparator<CharSequence>, Comparable<CharSequence> {

    //region constructors
    /**
     * Constructs inputing initial value.
     * @param chars initial value.
     * @param charset charset.
     */
    protected SensitiveChars(char[] chars, Charset charset) {
        super(fromChars(chars, charset));
    }

    /**
     * Constructs inputing initial value.
     * @param chars initial value.
     */
    protected SensitiveChars(char[] chars) {
        this(chars, Charset.defaultCharset());
    }

    /**
     * Constructs inputing initial value.
     * @param bytes initial value.
     */
    protected SensitiveChars(byte[] bytes) {
        super(bytes);
    }

    /**
     * Constructs empty.
     */
    public SensitiveChars() { }
    //endregion

    //region append
    @Override
    public SensitiveChars append(byte[] args) {
        super.append(args);
        return this;
    }

    @Override
    public SensitiveChars append(byte[] args, int start, int end) {
        super.append(args, start, end);
        return this;
    }

    /**
     * Appends the char array to secure context.
     * @param args content to be stored in secure context.
     * @return current instance.
     */
    public SensitiveChars append(char[] args) {
        byte[] arr = ArrayHelper.reduceI(args,
                (acc, curr, i) -> {
                    acc[i] = (byte) curr.charValue();
                    return acc;
                }, new byte[args.length]);
        this.put(arr, 0, args.length);
        return this;
    }

    /**
     * Appends the char array to secure context.
     * @param args target content to be stored in secure context.
     * @param start start index to read
     * @param end array max length to read.
     * @return current instance.
     */
    public SensitiveChars append(char[] args, int start, int end) {
        byte[] arr = ArrayHelper.reduceI(args,
                (acc, curr, i) -> {
                    if(i >= start && i < end)
                        acc[i] = (byte) curr.charValue();
                    return acc;
                }, new byte[args.length]);
        this.put(arr, start, end);
        return this;
    }

    /**
     * Appends the byte to secure context.
     * @param b target byte.
     * @return current instance.
     */
    public SensitiveChars append(char b) {
        this.put((byte) b);
        return this;
    }

    /**
     * Appends the string to secure context.
     * @param str content to be stored in secure context.
     * @return current instance.
     */
    public SensitiveChars append(String str) {
        return this.append(str.toCharArray());
    }
    //endregion

    //region behavior
    /**
     * Enabled to clear input data
     * after "append" requests.
     * @return current instance.
     */
    @Override
    public SensitiveChars useClearAfterAppend() {
        super.useClearAfterAppend();
        return this;
    }
    //endregion

    //region charSequence
    @Override
    public char charAt(int index) {
        return (char) peek(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new String(read(start, end));
    }

    @Override
    public String toString() {
        return new String(readAll());
    }
    //endregion

    //region Comparable and Comparator
    @Override
    public int compareTo(CharSequence other) {
        return compare(this, other);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SensitiveChars && (this == obj || (
                compareTo((CharSequence) obj) == 0));
    }

    @Override
    public int compare(CharSequence cs1, CharSequence cs2) {
        if (Objects.requireNonNull(cs1) == Objects.requireNonNull(cs2)) {
            return 0;
        }

        for (int i = 0, len = Math.min(cs1.length(), cs2.length()); i < len; i++) {
            char a = cs1.charAt(i);
            char b = cs2.charAt(i);
            if (a != b) {
                return a - b;
            }
        }

        return cs1.length() - cs2.length();
    }
    //endregion

    //region store
    @Override
    public SensitiveChars stored(File file) throws IOException {
        super.stored(file);
        return this;
    }

    @Override
    public SensitiveChars stored() throws IOException {
        super.stored();
        return this;
    }
    //endregion

    //region SensitiveChars.of
    /**
     * Create an instance of sensitive byte from byte array.
     * @param bytes target byte array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveChars of(byte[] bytes) {
        return new SensitiveChars(bytes);
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @param charset charset for target chars.
     * @return current instance.
     */
    public static SensitiveChars of(char[] chars, Charset charset) {
        return new SensitiveChars(chars, charset);
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveChars of(char[] chars) {
        return new SensitiveChars(chars);
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveChars of(String str) {
        return of(Objects.requireNonNull(str)
                .getBytes());
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @param charset charset for target string.
     * @return current instance.
     */
    public static SensitiveChars of(String str, Charset charset) {
        return of(Objects.requireNonNull(str)
                .getBytes(Objects.requireNonNull(charset)));
    }
    //endregion

    //region SensitiveChars.clearSensitiveData
    /**
     * Clear sensitive data.
     * @param arr target array.
     * @param start start index.
     * @param length max length to clear.
     */
    public static void clearSensitiveData(char[] arr, int start, int length) {
        if(arr != null) {
            char zero = '\0';
            for (int i = start; i < length; i++) {
                arr[i] = zero;
            }
        }
    }

    /**
     * Clear sensitive data.
     * @param arr target array.
     */
    public static void clearSensitiveData(char[] arr) {
        if(arr != null) {
            clearSensitiveData(arr, 0, arr.length);
        }
    }
    //endregion
}
