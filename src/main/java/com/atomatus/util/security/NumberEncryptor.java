package com.atomatus.util.security;

import com.atomatus.util.StringUtils;
import com.atomatus.util.Debug;

import java.util.Arrays;
import java.util.Random;

/**
 * Simple Encryptor for number characters.
 */
class NumberEncryptor extends Encryptor {

	/**
	 * Generate a random key for number encryptor.
	 * @return random key.
	 */
	public static String generateKey() {
		return KeyGenerator.generateRandomKey(KEYSIZE);
	}

	public final static int COUNT					= 3;
	public final static int GENERATOR_KEY_LENGTH	= 4;
	public final static int MAX_VALUE				= 30;

	private final static int MIN_SEP_CHAR 			= 71;
	private final static int MAX_SEP_CHAR 			= 90;

	private final static int KEYSIZE				= 4;

	private String privateKey;
	private final boolean hasLog;

	{
		hasLog = Debug.isDebugMode();
	}

	/**
	 * Constructor/
	 * @param privateKey numeric private key
	 * @throws IllegalArgumentException throws when private key is not numeric or length is different of  GENERATOR_KEY_LENGTH.
	 */
	public NumberEncryptor(String privateKey) {
		this.privateKey	= requireValidPrivateKey(privateKey);
	}

	public NumberEncryptor() {
		this("1143");//never change it.
	}

	private String requireValidPrivateKey(String privateKey) {
		StringUtils.requireNonNullOrEmpty(privateKey);
		requireOnlyDigits(privateKey);

		if(privateKey.length() != GENERATOR_KEY_LENGTH){
			throw new IllegalArgumentException("Private key length is different of "+GENERATOR_KEY_LENGTH+" digits.");
		}
		return privateKey;
	}

	protected void requireOnlyDigits(String str){
		if(str.replaceAll("[0-9]", "").length() > 0){
			throw new IllegalArgumentException("Use only digits!");
		}
	}

	/**
	 * Add leading zeros to leave a value with even number of digits.
	 * @param original target
	 * @return target non even, return leading zeros, otherwise, return self.
	 */
	private String prepare(String original) {
		StringUtils.requireNonNullOrWhitespace(original);
		requireOnlyDigits(original);
		StringBuilder originalBuilder = new StringBuilder(original);
		while((originalBuilder.length() % 2) != 0){
			originalBuilder.insert(0, "0");
		}
		return originalBuilder.toString();
	}

	/**
	 * Reverse characters to even block positions.
	 * @param block1 target block one
	 * @param block2 target block two
	 * @return a array with reversed block values.
	 */
	private String[] invertPairCharacters(String block1, String block2) {
		if(block1.length() != block2.length()){
			throw new IllegalArgumentException("Diff blocks lengths!");
		}
		
		String aux1	= block1;
		String aux2	= block2;
			
		for(int i=0; i < block1.length(); i+=2){
			aux1 = aux1.substring(0, i) + block2.charAt(i) + aux1.substring(i + 1);
			aux2 = aux2.substring(0, i) + block1.charAt(i) + aux2.substring(i + 1);
		}
		
		return new String[]{aux1, aux2};
	}

	private int untilLength(String block){
		int p = -1;
		char[] var	= block.toCharArray();
		for(int i=0; i < var.length; i++){
			if(Character.isDigit(var[i])){
				p = i+1;
			}
			else{
				return p;
			}
		}
		return p;
	}
		
	/**
	 * Convert numeric sequence of block to hex.
	 * @param block  target.
	 * @param length target length.
	 * @return hex value or null
	 */
	private String convertToHex(String block, int length){
		return length < 1 ? null : Long.toHexString(Long.parseLong(block.substring(0, length)));
	}
	
	/**
	 * Convert hex sequence of block to decimal.
	 * @param block  target.
	 * @param length target length.
	 * @return decimal value or null
	 */
	private String convertToDecimal(String block, int length){
		return length < 1 ? null : String.valueOf(Long.parseLong(block.substring(0, length), 16));
	}
	
	/**
	 * Generate separator char.
	 * @return a character to separator and identify blocks
	 */
	private String generateSeparatorChar(){
		int ascii	= new Random().nextInt(MAX_SEP_CHAR - MIN_SEP_CHAR) + MIN_SEP_CHAR;
		return Character.toString((char)ascii);
	}
	
	/**
	 * Get all candidates separator chars.
	 * @return an array with candidates separator chars.
	 */
	private String[] candidatesSeparatorChar(){
		String[] var = new String[(MAX_SEP_CHAR - MIN_SEP_CHAR) + 1];
		for(int i=0; i < var.length; i++){
			var[i] = Character.toString((char)(i + MIN_SEP_CHAR));
		}
		return var;
	}

	private String concatArray(String[] var) {
		StringBuilder builder = new StringBuilder();
		for(String s : var) {
		    builder.append(s);
		}
		return builder.toString();
	}

	private String[] convertedBlock(String block)  {
		
		String[] var	= new String[4];
		

		if(hasLog){
			System.out.println("\nConverting Block ("+block+")");
		}
		
		for(int i=0; i < COUNT; i++){
			String str				= block; //for log only.
			int j					= this.untilLength(block);
			String aux				= this.convertToHex(block, j);
			block 					= aux == null ? block	: aux+block.substring(j);
			var[var.length-(i+1)] 	= this.prepare(aux == null ? "0" : String.valueOf(aux.length()));
			
			if(hasLog){
				System.out.println("-> get until length "+(Math.max(j, 0))+" ("+(j < 1 ? "" : str.substring(0, j))+") > "+block);
			}
		}
		var[0] = block;
		return var;
	}

	private String[] generateBlocks(String encrypted){
		return encrypted.split(Arrays.toString(this.candidatesSeparatorChar()));
	}

	private String decryptBlock(String[] var){
		for(int i=0; i < COUNT; i++){
			int length = Integer.parseInt(var[i+1]);
			String aux = convertToDecimal(var[0], length);
			var[0] = aux == null ? var[0] : aux + var[0].substring(length);
		}
		return var[0];
	}

	private String[] toArray(String block){
		return new String[]{block.substring(0, block.length()-6),
				block.substring(block.length()-6, block.length()-4),
				block.substring(block.length()-4, block.length()-2),
				block.substring(block.length()-2)};
	}

	/**
	 * Decrypt value and return array length equals 2. Decrypted value and private key.
	 * @param encrypted encrypted value
	 * @return array with result
	 */
	private String[] decryptValue(String encrypted) {
		String[] var = this.generateBlocks(StringUtils.requireNonNullOrWhitespace(encrypted));
		StringBuilder block1	= new StringBuilder(this.decryptBlock(this.toArray(var[0])));
		StringBuilder block2	= new StringBuilder(this.decryptBlock(this.toArray(var[1])));
		
		while(block1.length() < block2.length()){
			block1.insert(0, "0");
		}
		
		while(block2.length() < block1.length()){
			block2.insert(0, "0");
		}
		
		var	= invertPairCharacters(block1.toString(), block2.toString());
		block1 = new StringBuilder(var[0]);
		block2 = new StringBuilder(new StringBuilder(var[1]).reverse().toString());
		
		String result 	= block1.toString().concat(block2.toString());

		return new String[]{result.substring(0, result.length()-GENERATOR_KEY_LENGTH), 
				result.substring(result.length()-GENERATOR_KEY_LENGTH)};
	}

	//region Encryptor
	/**
	 * Load a privateKey through an encrypted value.
	 * @param encrypted new private key encrypted.
	 */
	public void loadPrivateKeyByEncrypted(String encrypted) {
		StringUtils.requireNonNullOrWhitespace(encrypted);
		this.privateKey = requireValidPrivateKey(this.decryptValue(encrypted)[1]);
	}
	
	/**
	 * Check if input encrypted values were generates from same private key.
	 * @param encrypted1 first target
	 * @param encrypted2 second target
	 * @return true when equivalent, otherwise, false.
	 */
	public boolean isEquivalent(String encrypted1, String encrypted2) {
		return this.decryptValue(encrypted1)[1].equals(this.decryptValue(encrypted2)[1]);
	}
	
	public String encrypt(String original) {
		if(StringUtils.requireNonNullOrWhitespace(original).length() > MAX_VALUE) {
			throw new IllegalArgumentException("Encrypt limited to " + MAX_VALUE + " digits!");
		}
		
		original		= this.prepare(original+privateKey);
	
		String block1	= original.substring(0, original.length()/2);
		String block2	= new StringBuilder(original.substring(original.length()/2)).reverse().toString();
		
		String[] var = this.invertPairCharacters(block1, block2);

		if(hasLog) {
			System.out.println("Private Key:\t\t\t"+privateKey);
			System.out.println("Prepared (Zeros in the left):\t"+original);
			System.out.println("\nDivide in Half");
			System.out.println("Block1:\t\t\t\t"+block1);
			System.out.println("Block2 (reverse):\t\t"+block2);
			System.out.println("\nInverted Pair Positions");
			System.out.println("Block1:\t\t\t\t"+var[0]);
			System.out.println("Block2:\t\t\t\t"+var[1]);
		}
		
		block1			= var[0];
		block2			= var[1];
				
		String result = concatArray(convertedBlock(block1)).
						concat(generateSeparatorChar()).
						concat(concatArray(convertedBlock(block2))).
						toUpperCase();
		
		if(hasLog){
			System.out.println("\nEncrypted: " + result);
		}
		
		return result;
	}

	@Override
	public byte[] encrypt(byte[] original) {
		return this.encrypt(new String(original)).getBytes();
	}

	@Override
	public byte[] encrypt(byte[] original, int offset, int len) {
		return this.encrypt(new String(original, offset, len)).getBytes();
	}

	public String decrypt(String encrypted) {
		return this.decryptValue(encrypted)[0];
	}

	@Override
	public byte[] decrypt(byte[] encrypted) {
		return this.decrypt(new String(encrypted)).getBytes();
	}

	@Override
	public byte[] decrypt(byte[] encrypted, int offset, int len) {
		return this.decrypt(new String(encrypted, offset, len)).getBytes();
	}

	//endregion

}