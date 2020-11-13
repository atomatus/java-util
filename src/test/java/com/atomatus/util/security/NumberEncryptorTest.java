package com.atomatus.util.security;

import junit.framework.TestCase;

public class NumberEncryptorTest extends TestCase {

    public void testEncryptDecrypt() {
        String pk = KeyGenerator.generateRandomKey(NumberEncryptor.GENERATOR_KEY_LENGTH);
        String test = KeyGenerator.generateRandomKey(NumberEncryptor.MAX_VALUE);//only numbers
        String enc = new NumberEncryptor(pk).encrypt(test);
        String dec = new NumberEncryptor(pk).decrypt(enc);
        assertEquals(test, dec);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void testLoadPrivateKeyByEncrypted() {
        NumberEncryptor ne = new NumberEncryptor();
        ne.loadPrivateKeyByEncrypted("C08849C9BB6A65000014I2BCED4BFB89D6C010415");
        String dec = ne.decrypt("C08849C9BB6A65000014I2BCED4BFB89D6C010415");
        String original = "741970466209881861999386007403";
        assertEquals(original, dec);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void testIsEquivalent() {
        NumberEncryptor ne = new NumberEncryptor();
        String enc0 = "C08849C9BB6A65000014I2BCED4BFB89D6C010415";
        ne.loadPrivateKeyByEncrypted(enc0);

        String test = KeyGenerator.generateRandomKey(NumberEncryptor.MAX_VALUE);//only numbers
        String enc1 = ne.encrypt(test);

        assertTrue(ne.isEquivalent(enc0, enc1));
    }

}