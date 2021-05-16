package com.atomatus.util.security;

import junit.framework.TestCase;
import org.junit.Assert;

public class Cipher3DESTest extends TestCase {

    private void doEncDec(Cipher3DES cipher3DES) {
        String str = "hello world";
        byte[] arr = str.getBytes();
        byte[] enc = cipher3DES.encrypt(arr);
        byte[] dec = cipher3DES.decrypt(enc);
        Assert.assertArrayEquals(arr, dec);
        assertEquals(str, new String(dec));
    }

    public void testDefault() {
        doEncDec(new Cipher3DES());
    }

    public void testSecretKey() {
        doEncDec(new Cipher3DES(Cipher3DES.generateKey()));
        doEncDec(new Cipher3DES("0123456789QWERTYUIOPASDF"));
    }

    public void testSecretKeyIv() {
        doEncDec(new Cipher3DES(Cipher3DES.generateKey(), Cipher3DES.generateIv()));
        doEncDec(new Cipher3DES("0123456789QWERTYUIOPASDF", "98989898"));
    }

}