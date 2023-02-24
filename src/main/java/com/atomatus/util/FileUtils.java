package com.atomatus.util;

import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

/**
 * General File Utilitieis.
 * <p>
 * Facilities are provided in the following areas:
 * </p>
 * <ul>
 * <li>writing to a file
 * <li>reading from a file
 * <li>calculating file size
 * <li>comparing file content
 * </ul>
 */
public final class FileUtils {

    /**
     * File size units.
     * <i>Created by chcmatos on 27, janeiro, 2022</i>
     *
     * @author Carlos Matos {@literal @chcmatos}
     */
    public enum SizeUnit {
        /**
         * Byte.
         * <p><i>The number of bytes is: 1</i></p>
         */
        BYTE,
        /**
         * Kilobyte
         * <p><i>The number of bytes is: 1024</i></p>
         */
        KBYTE,
        /**
         * Megabyte
         * <p><i>The number of bytes is: 1024^2</i></p>
         */
        MBYTE,
        /**
         * Gigabyte
         * <p><i>The number of bytes is: 1024^3</i></p>
         */
        GBYTE,
        /**
         * Terabyte
         * <p><i>The number of bytes is: 1024^4</i></p>
         */
        TBYTE,
        /**
         * Petabyte
         * <p><i>The number of bytes is: 1024^5</i></p>
         */
        PBYTE,
        /**
         * Exabyte
         * <p><i>The number of bytes is: 1024^6</i></p>
         */
        EBYTE;

        /**
         * The number of bytes in this file unit type.
         * @return number of bytes.
         */
        public long numberOfBytes() {
            return (long) Math.pow(1024, ordinal());
        }

        /**
         * The number of bytes in this file unit type.
         * @return number of bytes in BigInteger.
         */
        public BigInteger numberOfBytesBI() {
            return BigInteger.valueOf(1024L).pow(ordinal());
        }

        /**
         * Unit type symbol.
         * <br>
         * <i>Example: MB</i>
         * @return symbol.
         */
        public String symbol() {
            return this == BYTE ? "bytes" : this.name().substring(0, 2);
        }

        /**
         * Cast unit type symbol with prefix value.
         * <br>
         * <i>Example: 2 MB</i>
         * @param prefix prefix
         * @return prefix with value.
         */
        public String symbolCast(Number prefix) {
            return prefix + " " + symbol();
        }
    }

    private FileUtils() { }

    /**
     * Requires that the given {@code File} is a file.
     *
     * @param file The {@code File} to check.
     * @param name The parameter name to use in the exception message.
     * @return the given file.
     * @throws NullPointerException if the given {@code File} is {@code null}.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     */
    public static File requireFile(File file, String name) {
        if(!Objects.requireNonNull(file, "Parameter \"" + name + "\" is null").isFile()) {
            throw new IllegalArgumentException("Parameter \"" + name + "\" is not a file: " + file);
        }
        return file;
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param file target file
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @throws NullPointerException if the given {@code File} is {@code null}.
     */
    public static String displayFileSize(File file) {
        return displayFileSize(Objects.requireNonNull(file, "File is null!").length());
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     */
    public static String displayFileSize(long size) {
        return displayFileSize(BigInteger.valueOf(size));
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @throws NullPointerException if the given {@code BigInteger} is {@code null}.
     */
    public static String displayFileSize(BigInteger size) {
        Objects.requireNonNull(size, "Size is null!");
        SizeUnit[] values = SizeUnit.values();
        for(int i = values.length - 1; i >= 0; i--) {
            SizeUnit unit = values[i];
            BigInteger res = size.divide(unit.numberOfBytesBI());
            if (res.compareTo(BigInteger.ZERO) > 0) {
                return unit.symbolCast(res);
            }
        }
        throw new UnsupportedOperationException("Ops! This file is so larger!");
    }

    /**
     * Tests whether the contents of two files are equal.
     * <p>
     * This method checks to see if the two files are different lengths or if they point to the same file, before
     * resorting to byte-by-byte comparison of the contents.
     * </p>
     * <p>
     * Based Code of: Avalon
     * </p>
     *
     * @param file1 the first file
     * @param file2 the second file
     * @return true if the content of the files are equal or they both don't exist, false otherwise
     * @throws IllegalArgumentException when an input is not a file.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean contentEquals(File file1, File file2) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        } else if (file1 == null || file2 == null) {
            return false;
        }

        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        } else if (!file1Exists) {
            // two not existing files are equal
            return true;
        } else if (requireFile(file1, "file1").length() != requireFile(file2, "file2").length()) {
            // lengths differ, cannot be equal
            return false;
        } else if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            // same file
            return true;
        } else {
            try (FileInputStream fis1 = new FileInputStream(file1);
                 FileInputStream fis2 = new FileInputStream(file2)) {
                return IOUtils.contentEquals(fis1, fis2);
            }
        }
    }

    /**
     * Compares the contents of two files to determine if they are equal or not.
     * <p>
     * This method checks to see if the two files point to the same file,
     * before resorting to line-by-line comparison of the contents.
     * </p>
     *
     * @param file1       the first file
     * @param file2       the second file
     * @param charsetName the name of the requested charset.
     *                    May be null, in which case the platform default is used
     * @return true if the content of the files are equal or neither exists, false otherwise
     * @throws IllegalArgumentException when an input is not a file.
     * @throws IOException in case of an I/O error.
     */
    public static boolean contentEqualsIgnoreEOL(File file1, File file2, final String charsetName)
            throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        } else if (file1 == null || file2 == null) {
            return false;
        }

        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        } else if (!file1Exists) {
            // two not existing files are equal
            return true;
        } else if (requireFile(file1, "file1").getCanonicalFile().equals(requireFile(file2, "file2").getCanonicalFile())) {
            // same file
            return true;
        } else {
            try (FileInputStream fis1 = new FileInputStream(file1);
                 FileInputStream fis2 = new FileInputStream(file1);
                 Reader input1 = new InputStreamReader(fis1, charsetName);
                 Reader input2 = new InputStreamReader(fis2, charsetName)) {
                return IOUtils.contentEqualsIgnoreEOL(input1, input2);
            }
        }
    }

    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     *
     * @param file the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     * @throws NullPointerException if file is {@code null}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     *         other reason cannot be opened for reading.
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] toByteArray(File file) throws IOException {
        //noinspection IOStreamConstructor
        try (InputStream inputStream = new FileInputStream(file)) {
            final long fileLength = file.length();
            // file.length() may return 0 for system-dependent entities, treat 0 as unknown length - see IO-453
            return fileLength > 0 ? IOUtils.toByteArray(inputStream, fileLength) : IOUtils.toByteArray(inputStream);
        }
    }

    /**
     * Reads the contents of an internal resouce file into a byte array.
     * The file is always closed.
     * @param resourceName resource name
     * @return resource content as byte array.
     * @throws FileNotFoundException throws when resource is not found.
     */
    public static byte[] resourceToByteArray(String resourceName) throws IOException {
        return IOUtils.resourceToByteArray(resourceName);
    }

    /**
     * Reads the contents of an internal resouce file.
     * The file is always closed.
     * @param resourceName resource name
     * @param charset charset name
     * @return resource content as string.
     * @throws FileNotFoundException throws when resource is not found.
     */
    public static String resourceContent(String resourceName, Charset charset) throws IOException {
        return new String(resourceToByteArray(resourceName), charset);
    }

    /**
     * Reads the contents of an internal resouce file.
     * The file is always closed.
     * @param resourceName resource name
     * @return resource content as string.
     * @throws FileNotFoundException throws when resource is not found.
     */
    public static String resourceContent(String resourceName) throws IOException {
        return new String(resourceToByteArray(resourceName));
    }

    /**
     * Get an internal jar resource by name.
     * @param resourceName resource name
     * @return resource as file.
     * @throws NullPointerException throws when resourceName is null.
     * @throws FileNotFoundException throws when resource is not found.
     * @throws URISyntaxException throws if is not possible convert resource path to URI.
     */
    public static File resource(String resourceName) throws IOException, URISyntaxException {
       URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
       if(url == null) {
           throw new FileNotFoundException("Resource \"" + resourceName + "\" not found!");
       }
       return new File(url.toURI());
    }

    /**
     * Extract internal resource to target file path.
     * @param file target file.
     * @param resourceName resource name.
     * @return resorce extracted file.
     * @throws IOException throws any I/O exception.
     * @throws NullPointerException throws when file or resourceName is null.
     */
    public static File extractResource(File file, String resourceName) throws IOException {
        write(file, IOUtils.resource(resourceName), true);
        return file;
    }

    /**
     * Extract internal resource to target temporary file created.
     * @param tmpFileName temporary file name (only name).
     * @param resourceName resource name.
     * @return resorce extracted file.
     * @throws IOException throws any I/O exception.
     * @throws NullPointerException throws when tmpFileName or resourceName is null.
     */
    public static File extractResource(String tmpFileName, String resourceName) throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile = new File(tmpDir, tmpFileName);
        tmpFile.deleteOnExit();
        write(tmpFile, IOUtils.resource(resourceName), true);
        return tmpFile;
    }

    /**
     * Extract internal resource to temporary directory named with pefix "res" and suffix ".tmp".
     * @param resourceName resource name.
     * @param deleteOnExit true, delete extracted file on app exit, otherwise false.
     * @return resorce extracted temp file.
     * @throws IOException throws any I/O exception.
     * @throws NullPointerException throws when resourceName is null.
     */
    public static File extractResource(String resourceName, boolean deleteOnExit) throws IOException {
        File file = File.createTempFile("res", ".tmp");
        if(deleteOnExit) file.deleteOnExit();
        return extractResource(file, resourceName);
    }

    /**
     * Extract internal resource to temporary directory named with pefix "res" and suffix ".tmp",
     * it will be deleted on app exit.
     * @param resourceName resource temp name.
     * @return resorce extracted file.
     * @throws IOException throws any I/O exception.
     * @throws NullPointerException throws when resourceName is null.
     */
    public static File extractResource(String resourceName) throws IOException {
        return extractResource(resourceName, true);
    }

    /**
     * Extract internal resource to temporary directory named with same name.
     * It will be deleted on app exit.
     * @param resourceName resource temp name.
     * @return resorce extracted file.
     * @throws IOException throws any I/O exception.
     * @throws NullPointerException throws when resourceName is null.
     */
    public static File extractResourceKeepName(String resourceName) throws IOException {
        return extractResource(resourceName, resourceName);
    }

    /**
     * Create or update file content.
     * @param file target file
     * @param buffer new file content
     * @param offset start index
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(File file, byte[] buffer, int offset, int length) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(Objects.requireNonNull(buffer, "Buffer is null!"), offset, length);
        }
    }

    /**
     * Create or update file content.
     * @param file target file
     * @param buffer new file content
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(File file, byte[] buffer, int length) throws IOException {
        write(file, buffer, 0, length);
    }

    /**
     * Create or update file content.
     * @param file target file
     * @param buffer new file content
     * @throws IOException throws any I/O exception.
     */
    public static void write(File file, byte[] buffer) throws IOException {
        write(file, Objects.requireNonNull(buffer, "Buffer is null!"), 0, buffer.length);
    }

    /**
     * Create or update file content.
     * @param filePath target file
     * @param buffer new file content
     * @param offset start index
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(Path filePath, byte[] buffer, int offset, int length) throws IOException {
        write(filePath.toFile(), buffer, offset, length);
    }

    /**
     * Create or update file content.
     * @param filePath target file
     * @param buffer new file content
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(Path filePath, byte[] buffer, int length) throws IOException {
        write(filePath.toFile(), buffer, length);
    }

    /**
     * Create or update file content.
     * @param filePath target file
     * @param buffer new file content
     * @throws IOException throws any I/O exception.
     */
    public static void write(Path filePath, byte[] buffer) throws IOException {
        write(filePath.toFile(), buffer);
    }

    /**
     * Create or update file content.
     * @param filePath target file path
     * @param buffer new file content
     * @param offset start index
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(String filePath, byte[] buffer, int offset, int length) throws IOException {
        write(new File(filePath), buffer, offset, length);
    }

    /**
     * Create or update file content.
     * @param filePath target file path
     * @param buffer new file content
     * @param length data length.
     * @throws IOException throws any I/O exception.
     */
    public static void write(String filePath, byte[] buffer, int length) throws IOException {
        write(new File(filePath), buffer, length);
    }

    /**
     * Create or update file content.
     * @param filePath target file path
     * @param buffer new file content
     * @throws IOException throws any I/O exception.
     */
    public static void write(String filePath, byte[] buffer) throws IOException {
        write(new File(filePath), buffer);
    }

    /**
     * Create or update file content.
     * @param file target file path
     * @param is new file content.
     * @param close true close stream connection, false otherwise.
     * @throws IOException throws any I/O exception.
     */
    public static void write(File file, InputStream is, boolean close) throws IOException {
        //noinspection IOStreamConstructor
        IOUtils.write(new FileOutputStream(file), is, close);
    }

    /**
     * Create or update file content and close input stream.
     * @param file target file path
     * @param is new file content.
     * @throws IOException throws any I/O exception.
     */
    public static void write(File file, InputStream is) throws IOException {
        write(file, is, true);
    }

}
