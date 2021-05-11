package com.atomatus.util.cache;

import com.atomatus.util.security.SensitiveBytes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * <strong>Cache data container.</strong>
 * <p>
 *     Cache data container to binding data and id on cache control.
 * </p>
 * @author Carlos Matos
 */
public abstract class CacheData {

    /**
     * Build a cache data container to binding data and id on cache control.
     */
    public static class Builder {

        /**
         * Cache data id.
         */
        protected transient Object id;

        /**
         * Cache data.
         */
        protected transient SensitiveBytes data;

        /**
         * Cache data hash id.
         * @param id cache data id.
         * @return current builder.
         */
        public Builder id(Object id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        /**
         * Set cache data.
         * @param data cached data.
         * @return current builder.
         */
        public Builder bytes(byte[] data) {
            this.data = SensitiveBytes.of(data);
            return this;
        }

        /**
         * Set cache data.
         * @param data cached data.
         * @return current builder.
         */
        public Builder text(String data) {
            this.data = SensitiveBytes.of(data);
            return this;
        }

        /**
         * Append cache data as bytes.
         * @param data cached data to be append.
         * @return current builder.
         */
        public Builder append(byte[] data) {
            this.data = this.data == null ?
                    SensitiveBytes.of(data) :
                    this.data.append(data);
            return this;
        }

        /**
         * Append cache data as bytes.
         * @param data cached data to be append.
         * @return current builder.
         */
        public Builder append(String data) {
            this.data = data == null ? this.data :
                    this.data == null ? SensitiveBytes.of(data) :
                     this.data.append(data.getBytes());
            return this;
        }

        /**
         * Build a new cache data
         * @return cache data built.
         */
        public CacheData build() {
            try {

                if(id == null) {
                    id = new Object();
                }

                return new CacheDataImpl(this);

            } finally {
                this.id = null;
                this.data = null;
            }
        }
    }

    /**
     * Sensitive bytes ciphered stored in memory or disc.
     * @return sensitive bytes
     */
    protected abstract SensitiveBytes data();

    /**
     * Cache data hash id.
     * @return hash id.
     */
    protected abstract int hash();

    /**
     * Calculate cache data max age to live.
     * @param maxAgeInMillis max age in millis.
     * @return current cache data.
     */
    protected abstract CacheData maxAge(long maxAgeInMillis);

    /**
     * Check whether current cache data is expired (reached max age time to live).
     * @return true, current cache data reached max age time to live, otherwise false.
     */
    protected abstract boolean isExpired();

    /**
     * Check whether current cache data contains data and exists on cache control.
     * @return true, exists and contains data, otherwise false.
     */
    public abstract boolean exists();

    /**
     * Current cache data is keeping in memory mode.
     * @return true, cache data is in memory mode, otherwhise false.
     */
    public abstract boolean isMemory();

    /**
     * Current cache data is keeping in stored disc (temp files) mode.
     * @return true, cache data is in stored mode, otherwhise false.
     */
    public abstract boolean isStored();

    /**
     * Open a stream to consume cache data, if in stored mode, keeps data in memory only until read.
     * @return input stream to consume data.
     * @throws IOException throws when is not possible access data in disc for stored mode.
     */
    public abstract InputStream stream() throws IOException;

    /**
     * Read all cached content as string.
     * @return content as string.
     * @throws IOException throws when is not possible access data in disc for stored mode.
     */
    public abstract String text() throws IOException;

    /**
     * Read all cached content as byte array.
     * @return content as string.
     * @throws IOException throws when is not possible access data in disc for stored mode.
     */
    public abstract byte[] bytes() throws IOException;

    /**
     * Clear cached data.
     */
    protected abstract void clear();
}
