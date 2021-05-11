package com.atomatus.util.cache;

import java.io.IOException;

/**
 * Cache control for storing data.
 */
final class CacheControlStored extends CacheControlBase {

    protected CacheControlStored(int id) {
        super(id);
    }

    @Override
    protected void onAddCallback(CacheData cacheData) {
        try {
            cacheData.data().store();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected CacheData onGetCallback(CacheData cacheData) {
        return super.onGetCallback(cacheData);
    }

    @Override
    protected void onRemoveCallback(CacheData cacheData) {
        super.onRemoveCallback(cacheData);
    }
}
