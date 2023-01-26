package com.atomatus.util.mod;

import junit.framework.TestCase;

public class Mod43Test extends TestCase {

    public void testMode43Cod39() {
        String value = "159AZ";
        Mod mod = Mod.get(Mod.MOD_43);
        int c = mod.calc(value);
        assertEquals(60, c);
        int m = mod.mod(value);
        assertEquals(17, m);
        int r = mod.checkum(value);
        assertEquals(17, r);
        int t = mod.checkumChar(value);
        assertEquals('H', t);
    }

}