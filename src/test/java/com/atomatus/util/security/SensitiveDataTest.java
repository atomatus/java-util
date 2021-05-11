package com.atomatus.util.security;

import junit.framework.TestCase;

public class SensitiveDataTest extends TestCase {

    public void testInt() {
        SensitiveData sd = new SensitiveData();
        sd.append(0).append(1).append(2).append(3);
        assertEquals("0123", sd.toString());
    }

    public void testLong() {
        SensitiveData sd = new SensitiveData();
        sd.append(0L).append(1L).append(2L).append(3L);
        assertEquals("0123", sd.toString());
    }

    public void testDouble() {
        SensitiveData sd = new SensitiveData();
        sd.append(0.1d).append(1.2d).append(2.3d).append(3.4d);
        assertEquals("0.11.22.33.4", sd.toString());
    }

}