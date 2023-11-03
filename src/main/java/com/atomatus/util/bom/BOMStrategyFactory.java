package com.atomatus.util.bom;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This class represents a factory for creating BOMStrategy objects based on the provided Charset.
 */
final class BOMStrategyFactory {

    private BOMStrategyFactory() { }

    /**
     * Creates a BOMStrategy based on the given Charset.
     *
     * @param charset The Charset for which to create the BOMStrategy.
     * @return A BOMStrategy corresponding to the provided Charset.
     */
    static BOMStrategy create(Charset charset) {
        if (charset.equals(StandardCharsets.UTF_8)) {
            return createForUTF8();
        } else if (charset.equals(StandardCharsets.UTF_16)) {
            return createForUTF16();
        } else if (charset.equals(StandardCharsets.UTF_16BE)) {
            return createForUTF16BigEndian();
        } else if (charset.equals(StandardCharsets.UTF_16LE)) {
            return createForUTF16LittleEndian();
        } else if (charset.displayName().contains("UTF-32")) {
            return createForUTF32(); // UTF-32, not commonly used in Java.
        } else {
            return createEmpty();
        }
    }

    /**
     * Creates a BOMStrategy for UTF-8.
     *
     * @return A BOMStrategy for UTF-8.
     */
    private static BOMStrategy createForUTF8() {
        return os -> {
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // UTF-8 BOM.
        };
    }

    /**
     * Creates a BOMStrategy for UTF-16 based on Byte Order.
     *
     * @return A BOMStrategy for UTF-16 (Big-Endian or Little-Endian) based on Byte Order.
     */
    private static BOMStrategy createForUTF16() {
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return createForUTF16BigEndian();
        } else {
            return createForUTF16LittleEndian();
        }
    }

    /**
     * Creates a BOMStrategy for UTF-16 Big-Endian.
     *
     * @return A BOMStrategy for UTF-16 Big-Endian.
     */
    private static BOMStrategy createForUTF16BigEndian() {
        return os -> {
            os.write(new byte[]{(byte) 0xFE, (byte) 0xFF}); // UTF-16 Big-Endian BOM.
        };
    }

    /**
     * Creates a BOMStrategy for UTF-16 Little-Endian.
     *
     * @return A BOMStrategy for UTF-16 Little-Endian.
     */
    private static BOMStrategy createForUTF16LittleEndian() {
        return os -> {
            os.write(new byte[]{(byte) 0xFF, (byte) 0xFE}); // UTF-16 Little-Endian BOM.
        };
    }

    /**
     * Creates a BOMStrategy for UTF-32 based on Byte Order.
     *
     * @return A BOMStrategy for UTF-32 (Big-Endian or Little-Endian) based on Byte Order.
     */
    private static BOMStrategy createForUTF32() {
        ByteOrder byteOrder = ByteOrder.nativeOrder();
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return createForUTF32BigEndian();
        } else {
            return createForUTF32LittleEndian();
        }
    }

    /**
     * Creates a BOMStrategy for UTF-32 Big-Endian.
     *
     * @return A BOMStrategy for UTF-32 Big-Endian.
     */
    private static BOMStrategy createForUTF32BigEndian() {
        return os -> {
            os.write(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF}); // UTF-32 Big-Endian BOM.
        };
    }

    /**
     * Creates a BOMStrategy for UTF-32 Little-Endian.
     *
     * @return A BOMStrategy for UTF-32 Little-Endian.
     */
    private static BOMStrategy createForUTF32LittleEndian() {
        return os -> {
            os.write(new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00}); // UTF-32 Little-Endian BOM.
        };
    }

    /**
     * Creates an empty BOMStrategy that does nothing.
     *
     * @return An empty BOMStrategy.
     */
    private static BOMStrategy createEmpty() {
        return os -> { };
    }
}
