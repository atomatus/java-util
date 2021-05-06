package com.atomatus.util.security;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <strong>Key Generator</strong><br/>
 * <p>
 *     Final class within static methods to help to generate
 *     random keys by length and type.
 * </p>
 */
public final class KeyGenerator {

	private static final int DEFAULT_LEN 	= 6;
	private static final int DEC_BOUND 		= 10;

	private KeyGenerator() { }

	/**
	 * Generate a random key.
	 * @param len key length
	 * @param bound the upper bound (exclusive). Must be positive.
	 * @return generated key.
	 */
	public static String generateRandomKey(int len, int bound) {
		StringBuilder builder = new StringBuilder();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < len; i++) builder.append(random.nextInt(bound));
		return builder.toString();
	}

	/**
	 * Generate a random key.
	 * @param len key length
	 * @return generated key.
	 */
	public static String generateRandomKey(int len) {
		return generateRandomKey(len, DEC_BOUND);
	}

	/**
	 * Generate a random key using default length and default decimal bounds.
	 * @return generated key.
	 */
	public static String generateRandomKey() {
		return generateRandomKey(DEFAULT_LEN, DEC_BOUND);
	}

	/**
	 * Generate a random key using default decimal bounds.
	 * @param len key length
	 * @return generated key.
	 */
	public static String generateRandomKeyHex(int len) {
		return generateRandomKey(len, "0123456789ABCDEF");
	}

	/**
	 * Generate a random hexdecimal key using default hexdecimal length.
	 * @return generated key.
	 */
	public static String generateRandomKeyHex() {
		return generateRandomKeyHex(DEFAULT_LEN);
	}

	/**
	 * Generate a random Alphanumeric key using default alphanumeric bounds.
	 * @param len key length
	 * @return generated key.
	 */
	public static String generateRandomKeyAlpha(int len) {
		return generateRandomKey(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
	}

	/**
	 * Generate a random Alphanumeric key using default alphanumeric bounds.
	 * @param len key length
	 * @return generated key.
	 */
	public static String generateRandomKeyAlphaNumeric(int len) {
		return generateRandomKey(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	/**
	 * Generate a random Alphanumeric key using default length and alphanumeric bounds.
	 * @return generated key.
	 */
	public static String generateRandomKeyAlpha() {
		return generateRandomKeyAlpha(DEFAULT_LEN);
	}

	/**
	 * Generate a random Alphanumeric key using default length and alphanumeric bounds.
	 * @return generated key.
	 */
	public static String generateRandomKeyAlphaNumeric() {
		return generateRandomKeyAlphaNumeric(DEFAULT_LEN);
	}

	private static String generateRandomKey(int len, String args) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		char[] arr = new char[len];
		for (int i = 0, j = args.length(); i < len; i++) {
			arr[i] = args.charAt(random.nextInt(j));
		}
		return new String(arr);
	}
}