package com.atomatus.util.security;

import java.util.concurrent.ThreadLocalRandom;

public final class KeyGenerator {

	private static final int DEFAULT_LEN 	= 6;
	private static final int DEC_BOUND 		= 10;

	private KeyGenerator() { }

	public static String generateRandomKey(int len, int bound) {
		StringBuilder builder = new StringBuilder();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < len; i++) builder.append(random.nextInt(bound));
		return builder.toString();
	}

	public static String generateRandomKey(int len) {
		return generateRandomKey(len, DEC_BOUND);
	}

	public static String generateRandomKey() {
		return generateRandomKey(DEFAULT_LEN, DEC_BOUND);
	}

	public static String generateRandomKeyHex(int len) {
		return generateRandomKey(len, "0123456789ABCDEF");
	}

	public static String generateRandomKeyHex() {
		return generateRandomKeyHex(DEFAULT_LEN);
	}

	public static String generateRandomKeyAlpha(int len) {
		return generateRandomKey(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
	}

	public static String generateRandomKeyAlphaNumeric(int len) {
		return generateRandomKey(len, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	public static String generateRandomKeyAlpha() {
		return generateRandomKeyAlpha(DEFAULT_LEN);
	}

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