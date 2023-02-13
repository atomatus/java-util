package com.atomatus.util;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class IOUtilsTest extends TestCase {

    public void testToByteArray() throws IOException {
        byte[] bytes = IOUtils.resourceToByteArray("lorem_ipsum.txt");
        assertTrue(bytes.length != 0);
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
