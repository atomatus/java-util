package com.atomatus.util.security;

import com.atomatus.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * Cipher (DESede/CBC/PKCS5Paddin) Encryptor.
 * @see Cipher
 * @author Carlos Matos
 */
@SuppressWarnings("unused")
final class Cipher3DES extends Encryptor {

    public static String generateKey() {
        return KeyGenerator.generateRandomKeyHex(KEYSIZE);
    }

    public static String generateIv(){
        return KeyGenerator.generateRandomKeyHex(IVSIZE);
    }

    public static final int KEYSIZE    = 8 * 3;
    public static final int IVSIZE     = 8;

    private static final String DESEDE  = "DESede";
    private static final String DESEDE_CBC_PKCS5PADDING = DESEDE + "/CBC/PKCS5Padding";

    protected static final String DEFAULT_IV;
    protected static final String DEFAULT_KEY;

    private final SecretKey chave;
    private final IvParameterSpec iv;
    private final Cipher cifrador;

    static {
        DEFAULT_KEY = generateKey();
        DEFAULT_IV = generateIv();
    }

    /**
     * Constructor with key and initialization vector.
     * @param key private key (24 bytes)
     * @param initializationVector initialization vector (8 bytes).
     */
    public Cipher3DES(byte[] key, byte[] initializationVector) {
        try{
	        this.cifrador    = Cipher.getInstance(DESEDE_CBC_PKCS5PADDING);
	        this.chave       = new SecretKeySpec(key, DESEDE);
	        this.iv          = new IvParameterSpec(initializationVector);
        }catch(NoSuchAlgorithmException | NoSuchPaddingException ex){
        	throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Constructor with key and initialization vector.
     * @param key private key (24 bytes)
     * @param initializationVector initialization vector (8 bytes).
     */
    public Cipher3DES(String key, String initializationVector) {
        this(key.getBytes(), initializationVector.getBytes());
    }

    /**
     * Construtor with default initialization vector.
     * @param key private key (24 bytes)
     */
    public Cipher3DES(String key) {
    	this(key, DEFAULT_IV);
    }

    Cipher3DES(){
        this(DEFAULT_KEY);
    }
    
    /**
     * Encrypt target text.
     * @param original target value.
     * @return encrypted value.
     * @throws RuntimeException throws when can no encrypt.
     */
    @Override
    public String encrypt(String original) {
        byte[] plaintext, cipherText;
        try{
	        plaintext = original.getBytes();
	        cifrador.init(Cipher.ENCRYPT_MODE, chave, iv);
	        cipherText = cifrador.doFinal(plaintext);
        }catch(Throwable ex){
        	throw new RuntimeException(ex);
        }
	        
        return new String(Base64.getEncoder().encode(cipherText));
    }

    /**
     * Encrypt target content
     * @param original target content.
     * @return encrypted value.
     */
    @Override
    public byte[] encrypt(byte[] original) {
        try{
            cifrador.init(Cipher.ENCRYPT_MODE, chave, iv);
            return cifrador.doFinal(original);
        } catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Encrypt target content
     * @param original target content.
     * @param offset array offset index.
     * @param len count element to read.
     * @return encrypted value.
     */
    @Override
    public byte[] encrypt(byte[] original, int offset, int len) {
        try{
            cifrador.init(Cipher.ENCRYPT_MODE, chave, iv);
            return cifrador.doFinal(original, offset, len);
        } catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Decrypt target encrypted text.
     * @param encrypted encrypted value.
     * @return descrypted value.
     * @throws RuntimeException throws when can no decrypt.
     */
    @Override
    public String decrypt(String encrypted) {
    	byte[] encBytes, plainTxtBytes;
    	
    	try{
	    	cifrador.init(Cipher.DECRYPT_MODE, chave, iv);  
	        encBytes = Base64.getDecoder().decode(encrypted);
	        plainTxtBytes = cifrador.doFinal(encBytes);  
    	} catch(Throwable ex){
    		throw new RuntimeException(ex);
    	 }
    	
        return new String(plainTxtBytes);  
    }

    /**
     * Descrypted target value.
     * @param encrypted target value
     * @return original value.
     */
    @Override
    public byte[] decrypt(byte[] encrypted) {
        try{
            cifrador.init(Cipher.DECRYPT_MODE, chave, iv);
            return cifrador.doFinal(encrypted);
        } catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] decrypt(byte[] encrypted, int offset, int len) {
        try{
            cifrador.init(Cipher.DECRYPT_MODE, chave, iv);
            return cifrador.doFinal(encrypted, offset, len);
        } catch(Throwable ex){
            throw new RuntimeException(ex);
        }
    }
}