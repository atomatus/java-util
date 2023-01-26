package com.atomatus.util.mod;

import junit.framework.TestCase;

public class Mod16Test extends TestCase {

    public void testMod16Codabar() {
        //https://pt.activebarcode.com/codes/checkdigit/modulo16
        String value = "A789A";
        Mod mod = Mod.get(Mod.MOD_16);
        int c = mod.calc(value);
        assertEquals(56, c);
        int m = mod.mod(value);
        assertEquals(8, m);
        int r = mod.checkum(value);
        assertEquals(8, r);
    }

}