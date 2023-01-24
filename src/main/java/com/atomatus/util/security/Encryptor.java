package com.atomatus.util.security;

import com.atomatus.util.StringUtils;

import java.util.Objects;

/**
 * <strong>Encryptor</strong><br>
 * <i>Construct an encryptor using Builder mode.</i><br>
 * <p>
 * Request builder from method {@link Encryptor#builder()},
 * choose a type ({@link Encryptor.Builder#type(Encryptor.Type)}) and might set a private key ({@link Encryptor.Builder#key(String)}),
 * then call {@link Encryptor.Builder#build()}} to build encryptor.
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
 * @author Carlos Matos {@literal @chcmatos}
 */
public abstract class Encryptor {

	//region Builder
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

	/**
	 * Encryptor builder for vector type.
	 */
	public static class BuilderVector extends Builder {

		private String vector;

		/**
		 * Constructs from other builder
		 * @param other copy data from.
		 */
		protected BuilderVector(Builder other) {
			super(other);
		}

		/**
		 * Private key (21 bytes).
		 * @param key hash key
		 * @return current builder.
		 */
		@Override
		public BuilderVector key(String key) {
			super.key(key);
			return this;
		}

		/**
		 * Generate a private key (21 bytes).
		 * @return current builder.
		 */
		public BuilderVector key() {
			super.key();
			return this;
		}

		/**
		 * Generate an encryptor initialization vector (8 bytes).
		 * @param vector vector in 8 bytes.
		 * @return current builder.
		 */
		public BuilderVector vector(String vector) {
			this.vector = vector;
			return this;
		}

		/**
		 * Encryptor initialization vector (8 bytes).
		 * @return current builder.
		 */
		public BuilderVector vector() {
			return this.vector(Cipher3DES.generateIv());
		}

		@Override
		public Encryptor build() {
			try {
				return vector == null ?
						new Cipher3DES(key) :
						new Cipher3DES(key, vector);
			} finally {
				this.dispose();
			}
		}
	}

	/**
	 * Encryptor builder.
	 */
	public static class Builder {

		/**
		 * Encrypt type.
		 */
		protected Type type;

		/**
		 * Encrypt key.
		 */
		protected String key;

		private boolean disposed;

		/**
		 * Constructs copying from other builder.
		 * @param other copy from.
		 */
		protected Builder(Builder other) {
			this.type = other.type;
			this.key = other.key;
			this.disposed = other.disposed;
		}

		/**
		 * Constructs default builder.
		 */
		public Builder() { }

		private void requireNonDisposed(){
			if(disposed) {
				throw new UnsupportedOperationException("Builder was disposed!");
			}
		}

		/**
		 * Encrypt type
		 * @param type target type
		 * @return current builder.
		 */
		public Builder type(Type type) {
			this.type = Objects.requireNonNull(type);
			return this;
		}

		/**
		 * Encrypt hash key
		 * @param key hash key
		 * @return current builder
		 */
		public Builder key(String key) {
			this.key = StringUtils.requireNonNullOrWhitespace(key);
			return this;
		}

		/**
		 * Generate an encrypt hash key by type.
		 * @return current builder
		 */
		public Builder key() {
			switch (type) {
				case CIPHER:
					this.key = Cipher3DES.generateKey();
					break;
				case NUMBER:
					this.key = NumberEncryptor.generateKey();
					break;
				case NUMERIC_MATRIX:
					this.key = NumericMatrixEncryptor.generateKey();
					break;
				default:
					throw new UnsupportedOperationException("Can not generate " +
							"a key for \"" + type + "\"!");
			}
			return this;
		}

		/**
		 * Encrypt as cipher3DES type.
		 * @return builder for cipher.
		 */
		public BuilderVector cipher() {
			try {
				return new BuilderVector(this.type(Type.CIPHER));
			} finally {
				this.dispose();
			}
		}

		/**
		 * Encrypt as number type.
		 * @return builder for number.
		 */
		public Builder number() {
			return this.type(Type.NUMBER);
		}

		/**
		 * Encrypt as numeric matrix type.
		 * @return builder for numeric matrix.
		 */
		public Builder numericMatrix() {
			return this.type(Type.NUMERIC_MATRIX);
		}

		/**
		 * Build encrypt from parameters
		 * @return encrypt instance.
		 */
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
				this.dispose();
			}
		}

		/**
		 * Dispose builder.
		 */
		protected void dispose() {
			type = null;
			key = null;
			disposed = true;
		}
	}

	/**
	 * Builder instance
	 * @return builder instance
	 */
	public static Builder builder(){
		return new Builder();
	}
	//endregion

	//region encrypt
	/**
	 * Encrypt target content
	 * @param original target content.
	 * @return encrypted value.
	 */
	public abstract String encrypt(String original);

	/**
	 * Encrypt target content
	 * @param original target content.
	 * @return encrypted value.
	 */
	public abstract byte[] encrypt(byte[] original);

	/**
	 * Encrypt target content
	 * @param original target content.
	 * @param offset array offset index.
	 * @param len count element to read.
	 * @return encrypted value.
	 */
	public abstract byte[] encrypt(byte[] original, int offset, int len);
	//endregion

	//region decrypt
	/**
	 * Descrypted target value.
	 * @param encrypted target value
	 * @return original value.
	 */
	public abstract String decrypt(String encrypted);

	/**
	 * Descrypted target value.
	 * @param encrypted target value
	 * @return original value.
	 */
	public abstract byte[] decrypt(byte[] encrypted);

	/**
	 * Descrypted target value.
	 * @param encrypted target value
	 * @param offset array offset index.
	 * @param len count element to read.
	 * @return original value.
	 */
	public abstract byte[] decrypt(byte[] encrypted, int offset, int len);
	//endregion

	//region behavior
	/**
	 * Load private key from encrypted value to be used to encrypt data.
	 * @param encrypted encrypted value.
	 */
	public void loadPrivateKeyByEncrypted(String encrypted) {
		throw new UnsupportedOperationException("Nao suportado por esse tipo de criptografia!");
	}

	/**
	 * Check is booth encrypted values was generated from same encryptor and key.
	 * @param encrypted1 encrypted value
	 * @param encrypted2 other encrypted value
	 * @return true, both as equivalent same origin, otherwise false.
	 */
	public boolean isEquivalent(String encrypted1, String encrypted2) {
		throw new UnsupportedOperationException("Nao suportado por esse tipo de criptografia!");
	}
	//endregion
}