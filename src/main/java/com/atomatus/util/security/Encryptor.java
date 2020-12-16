package com.atomatus.util.security;

import com.atomatus.util.StringUtils;

import java.util.Objects;

/**
 * Encryptor.<br>
 * Construct an encryptor using Builder mode.
 * <p>
 * Request builder from method {@link Encryptor#builder()},
 * choose a type ({@link Builder#type(Type)}) and might set a private key ({@link Builder#key(String)}),
 * then call {@link Builder#build()}} to build encryptor.
 * </p>
 * <i>Obs.: choose type is required, but if do not chosse a private key, a default key will be used for type.</i>
 * <p>Example:</p>
 * <pre>
 *     <code>
 *         Encryptor e = Encryptor.builder()
 *         		.type(Type.CIPHER)
 *         		.key("01234567890QWERTYUIOP1")
 *         		.build();
 *     </code>
 *     <code>
 *         Encryptor e = Encryptor.builder()
 *         		.type(Type.NUMBER)
 *         		.key("0123")
 *         		.build();
 *     </code>
 * </pre>
 * @author Carlos Matos
 */
public abstract class Encryptor {

	/**
	 * Encryptor types.
	 */
	public enum Type {
		/**
		 * Cipher (DESede/CBC/PKCS5Paddin).
		 */
		CIPHER,

		/**
		 * For numbers only.
		 */
		NUMBER,

		/**
		 * Like number but using inversed matrix.
		 */
		NUMERIC_MATRIX
	}

	public static class Builder {

		private Type type;
		private String key;
		private boolean disposed;

		private void requireNonDisposed(){
			if(disposed) {
				throw new UnsupportedOperationException("Builder was disposed!");
			}
		}

		public Builder type(Type type) {
			this.type = Objects.requireNonNull(type);
			return this;
		}

		public Builder key(String key) {
			this.key = StringUtils.requireNonNullOrWhitespace(key);
			return this;
		}

		public Encryptor build() {
			try {
				requireNonDisposed();
				boolean hasKey = key != null;
				switch (type) {
					case CIPHER:
						return hasKey ? new Cipher3DES(key) : new Cipher3DES();
					case NUMBER:
						return hasKey ? new NumberEncryptor(key) : new NumberEncryptor();
					case NUMERIC_MATRIX:
						return hasKey ? new NumericMatrixEncryptor(key) : new NumericMatrixEncryptor();
					default:
						throw new UnsupportedOperationException();
				}
			} finally {
				type = null;
				key = null;
				disposed = true;
			}
		}
	}

	public static Builder builder(){
		return new Builder();
	}

	public abstract String encrypt(String original);
	
	public abstract String decrypt(String encrypted);

	public void loadPrivateKeyByEncrypted(String encrypted) {
		throw new UnsupportedOperationException("Nao suportado por esse tipo de criptografia!");
	}

	public boolean isEquivalent(String encrypted1, String encrypted2) {
		throw new UnsupportedOperationException("Nao suportado por esse tipo de criptografia!");
	}
}