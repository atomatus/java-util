package com.atomatus.util.cache;

import com.atomatus.util.security.SensitiveBytes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

final class CacheDataImpl extends CacheData {

    //region fields
    protected static final CacheData empty;

    private final long createdAt;
    private long limitedAt;
    private boolean expired;
    private int hash;
    private SensitiveBytes data;
    //endregion

    //region constructs
    static {
        empty = new CacheDataImpl();
    }

    {
        this.createdAt = this.limitedAt = System.currentTimeMillis();
    }

    protected CacheDataImpl(Builder builder) {
        this.hash   = builder.id.hashCode();
        this.data   = builder.data;
        new WeakReference<>(data);//to gc if no more refering.
    }

    private CacheDataImpl() { }
    //endregion

    //region cacheData
    protected final void requireExists() {
        if(data == null) {
            throw new UnsupportedOperationException("Cache does not contains no one data!");
        }
    }

    @Override
    protected SensitiveBytes data() {
        return data;
    }

    @Override
    protected int hash() {
        requireExists();
        return hash;
    }

    @Override
    protected CacheData maxAge(long maxAgeInMillis) {
        limitedAt = createdAt + maxAgeInMillis;
        return this;
    }

    @Override
    protected boolean isExpired() {
        return expired || (expired = limitedAt < System.currentTimeMillis());
    }

    @Override
    public boolean exists() {
        return data != null;
    }

    @Override
    public boolean isMemory() {
        return data != null && !data.isStored();
    }

    @Override
    public boolean isStored() {
        return data != null && data.isStored();
    }

    @Override
    public InputStream stream() throws IOException {
        requireExists();
        return data.isStored() ?
                data.streamStored() :
                data.stream();
    }

    @Override
    public String text() throws IOException {
        return new String(bytes());
    }

    @Override
    public byte[] bytes() throws IOException {
        requireExists();
        return data.isStored() ? data.peekStored() : data.readAll();
    }

    @Override
    protected void clear() {
        try {
            if(data != null) data.destroy();
        } catch (Exception ignored) { }
        finally {
            data = null;
            hash = 0;
        }
    }
    //endregion

    //region equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheDataImpl cacheData = (CacheDataImpl) o;
        return hash == cacheData.hash && Objects.equals(data, cacheData.data);
    }

    @Override
    public int hashCode() {
        return hash;
    }
    //endregion
}
