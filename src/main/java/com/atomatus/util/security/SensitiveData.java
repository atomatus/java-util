package com.atomatus.util.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Sensitive Data.<br>
 * Implements a bytes matrix manager encrypted using Cipher and DESede algorithm.<br>
 * Each sensitive bytes class instance will generate an unique private key.
 * @author Carlos Matos {@literal @chcmatos}
 * @see SensitiveChars
 */
public final class SensitiveData extends SensitiveChars {

    //region constructors
    /**
     * Constructs inputing initial value.
     * @param chars initial value.
     * @param charset charset.
     */
    private SensitiveData(char[] chars, Charset charset) {
        super(chars, charset);
    }

    /**
     * Constructs inputing initial value.
     * @param chars initial value.
     */
    private SensitiveData(char[] chars) {
        this(chars, Charset.defaultCharset());
    }

    /**
     * Constructs inputing initial value.
     * @param bytes initial value.
     */
    private SensitiveData(byte[] bytes) {
        super(bytes);
    }

    /**
     * Constructs empty.
     */
    public SensitiveData() { }
    //endregion

    //region append
    @Override
    public SensitiveData append(byte[] args) {
        super.append(args);
        return this;
    }

    @Override
    public SensitiveData append(byte[] args, int start, int end) {
        super.append(args, start, end);
        return this;
    }

    /**
     * Appends the char array to secure context.
     * @param args content to be stored in secure context.
     * @return current instance.
     */
    public SensitiveData append(char[] args) {
        super.append(args);
        return this;
    }

    /**
     * Appends the char array to secure context.
     * @param args target content to be stored in secure context.
     * @param start start index to read
     * @param end array max length to read.
     * @return current instance.
     */
    public SensitiveData append(char[] args, int start, int end) {
        super.append(args, start, end);
        return this;
    }

    /**
     * Appends the byte to secure context.
     * @param b target byte.
     * @return current instance.
     */
    public SensitiveData append(char b) {
        super.append(b);
        return this;
    }

    /**
     * Appends the string to secure context.
     * @param str content to be stored in secure context.
     * @return current instance.
     */
    public SensitiveData append(String str) {
        super.append(str);
        return this;
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(short i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(int i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(boolean i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(long i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(float i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(double i) {
        return this.append(String.valueOf(i));
    }

    /**
     * Appends the value to secure context.
     * @param i target.
     * @return current instance.
     */
    public SensitiveData append(Object i) {
        return this.append(i.toString());
    }
    //endregion

    //region behavior
    /**
     * Enabled to clear input data
     * after "append" requests.
     * @return current instance.
     */
    @Override
    public SensitiveData useClearAfterAppend() {
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

    //region store
    @Override
    public SensitiveData stored(File file) throws IOException {
        super.stored(file);
        return this;
    }

    @Override
    public SensitiveData stored() throws IOException {
        super.stored();
        return this;
    }
    //endregion

    //region SensitiveData.of
    /**
     * Create an instance of sensitive byte from byte array.
     * @param bytes target byte array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveData of(byte[] bytes) {
        return new SensitiveData(bytes);
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @param charset charset for target chars.
     * @return current instance.
     */
    public static SensitiveData of(char[] chars, Charset charset) {
        return new SensitiveData(chars, charset);
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveData of(char[] chars) {
        return new SensitiveData(chars);
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveData of(String str) {
        return of(Objects.requireNonNull(str)
                .getBytes());
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @param charset charset for target string.
     * @return current instance.
     */
    public static SensitiveData of(String str, Charset charset) {
        return of(Objects.requireNonNull(str)
                .getBytes(Objects.requireNonNull(charset)));
    }
    //endregion
}
