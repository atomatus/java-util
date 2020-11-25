package com.atomatus.util;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void testIsNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(""));
    }

    public void testIsNullOrWhitespace() {
        assertTrue(StringUtils.isNullOrWhitespace("  "));
    }

    public void testCapitalize() {
        assertEquals("abc",
                StringUtils.capitalize("AbC", StringUtils.Capitalize.NONE));

        assertEquals("ABC",
                StringUtils.capitalize("aBc", StringUtils.Capitalize.ALL));

        assertEquals("Da Pacem Domine",
                StringUtils.capitalize("DA pacem dOmiNe", StringUtils.Capitalize.FIRST_EACH_WORD));

        assertEquals("Da pacem domine",
                StringUtils.capitalize("DA pacem dOmiNe", StringUtils.Capitalize.ONLY_FIRST));
    }

    public void testPadLeft() {
        assertEquals("001",
                StringUtils.padLeft("1", 3, '0'));
    }

    public void testTestPadLeft() {
        assertEquals("01",
                StringUtils.padLeft("01", 3, "00"));

        assertEquals("001",
                StringUtils.padLeft("1", 3, "00"));
    }

    public void testPadRight() {
        assertEquals("100",
                StringUtils.padRight("1", 3, '0'));
    }

    public void testTestPadRight() {
        assertEquals("10",
                StringUtils.padRight("10", 3, "00"));

        assertEquals("100",
                StringUtils.padRight("1", 3, "00"));
    }
}