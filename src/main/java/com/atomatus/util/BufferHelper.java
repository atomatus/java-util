package com.atomatus.util;

import java.nio.charset.Charset;

public final class BufferHelper {

    private BufferHelper() { }

    private static int requireNonNegativeOffset(int offset) {
        if(offset < 0) throw new IndexOutOfBoundsException("Offset can not be less then Zero!");
        return offset;
    }

    //region String
    public static byte[] fromString(String str, Charset charset) {
        return str.getBytes(charset);
    }

    public static byte[] fromString(String str) {
        return str.getBytes();
    }

    public static String toString(byte[] buffer, int offset) {
        ArrayHelper.requireArrayNonNullOrEmpty(buffer);
        return new String(buffer, offset, buffer.length);
    }

    public static String toString(byte[] buffer) {
        return toString(buffer, 0);
    }
    //endregion

    //region char
    public static byte[] fromChar(char value) {
        return new byte[] { (byte) value };
    }

    public static byte[] fromChar(Character value) {
        return fromChar(value.charValue());
    }

    public static char toChar(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) == 0) {
            throw new IllegalArgumentException("Byte array can not be empty!");
        }
        return (char) bytes[offset];
    }

    public static char toChar(byte[] bytes) {
        return toChar(bytes, 0);
    }
    //endregion

    //region short
    public static byte[] fromShort(short value) {
        return new byte[] {
                (byte)(value >> 8),
                (byte)(value /*>> 0*/) };
    }

    public static byte[] fromShort(Number value) {
        return fromShort(value.shortValue());
    }

    public static short toShort(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) < Short.BYTES) {
            return (short) toChar(bytes, offset);
        }

        return (short) ((bytes[offset++] <<  8) | (bytes[offset] & 0xFF));
    }

    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }
    //endregion

    //region int
    public static byte[] fromInt(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)(value /*>> 0*/) };
    }

    public static byte[] fromInt(Number value) {
        return fromInt(value.intValue());
    }

    public static int toInt(byte[] bytes, int offset) {
        if((bytes.length - requireNonNegativeOffset(offset)) < Integer.BYTES) {
            return toShort(bytes, offset);
        }

        return ((bytes[offset++]) << 24) |
               ((bytes[offset++] & 0xFF) << 16) |
               ((bytes[offset++] & 0xFF) <<  8) |
               (bytes[offset] & 0xFF);
    }

    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }
    //endregion

    //region long
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

    public static byte[] fromLong(Number value) {
        return fromLong(value.longValue());
    }

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

    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }
    //endregion

    //region float
    public static byte[] fromFloat(float value) {
        int intBits = Float.floatToIntBits(value);
        return fromInt(intBits);
    }

    public static byte[] fromFloat(Number value) {
        return fromFloat(value.floatValue());
    }

    public static float toFloat(byte[] bytes, int offset) {
        int intBits = toInt(bytes, offset);
        return Float.intBitsToFloat(intBits);
    }

    public static float toFloat(byte[] bytes) {
        return toFloat(bytes, 0);
    }
    //endregion

    //region double
    public static byte[] fromDouble(double value) {
        long longBits = Double.doubleToRawLongBits(value);
        return fromLong(longBits);
    }

    public static byte[] fromDouble(Number value) {
        return fromDouble(value.doubleValue());
    }

    public static double toDouble(byte[] bytes, int offset) {
        long longBits = toLong(bytes, offset);
        return Double.longBitsToDouble(longBits);
    }

    public static double toDouble(byte[] bytes) {
        return toDouble(bytes, 0);
    }
    //endregion

    //region boolean
    public static byte[] fromBoolean(boolean value) {
        return fromInt(value ? 1 : 0);
    }

    public static byte[] fromBoolean(Boolean value) {
        return fromInt(value ? 1 : 0);
    }

    public static boolean toBoolean(byte[] bytes, int offset) {
        return toInt(bytes, offset) == 1 || "TRUE".equalsIgnoreCase(BufferHelper.toString(bytes, offset));
    }

    public static boolean toBoolean(byte[] bytes) {
        return toBoolean(bytes, 0);
    }
    //endregion
}
