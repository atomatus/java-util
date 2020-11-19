package com.atomatus.util.security;

import com.atomatus.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cipher (DESede/CBC/PKCS5Paddin) Encryptor.
 * @see Cipher
 * @author Carlos Matos
 */
@SuppressWarnings("unused")
final class Cipher3DES extends Encryptor {

    private final SecretKeySpec chave;
    private final IvParameterSpec iv;
    private final Cipher cifrador;

    /**
     * Constructor with key and initialization vector.
     * @param key private key (21 bytes)
     * @param initializationVector initialization vector (8 bytes).
     */
    public Cipher3DES(String key, String initializationVector) {
        try{
	        this.cifrador    = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	        this.chave       = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "DESede");
	        this.iv          = new IvParameterSpec(initializationVector.getBytes());
        }catch(NoSuchAlgorithmException | NoSuchPaddingException ex){
        	throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Construtor with default initialization vector.
     * @param key private key (21 bytes)
     */
    public Cipher3DES(String key) {
    	this(key, "01234567");
    }

    Cipher3DES(){
        this("4t0m4tu5515t3m5181120");//never change it.
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
	        plaintext = original.getBytes(StandardCharsets.UTF_8);
	        cifrador.init(Cipher.ENCRYPT_MODE, chave, iv);
	        cipherText = cifrador.doFinal(plaintext);
        }catch(Throwable ex){
        	throw new RuntimeException(ex);
        }
	        
        return new String(Base64.getEncoder().encode(cipherText));
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
        
}