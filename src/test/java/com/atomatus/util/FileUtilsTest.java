package com.atomatus.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URISyntaxException;

public class FileUtilsTest extends TestCase {

    public void testSizeUnits() {
        long exp = 0;
        for (FileUtils.SizeUnit unit : FileUtils.SizeUnit.values()) {
             assertEquals((long)Math.pow(1024, exp++), unit.numberOfBytes());
        }
    }

    public void testDisplayFileSize() {
        for(FileUtils.SizeUnit unit : FileUtils.SizeUnit.values()) {
            int n = (int)(Math.random() * 6) + 1;
            String size = FileUtils.displayFileSize(unit.numberOfBytes() * n);
            assertEquals("Failed in test display file size for unit "
                            + unit + " as \"" + n + " " + unit.symbol() + "\" " ,
                    size, unit.symbolCast(n));
        }
    }

    public void testResourceContent() throws IOException, URISyntaxException {
        String str = "Lorem ipsum dolor sit amet";
        String res = FileUtils.resourceContent("lorem_ipsum.txt");
        assertTrue("Invalid resource content!", res.startsWith(str));
    }

    public void testFileSize() throws IOException, URISyntaxException {
        String size = FileUtils.displayFileSize(FileUtils.resource("lorem_ipsum.txt"));
        assertNotNull(size);
    }

}