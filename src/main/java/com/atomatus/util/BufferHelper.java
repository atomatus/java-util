package com.atomatus.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * <strong>Buffer Helper</strong>
 * <p>
 *     To help to buferring wrapper types to byte array.
 * </p>
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class BufferHelper {

    private BufferHelper() { }

    private static int requireNonNegativeOffset(int offset) {
        if(offset < 0) throw new IndexOutOfBoundsException("Offset can not be less then Zero!");
        return offset;
    }

    //region Object
    /**
     * Buffering object to byte array.
     * @param obj target
     * @param <T> target type
     * @return buffered object byte array.
     * @throws IOException if an I/O error occurs while writing data.
     */
    public static <T extends Serializable> byte[] fromObject(T obj) throws IOException {
        Objects.requireNonNull(obj);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        return bos.toByteArray();
    }

    /**
     * Buffered byte array back to be object
     * @param arr target array
     * @param <T> target type
     * @return object back
     * @throws IOException if an I/O error occurs while writing data.
     * @throws ClassNotFoundException when is not possible access object target class type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T toObject(byte[] arr) throws IOException, ClassNotFoundException {
        Objects.requireNonNull(arr);
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        bais.close();
        return (T) obj;
    }
    //endregion

    //region String
    /**
     * Buffering string to byte array.
     * @param str target string.
     * @param charset string charset.
     * @return buffered string in byte array.
     */
    public static byte[] fromString(String str, Charset charset) {
        return str.getBytes(charset);
    }

    /**
     * Buffering string to byte array.
     * @param str target string.
     * @return buffered string in byte array.
     */
    public static byte[] fromString(String str) {
        return str.getBytes();
    }

    /**
     * Buffered byte array back to be string
     * @param buffer target array
     * @param offset the index of the first byte to decode
     * @return string back
     */
    public static String toString(byte[] buffer, int offset) {
        ArrayHelper.requireArrayNonNullOrEmpty(buffer);
        return new String(buffer, offset, buffer.length);
    }

    /**
     * Buffered byte array back to be string
     * @param buffer target array
     * @return string back
     */
    public static String toString(byte[] buffer) {
        return toString(buffer, 0);
    }
    //endregion

    //region char
    /**
     * Buffering char to byte array.
     * @param value target char.
     * @return buffered char in byte array.
     */
    public static byte[] fromChar(char value) {
        return new byte[] { (byte) value };
    }

    /**
     * Buffering char to byte array.
     * @param value target char.
     * @return buffered char in byte array.
     */
    public static byte[] fromChar(Character value) {
        return fromChar(value.charValue());
    }

    /**
     * Buffered byte array back to be char
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return char back
     */
    public static char toChar(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) == 0) {
            throw new IllegalArgumentException("Byte array can not be empty!");
        }
        return (char) bytes[offset];
    }

    /**
     * Buffered byte array back to be char
     * @param bytes target array
     * @return char back
     */
    public static char toChar(byte[] bytes) {
        return toChar(bytes, 0);
    }
    //endregion

    //region short
    /**
     * Buffering short to byte array.
     * @param value target short.
     * @return buffered short in byte array.
     */
    public static byte[] fromShort(short value) {
        return new byte[] {
                (byte)(value >> 8),
                (byte)(value /*>> 0*/) };
    }

    /**
     * Buffering short to byte array.
     * @param value target short.
     * @return buffered short in byte array.
     */
    public static byte[] fromShort(Number value) {
        return fromShort(value.shortValue());
    }

    /**
     * Buffered byte array back to be short
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return short back
     */
    public static short toShort(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) < Short.BYTES) {
            return (short) toChar(bytes, offset);
        }

        return (short) ((bytes[offset++] <<  8) | (bytes[offset] & 0xFF));
    }

    /**
     * Buffered byte array back to be short
     * @param bytes target array
     * @return short back
     */
    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }
    //endregion

    //region int
    /**
     * Buffering int to byte array.
     * @param value target int.
     * @return buffered int in byte array.
     */
    public static byte[] fromInt(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)(value /*>> 0*/) };
    }

    /**
     * Buffering int to byte array.
     * @param value target int.
     * @return buffered int in byte array.
     */
    public static byte[] fromInt(Number value) {
        return fromInt(value.intValue());
    }

    /**
     * Buffered byte array back to be int
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return int back
     */
    public static int toInt(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) < Integer.BYTES) {
            return toShort(bytes, offset);
        }

        return ((bytes[offset++]) << 24) |
               ((bytes[offset++] & 0xFF) << 16) |
               ((bytes[offset++] & 0xFF) <<  8) |
               (bytes[offset] & 0xFF);
    }

    /**
     * Buffered byte array back to be int
     * @param bytes target array
     * @return int back
     */
    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }
    //endregion

    //region long
    /**
     * Buffering long to byte array.
     * @param value target long.
     * @return buffered long in byte array.
     */
    public static byte[] fromLong(long value) {
        if(Long.BYTES != 8) {
            return new byte[] {
                    (byte)(value >> 24),
                    (byte)(value >> 16),
                    (byte)(value >>  8),
                    (byte) value };
        } else{
            return new byte[] {
                    (byte)(value >> 56),
                    (byte)(value >> 48),
                    (byte)(value >> 40),
                    (byte)(value >> 32),
                    (byte)(value >> 24),
                    (byte)(value >> 16),
                    (byte)(value >>  8),
                    (byte) value };
        }
    }

    /**
     * Buffering long to byte array.
     * @param value target long.
     * @return buffered long in byte array.
     */
    public static byte[] fromLong(Number value) {
        return fromLong(value.longValue());
    }

    /**
     * Buffered byte array back to be long
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return long back
     */
    public static long toLong(byte[] bytes, int offset) {
        if(Long.BYTES < 8 || (bytes.length - requireNonNegativeOffset(offset)) < Long.BYTES) {
            return toInt(bytes, offset);
        }

        return (((long)bytes[offset++]) << 56) |
               (((long)bytes[offset++] & 0xFF) << 48) |
               (((long)bytes[offset++] & 0xFF) << 40) |
               (((long)bytes[offset++] & 0xFF) << 32) |
               (((long)bytes[offset++] & 0xFF) << 24) |
               (((long)bytes[offset++] & 0xFF) << 16) |
               (((long)bytes[offset++] & 0xFF) << 8) |
                ((long)bytes[offset] & 0xFF);
    }

    /**
     * Buffered byte array back to be long
     * @param bytes target array
     * @return long back
     */
    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }
    //endregion

    //region float
    /**
     * Buffering float to byte array.
     * @param value target float.
     * @return buffered float in byte array.
     */
    public static byte[] fromFloat(float value) {
        int intBits = Float.floatToIntBits(value);
        return fromInt(intBits);
    }

    /**
     * Buffering float to byte array.
     * @param value target float.
     * @return buffered float in byte array.
     */
    public static byte[] fromFloat(Number value) {
        return fromFloat(value.floatValue());
    }

    /**
     * Buffered byte array back to be float
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return float back
     */
    public static float toFloat(byte[] bytes, int offset) {
        int intBits = toInt(bytes, offset);
        return Float.intBitsToFloat(intBits);
    }

    /**
     * Buffered byte array back to be float
     * @param bytes target array
     * @return float back
     */
    public static float toFloat(byte[] bytes) {
        return toFloat(bytes, 0);
    }
    //endregion

    //region double
    /**
     * Buffering double to byte array.
     * @param value target double.
     * @return buffered double in byte array.
     */
    public static byte[] fromDouble(double value) {
        long longBits = Double.doubleToRawLongBits(value);
        return fromLong(longBits);
    }

    /**
     * Buffering double to byte array.
     * @param value target double.
     * @return buffered double in byte array.
     */
    public static byte[] fromDouble(Number value) {
        return fromDouble(value.doubleValue());
    }

    /**
     * Buffered byte array back to be double
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return double back
     */
    public static double toDouble(byte[] bytes, int offset) {
        long longBits = toLong(bytes, offset);
        return Double.longBitsToDouble(longBits);
    }

    /**
     * Buffered byte array back to be double
     * @param bytes target array
     * @return double back
     */
    public static double toDouble(byte[] bytes) {
        return toDouble(bytes, 0);
    }
    //endregion

    //region boolean
    /**
     * Buffering boolean to byte array.
     * @param value target boolean.
     * @return buffered boolean in byte array.
     */
    public static byte[] fromBoolean(boolean value) {
        return fromInt(value ? 1 : 0);
    }

    /**
     * Buffering boolean to byte array.
     * @param value target boolean.
     * @return buffered boolean in byte array.
     */
    public static byte[] fromBoolean(Boolean value) {
        return fromInt(value ? 1 : 0);
    }

    /**
     * Buffered byte array back to be boolean
     * @param bytes target array
     * @param offset the index of the first byte to decode
     * @return boolean back
     */
    public static boolean toBoolean(byte[] bytes, int offset) {
        return toInt(bytes, offset) == 1 || "TRUE".equalsIgnoreCase(BufferHelper.toString(bytes, offset));
    }

    /**
     * Buffered byte array back to be boolean
     * @param bytes target array
     * @return boolean back
     */
    public static boolean toBoolean(byte[] bytes) {
        return toBoolean(bytes, 0);
    }
    //endregion
}
