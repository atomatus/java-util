package com.atomatus.util.bom;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * <p>
 * Serves as an intermediary that encapsulates a BOMStrategy.
 * It allows for simplified creation and usage of BOMStrategy based on the provided Charset.
 * </p>
 * <p>
 * BOM (Byte Order Mark) is a special character at the beginning of a text file to indicate character
 * encoding and byte order. It helps with encoding detection, endianness resolution, and preventing
 * character corruption.
 * </p>
 * <p>
 * <h4>Usage Sample (File)</h4>
 * <pre>{@code
 *  BOMHelper
 *      .createBy(StandardCharsets.UTF_8)
 *      .writeIt(targetFile);
 * }</pre>
 * <h4>Usage Sample (OutputStream)</h4>
 * <pre>{@code
 *  BOMHelper
 *      .createBy(StandardCharsets.UTF_8)
 *      .writeIt(targetOutputStream);
 * }</pre>
 * </p>
 */
@SuppressWarnings("UnusedReturnValue")
public final class BOMHelper implements BOMStrategy {

    private final BOMStrategy strategy;

    /**
     * Private constructor to create a BOMHelper with the provided BOMStrategy.
     *
     * @param strategy The BOMStrategy to encapsulate.
     */
    private BOMHelper(BOMStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Static factory method to create a BOMHelper based on the provided Charset.
     *
     * @param charset The Charset for which to create the BOMHelper.
     * @return A BOMHelper encapsulating the appropriate BOMStrategy.
     */
    public static BOMHelper createBy(Charset charset) {
        return new BOMHelper(BOMStrategyFactory.create(charset));
    }

    @Override
    public void writeIt(OutputStream os) throws IOException {
        this.strategy.writeIt(os);
    }

    @Override
    public void writeIt(File file) throws IOException {
        this.strategy.writeIt(file);
    }

    @Override
    public boolean tryWriteIt(OutputStream os) {
        return this.strategy.tryWriteIt(os);
    }

    @Override
    public boolean tryWriteIt(File file) {
        return this.strategy.tryWriteIt(file);
    }
}
