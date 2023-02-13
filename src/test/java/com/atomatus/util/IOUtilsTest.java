package com.atomatus.util;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtilsTest extends TestCase {

    public void testToByteArray() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("lorem_ipsum.txt")) {
            byte[] bytes = IOUtils.toByteArray(is);
            assertEquals(bytes.length, 448);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testContentEquals() {
        ByteArrayInputStream bis0 = new ByteArrayInputStream(
                "test123".getBytes());
        assertNotNull(bis0);
        ByteArrayInputStream bis1 = new ByteArrayInputStream(
                "test123".getBytes());
        assertNotNull(bis1);
        try {
            boolean r = IOUtils.contentEquals(bis0, bis1);
            assertTrue(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
