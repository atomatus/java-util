package com.atomatus.util.bom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a functional interface for BOM (Byte Order Mark) writing strategies.
 */
@FunctionalInterface
interface BOMStrategy {
    /**
     * Writes the BOM to an OutputStream.
     *
     * @param os The OutputStream where the BOM should be written.
     * @throws IOException If an I/O error occurs during writing.
     */
    void writeIt(OutputStream os) throws IOException;

    /**
     * Writes the BOM to a file.
     *
     * @param file The File where the BOM should be written.
     * @throws IOException If an I/O error occurs during writing.
     */
    default void writeIt(File file) throws IOException {
        try(FileOutputStream fs = new FileOutputStream(file)) {
            writeIt(fs);
        }
    }

    /**
     * Attempts to write the BOM to an OutputStream and returns true if successful.
     *
     * @param os The OutputStream where the BOM should be written.
     * @return true if the BOM was written successfully, false in case of an error.
     */
    default boolean tryWriteIt(OutputStream os) {
        try{
            writeIt(os);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Attempts to write the BOM to a file and returns true if successful.
     *
     * @param file The File where the BOM should be written.
     * @return true if the BOM was written successfully, false in case of an error.
     */
    default boolean tryWriteIt(File file) {
        try{
            writeIt(file);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
