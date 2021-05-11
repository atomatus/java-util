package com.atomatus.util.cache;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache control base rules defining callbacks for each state change.
 */
abstract class CacheControlBase extends CacheControl {

    private final ConcurrentMap<Integer, CacheData> cache;

    protected CacheControlBase(int id) {
        super(id);
        this.cache = new ConcurrentHashMap<>();
    }

    //region callbacks
    protected void onAddCallback(CacheData cacheData) { }

    protected CacheData onGetCallback(CacheData cacheData) {
        return cacheData;
    }

    protected void onRemoveCallback(CacheData cacheData)  { }
    //endregion

    //region CacheControl
    @Override
    public final boolean exists(Object hash) {
        return get(hash).exists();
    }

    @Override
    public final CacheData get(Object hash) {
        CacheData found = cache.get(Objects.requireNonNull(hash).hashCode());

        if(found == null) {
            return CacheDataImpl.empty;
        } else if(found.isExpired()) {
            recycle();
            return CacheDataImpl.empty;
        }

        return onGetCallback(found);
    }

    @Override
    public final void add(CacheData data) {
        CacheData old = cache.put(data.hash(), data.maxAge(getMaxAgeInMillis()));
        if(old != data) {
            if(old != null) {
                onRemoveCallback(old);
                old.clear();
            }
            onAddCallback(data);
        }
    }

    @Override
    public final boolean remove(Object hash) {
        CacheData cd = cache.remove(Objects.requireNonNull(hash).hashCode());
        boolean success = cd != null;
        if(success) {
            onRemoveCallback(cd);
            cd.clear();
        }
        return success;
    }

    @Override
    public final boolean remove(CacheData data) {
        CacheData cd = cache.get(Objects.requireNonNull(data).hash());
        boolean success = cd == data || cd.hash() == data.hash();
        if(success && cache.remove(cd.hash()) == cd) {
            onRemoveCallback(cd);
            cd.clear();
        }
        return success;
    }

    @Override
    public final void clear() {
        new Thread(this::clearInternal).start();
    }

    @Override
    public final void recycle() {
        new Thread(this::recycleInternal).start();
    }

    private synchronized void clearInternal() {
        Collection<CacheData> col = cache.values();
        cache.clear();
        for (CacheData cd : col) cd.clear();
    }

    private synchronized void recycleInternal() {
        Collection<CacheData> col = cache.values();
        for (CacheData cd : col) {
            if(cd.isExpired()) {
                CacheData rm = cache.remove(cd.hash());
                if(rm != null && rm != cd && !rm.isExpired()) {
                    cache.put(rm.hash(), rm);
                }
            }
        }
    }
    //endregion
}
