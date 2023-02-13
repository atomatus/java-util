package com.atomatus.util;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <p>
 * General IO stream manipulation utilities.
 * </p>
 * <i>Created by chcmatos on 27, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public class IOUtils {

    /**
     * Stop byte function.
     */
    @FunctionalInterface
    public interface StopByteFunction {
        /**
         * Byte to stop input stream reading.
         * @return stop byte
         */
        byte get();
    }

    @FunctionalInterface
    private interface InputReadBufferFunction<T, ARR> {
        int read(T input, ARR arr, int offset, int len) throws IOException;
    }

    @FunctionalInterface
    private interface InputReadByteFunction<T> {
        int read(T input) throws IOException;
    }

    @FunctionalInterface
    private interface ArrayInstanceFunction<ARR> {
        ARR get(int size);
    }

    private static final int LARGE_BUFFER_SIZE = 1024 << 8;
    private static final int DEFAULT_BUFFER_SIZE = 1024 << 3;

    /**
     * Represents the end-of-file (or stream).
     */
    public static int EOF = -1;

    private IOUtils() { }

    //region toByteArray
    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]} until
     * {@link #EOF} or specified stop byte in {@code callback}.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * {@link BufferedInputStream}.
     * </p>
     *
     * @param input the {@link InputStream} to read.
     * @param callback callback function to notify when to stop stream reading.
     * @return the requested byte array.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws IOException if an I/O error occurs or reading more than {@link Integer#MAX_VALUE} occurs.
     */
    public static byte[] toByteArrayEOF(InputStream input, StopByteFunction callback) throws IOException {
        Objects.requireNonNull(input, "InpustStream is null!");
        byte[] buffer = new byte[LARGE_BUFFER_SIZE];
        int offset = 0;

        byte b;
        while((b = (byte) input.read()) != EOF && b != callback.get()) {
            buffer[offset++] = b;
            if(buffer.length == offset) {
                buffer = ArrayHelper.resize(buffer, LARGE_BUFFER_SIZE + buffer.length);
            }
        }

        if(offset < buffer.length) {
            buffer = ArrayHelper.resize(buffer, offset);
        }

        return buffer;
    }

    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]} until {@link #EOF} or
     * stop byte {@link AsciiTable#EOT}.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * {@link BufferedInputStream}.
     * </p>
     *
     * @param input the {@link InputStream} to read.
     * @return the requested byte array.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws IOException if an I/O error occurs or reading more than {@link Integer#MAX_VALUE} occurs.
     */
    public static byte[] toByteArrayEOT(InputStream input) throws IOException {
        return toByteArrayEOF(input, AsciiTable.EOT::code);
    }

    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]} until {@link #EOF} or
     * stop byte {@link AsciiTable#ETX}.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * {@link BufferedInputStream}.
     * </p>
     *
     * @param input the {@link InputStream} to read.
     * @return the requested byte array.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws IOException if an I/O error occurs or reading more than {@link Integer#MAX_VALUE} occurs.
     */
    public static byte[] toByteArrayEXT(InputStream input) throws IOException {
        return toByteArrayEOF(input, AsciiTable.ETX::code);
    }

    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]}.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * {@link BufferedInputStream}.
     * </p>
     *
     * @param input the {@link InputStream} to read.
     * @return the requested byte array.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws IOException if an I/O error occurs or reading more than {@link Integer#MAX_VALUE} occurs.
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        Objects.requireNonNull(input, "InpustStream is null!");
        byte[] buffer = new byte[LARGE_BUFFER_SIZE];
        int read, offset = 0;

        while((read = input.read(buffer, offset, buffer.length - offset)) != EOF) {
            offset += read;
            if(offset == Integer.MAX_VALUE) {
                throw new IOException("Size cannot be greater than Integer max value: " + offset);
            } else if(buffer.length == offset) {
                buffer = ArrayHelper.resize(buffer, LARGE_BUFFER_SIZE + buffer.length);
            }
        }

        if(offset < buffer.length) {
            buffer = ArrayHelper.resize(buffer, offset);
        }

        return buffer;
    }

    /**
     * Gets the contents of an {@link InputStream} as a {@code byte[]}. Use this method instead of
     * {@link #toByteArray(InputStream)} when {@link InputStream} size is known.
     *
     * @param input the {@link InputStream} to read.
     * @param size the size of {@link InputStream} to read, where 0 &lt; {@code size} &lt;= length of input stream.
     * @return byte [] of length {@code size}.
     * @throws NullPointerException if the InputStream is {@code null}.
     * @throws IOException if an I/O error occurs or {@link InputStream} length is smaller than parameter {@code size}.
     * @throws IllegalArgumentException if {@code size} is less than zero.
     * @since 2.1
     */
    public static byte[] toByteArray(InputStream input, int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        } else if (size == 0) {
            return new byte[0];
        } else {
            Objects.requireNonNull(input, "InpustStream is null!");
            byte[] data = new byte[size];
            int offset = 0;
            int read;
            while (offset < size && (read = input.read(data, offset, size - offset)) != EOF) {
                offset += read;
            }

            if (offset != size) {
                throw new IOException("Unexpected read size, current: " + offset + ", expected: " + size);
            }
            return data;
        }
    }

    /**
     * Gets contents of an {@link InputStream} as a {@code byte[]}.
     * Use this method instead of {@link #toByteArray(InputStream)}
     * when {@link InputStream} size is known.
     * <b>NOTE:</b> the method checks that the length can safely be cast to an int without truncation
     * before using {@link IOUtils#toByteArray(InputStream, int)} to read into the byte array.
     * (Arrays can have no more than Integer.MAX_VALUE entries anyway)
     *
     * @param input the {@link InputStream} to read from
     * @param size the size of {@link InputStream} to read, where 0 &lt; {@code size} &lt;= min(Integer.MAX_VALUE, length of input stream).
     * @return byte [] the requested byte array, of length {@code size}
     * @throws IOException              if an I/O error occurs or {@link InputStream} length is less than {@code size}
     * @throws IllegalArgumentException if size is less than zero or size is greater than Integer.MAX_VALUE
     * @see IOUtils#toByteArray(InputStream, int)
     */
    public static byte[] toByteArray(InputStream input, long size) throws IOException {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
        }
        return toByteArray(input, (int) size);
    }
    //endregion

    //region contentEquals
    private static <I, ARR> boolean contentEquals(I input1, I input2,
                                             InputReadByteFunction<I> readByteFunction,
                                             InputReadBufferFunction<I, ARR> readBufferFunction,
                                                  ArrayInstanceFunction<ARR> newArrFunction) throws IOException {
        if (input1 == input2) {
            return true;
        } else if (input1 == null || input2 == null) {
            return false;
        }

        ARR array1 = newArrFunction.get(DEFAULT_BUFFER_SIZE);
        ARR array2 = newArrFunction.get(DEFAULT_BUFFER_SIZE);

        int pos1, pos2;
        int count1, count2;
        while (true) {
            pos1 = 0;
            pos2 = 0;
            for (int index = 0; index < DEFAULT_BUFFER_SIZE; index++) {
                //noinspection DuplicatedCode
                if (pos1 == index) {
                    do {
                        count1 = readBufferFunction.read(input1, array1, pos1, DEFAULT_BUFFER_SIZE - pos1);
                    } while (count1 == 0);
                    if (count1 == EOF) {
                        return pos2 == index && readByteFunction.read(input2) == EOF;
                    }
                    pos1 += count1;
                }
                //noinspection DuplicatedCode
                if (pos2 == index) {
                    do {
                        count2 = readBufferFunction.read(input2, array2, pos2, DEFAULT_BUFFER_SIZE - pos2);
                    } while (count2 == 0);
                    if (count2 == EOF) {
                        return pos1 == index && readByteFunction.read(input1) == EOF;
                    }
                    pos2 += count2;
                }

                if (!Objects.equals(Array.get(array1, index), Array.get(array2, index))) {
                    return false;
                }
            }
        }
    }
    
    /**
     * Compares the contents of two Streams to determine if they are equal or
     * not.
     * <p>
     * This method buffers the input internally using
     * {@link BufferedInputStream} if they are not already buffered.
     * </p>
     *
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the content of the streams are equal or they both don't
     * exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws IOException          if an I/O error occurs
     */
    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        return contentEquals(input1, input2, InputStream::read, InputStream::read, byte[]::new);
    }

    /**
     * Compares the contents of two Readers to determine if they are equal or not.
     * <p>
     * This method buffers the input internally using {@link BufferedReader} if they are not already buffered.
     * </p>
     *
     * @param input1 the first reader
     * @param input2 the second reader
     * @return true if the content of the readers are equal or they both don't exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws IOException if an I/O error occurs
     */
    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        return contentEquals(input1, input2, Reader::read, Reader::read, char[]::new);
    }

    /**
     * Compares the contents of two Iterator to determine if they are equal or not.
     * @param iterator1 the first Iterator
     * @param iterator2 the second Iterator
     * @return true if the content of the readers are equal or they both don't exist, false otherwise
     * @throws NullPointerException if either input is null
     */
    public static boolean contentEquals(Iterator<?> iterator1, Iterator<?> iterator2) {
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            } else if (!Objects.equals(iterator1.next(), iterator2.next())) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }

    /**
     * Compares the contents of two Stream to determine if they are equal or not.
     * @param stream1 the first Stream
     * @param stream2 the second Stream
     * @return true if the content of the readers are equal or they both don't exist, false otherwise
     * @throws NullPointerException if either input is null
     */
    public static boolean contentEquals(Stream<?> stream1, Stream<?> stream2) {
        if (stream1 == stream2) {
            return true;
        } else if (stream1 == null || stream2 == null) {
            return false;
        } else {
            return contentEquals(stream1.iterator(), stream2.iterator());
        }
    }

    /**
     * Compares the contents of two Readers to determine if they are equal or
     * not, ignoring EOL characters.
     * <p>
     * This method buffers the input internally using
     * {@link BufferedReader} if they are not already buffered.
     * </p>
     *
     * @param reader1 the first reader
     * @param reader2 the second reader
     * @return true if the content of the readers are equal (ignoring EOL differences),  false otherwise
     * @throws NullPointerException if either input is null
     * @throws UncheckedIOException if an I/O error occurs
     */
    public static boolean contentEqualsIgnoreEOL(BufferedReader reader1, BufferedReader reader2) {
        if (reader1 == reader2) {
            return true;
        } else if (reader1 == null || reader2 == null) {
            return false;
        } else {
            return contentEquals(reader1.lines(), reader2.lines());
        }
    }

    /**
     * Compares the contents of two Readers to determine if they are equal or
     * not, ignoring EOL characters.
     * <p>
     * This method buffers the input internally using
     * {@link BufferedReader} if they are not already buffered.
     * </p>
     *
     * @param reader1 the first reader
     * @param reader2 the second reader
     * @return true if the content of the readers are equal (ignoring EOL differences),  false otherwise
     * @throws NullPointerException if either input is null
     * @throws UncheckedIOException if an I/O error occurs
     */
    public static boolean contentEqualsIgnoreEOL(Reader reader1, Reader reader2) throws UncheckedIOException {
        if (reader1 == reader2) {
            return true;
        } else if (reader1 == null || reader2 == null) {
            return false;
        } else {
            return contentEqualsIgnoreEOL(
                    reader1 instanceof BufferedReader ?
                            (BufferedReader) reader1 : new BufferedReader(reader1),
                    reader2 instanceof BufferedReader ?
                            (BufferedReader) reader2 : new BufferedReader(reader2));
        }
    }
    //endregion

    //region resource
    /**
     * Gets contents of an internal resource
     * as {@link InputStream} into a {@code byte[]}.
     *
     * @param resourceName internal resource name.
     * @return byte [] the requested byte array, of length {@code size}
     * @throws IOException              if an I/O error occurs or {@link InputStream} length is less than {@code size}
     * @throws IllegalArgumentException if size is less than zero or size is greater than Integer.MAX_VALUE
     * @see IOUtils#toByteArray(InputStream)
     * @see IOUtils#toByteArray(InputStream, int)
     */
    public static byte[] resourceToByteArray(String resourceName) throws IOException {
        return toByteArray(resource(resourceName));
    }

    /**
     * Returns an input stream for reading the specified resource.
     *
     * @param  resourceName
     *         The resource name
     *
     * @return  An input stream for reading the resource; {@code null} if the
     *          resource could not be found, the resource is in a package that
     *          is not opened unconditionally, or access to the resource is
     *          denied by the security manager.
     *
     * @throws  NullPointerException If {@code name} is {@code null}
     */
    public static InputStream resource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }
    //endregion

}
