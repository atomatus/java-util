package com.atomatus.util;

import junit.framework.TestCase;

/**
 * <p>
 *
 * </p>
 * <i>Created by chcmatos on 27, janeiro, 2023</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
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

}