package com.atomatus.util.cache;

import com.atomatus.util.ArrayHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <strong>Cache control</strong>
 * <p>
 *  Mechanism for multitasking and ciphered (encrypted) cache control for applications.<br>
 *  Allowing to be create as memory mode or stored mode, defining max time to alive and sync access.
 * </p>
 *     <ul>
 *         <li>
 *             <strong>Memory Mode</strong>
 *             <p>
 *                 <i>All cache data is stored in memory (ciphered).</i>
 *             </p>
 *             <br>
 *             <code>
 *                 <i>//to get or create global cache in memory, accessible for anyone app proccess.</i><br>
 *                 CacheControl.memory();<br><br>
 *                 <i>//to get or create isolated cache in memory, accessible only by who know cacheId.</i><br>
 *                 CacheControl.memory(cacheId);
 *             </code>
 *         </li>
 *         <li>
 *             <strong>Stored Mode</strong>
 *             <p>
 *                 <i>All cache data is stored in system disc as temp file (ciphered) and
 *                 deleted on system close.</i>
 *             </p>
 *             <br>
 *             <code>
 *                 <i>//to get or create global cache stored, accessible for anyone app proccess.</i><br>
 *                 CacheControl.stored();<br><br>
 *                 <i>//to get or create isolated cache stored, accessible only by who know cacheId.</i><br>
 *                 CacheControl.stored(cacheId);
 *             </code>
 *         </li>
 *         <li>
 *             <strong>Example how to store data in cache:</strong><br><br>
 *              <code>
 *                  CacheControl<br>
 *                      .memory()<br>
 *                      .maxAge(2L, TimeUnit.MINUTES)<i> //set or update max time to data keep cached.</i><br>
 *                      .add(new CacheData.Builder()<br>
 *                          .id(anyObjectTypeAsIdToRecoverData)<i> //if not set, will be generated on build.</i><br>
 *                          .text("Add text")<br>
 *                          .append("Append more text")<br>
 *                          .build());<br><br>
 *
 *                  <i>//attempt recover data cached</i><br>
 *                  CacheData data = CacheControl.memory().get(anyObjectTypeAsIdToRecoverData);<br><br>
 *
 *                  data.exists();<i> //check exists before access, may be expired (see max time above).</i><br>
 *                  data.text();<i> //read cached data as text; [or]</i><br>
 *                  data.bytes();<i> //read cached data as byte array; [or]</i><br>
 *                  data.stream();<i> //read cached data as stream.</i><br>
 *              </code>
 *         </li>
 *     </ul>
 * @author Carlos Matos
 */
public abstract class CacheControl {

    //region cache instance
    private enum CacheTypes {
        MEMORY,
        STORED
    }

    private static final Map<Integer, CacheControl> caches;
    private static final Object globalHashCode;
    private static final int DEFAULT_MAX_AGE_IN_MILLIS;

    private synchronized static CacheControl cache(Object id,
                                                   CacheTypes type,
                                                   ArrayHelper.Function<Integer, CacheControl> newCacheControlFun) {
        if(id == null) {
            throw new NullPointerException("Invalid id!");
        } else if(type == null) {
            throw new NullPointerException("Invalid type!");
        }

        int hash = Objects.hash(id, type);
        CacheControl cache = caches.get(hash);

        if(cache == null) {
            CacheControl res = caches.put(hash, cache = newCacheControlFun.apply(hash));
            assert res == null;
        }

        return cache;
    }

    /**
     * Get or create a cache control instance for memory mode.<br>
     * <i>Warning: This instance of cache may be accessible for all thread of your app proccesses,
     * if you want to create an isolated cache control use {@link #memory(Object)} with an isolated id.</i>
     * @return cache control in memory mode.
     */
    public static CacheControl memory() {
        return memory(globalHashCode);
    }

    /**
     * Get or create a cache control instance for stored mode (creates temp files during proccess execution, and deleted all when close id).<br>
     * <i>Warning: This instance of cache may be accessible for all thread of your app proccesses,
     * if you want to create an isolated cache control use {@link #stored(Object)} with an isolated id.</i>
     * @return cache control in stored mode.
     */
    public static CacheControl stored() {
        return stored(globalHashCode);
    }

    /**
     * Get or create a cache control instance for memory mode for isolated access by id.
     * @param id secure cache id.
     * @return cache control in memory mode.
     */
    public static CacheControl memory(Object id) {
        return cache(id, CacheTypes.MEMORY, CacheControlMemory::new);
    }

    /**
     * Get or create a cache control instance for stored mode (creates temp files during proccess execution, and deleted all when close id)
     * for isolated access by id.
     * @param id secure cache id.
     * @return cache control in stored mode.
     */
    public static CacheControl stored(Object id) {
        return cache(id, CacheTypes.STORED, CacheControlStored::new);
    }
    //endregion

    /**
     * Cache control id.
     */
    protected final int id;

    /**
     * Cache max age in milliseconds
     */
    private final AtomicLong maxAgeInMillis;

    static {
        caches = new HashMap<>();
        globalHashCode = new Object();
        DEFAULT_MAX_AGE_IN_MILLIS = 60 * 60 * 1000;//1h
    }

    /**
     * Constructs cache control by id
     * @param id cache control id.
     */
    protected CacheControl(int id) {
        this.id = id;
        this.maxAgeInMillis = new AtomicLong(DEFAULT_MAX_AGE_IN_MILLIS);
    }

    //region max age
    /**
     * <p>Se max age default for cache data.</p>
     * <i>Warning: This option will be applied if not defined it on CacheData.</i>
     * @param maxAge max age time
     * @param timeUnit time unit
     * @return current instance.
     */
    public synchronized CacheControl maxAge(long maxAge, TimeUnit timeUnit) {
        if(maxAge > 0L) maxAgeInMillis.set(timeUnit.toMillis(maxAge));
        return this;
    }

    /**
     * Get current max age default in milliseconds.
     * @return max age in milliseconds.
     */
    protected final long getMaxAgeInMillis(){
        return maxAgeInMillis.get();
    }
    //endregion

    //region abstracts

    /**
     * Check whether exists a cache data for target id.
     * @param hash cache data id.
     * @return true, exists a cache data stored for target id, otherwise false.
     */
    public abstract boolean exists(Object hash);

    /**
     * Attempt to recover cache data for target id.<br>
     * <i>Warning: Always check {@link CacheData#exists()} before attempt to read content.</i>
     * @param hash cache data id.
     * @return cache data stored and bonded by id, oterwhise empty cache data that
     * always will return false for {@link CacheData#exists()}.
     */
    public abstract CacheData get(Object hash);

    /**
     * Add or update cache data boned by id.
     * @param data cache data.
     */
    public abstract void add(CacheData data);

    /**
     * Attempt to remove cache data for target id.
     * @param hash cache data id.
     * @return true, cache data removed, otherwise false.
     */
    public abstract boolean remove(Object hash);

    /**
     * Attempt to remove cache data.
     * @param data cache data target.
     * @return true, cache data removed, otherwise false.
     */
    public abstract boolean remove(CacheData data);

    /**
     * Clear all data cached.
     */
    public abstract void clear();

    /**
     * Force attempt to recycle all cache
     * data expired (reached max age time defined in {@link #maxAge(long, TimeUnit)}).
     */
    public abstract void recycle();
    //endregion
}
