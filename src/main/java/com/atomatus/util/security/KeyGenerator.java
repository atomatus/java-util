package com.atomatus.util.security;

import java.util.Random;

public class KeyGenerator {

	private static final int DEFAULT_LEN 	= 6;
	private static final int DEC_BOUND 		= 10;
	private static final int HEX_BOUND 		= 16;

	public static String generateRandomKey(int len, int bound) {
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			builder.append(random.nextInt(bound));
		}

		return builder.toString();
	}

	public static String generateRandomKey(int len) {
		return generateRandomKey(len, DEC_BOUND);
	}

	public static String generateRandomKey() {
		return generateRandomKey(DEFAULT_LEN, DEC_BOUND);
	}

	public static String generateRandomKeyHex(int len) {
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			builder.append(Integer.toHexString(random.nextInt(HEX_BOUND)));
		}
		return builder.toString();
	}

	public static String generateRandomKeyHex() {
		return generateRandomKeyHex(DEFAULT_LEN);
	}
}
