package com.atomatus.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

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

    public void testJoin() {
        String sep = ", ";
        String s0  = StringUtils.join(sep, 0, '1', 2, 2.1f, 2.2d, "3", "45", Long.MAX_VALUE, Boolean.FALSE,
                new char[]{ 'C', 'H', 'A', 'R' });

        List<Object> aux = new ArrayList<>();
        aux.add(0);
        aux.add('1');
        aux.add(2);
        aux.add(2.1f);
        aux.add(2.2d);
        aux.add("3");
        aux.add("45");
        aux.add(Long.MAX_VALUE);
        aux.add(Boolean.FALSE);
        aux.add(new char[]{ 'C', 'H', 'A', 'R' });

        String s1  = StringUtils.join(sep, aux);

        assertEquals(s0, s1);
    }

}