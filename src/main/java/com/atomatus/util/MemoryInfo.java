package com.atomatus.util;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * System and App memory information.
 */
public final class MemoryInfo {

    /**
     * Amount of Memory type
     */
    public enum Amount {
        /**
         * Total amount of free memory available to the JVM
         */
        FREE,

        /**
         * Total memory currently in use by the JVM
         */
        TOTAL,

        /**
         * Maximum amount of memory the JVM will attempt to use.<br>
         * Obs.: This will return Long.MAX_VALUE if there is no preset limit
         */
        MAX,

        /**
         * Approximated amount of memory allocated.
         */
        ALLOCATED,

        /**
         * Approximated amount of memory which may be allocated until an out-of-memory
         * exception ({@link java.lang.OutOfMemoryError}) occurs.
         */
        PRESUMABLE_FREE
    }

    private long freeMemoryInBytes;
    private long totalMemoryInBytes;
    private long maxMemoryInBytes;
    private long allocatedMemoryInBytes;
    private long presumableFreeMemoryInBytes;

    private static WeakReference<MemoryInfo> instance;

    private static MemoryInfo buildDefault() {
        MemoryInfo mi = new MemoryInfo();
        mi.freeMemoryInBytes = Runtime.getRuntime().freeMemory();
        mi.totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        mi.maxMemoryInBytes = Runtime.getRuntime().maxMemory();
        mi.allocatedMemoryInBytes = mi.totalMemoryInBytes - mi.freeMemoryInBytes;
        mi.presumableFreeMemoryInBytes = mi.maxMemoryInBytes - mi.allocatedMemoryInBytes;
        return mi;
    }

    private static MemoryInfo buildAndroid(Object context) {
        Reflection rmi = Reflection.tryInflate("android.app.ActivityManager.MemoryInfo");
        if (rmi.inflated()) {
            Reflection rc = Reflection.tryCast(context, "android.content.Context");
            if (rc.inflated()) {
                rc.configDeflateAfterReturns()
                        .method("getSystemService", "activity")
                        .method("getMemoryInfo", (Object) rmi.value());

                MemoryInfo mi = new MemoryInfo();
                mi.freeMemoryInBytes = rmi.field("availMem").valueLong();
                mi.totalMemoryInBytes = rmi.field("totalMem").valueLong();
                mi.maxMemoryInBytes = rmi.field("totalMem").valueLong();
                mi.allocatedMemoryInBytes = mi.totalMemoryInBytes - mi.freeMemoryInBytes;
                mi.presumableFreeMemoryInBytes = mi.maxMemoryInBytes - mi.allocatedMemoryInBytes;
                rmi.deflate();
                return mi;
            } else {
                throw new ClassCastException("Object (" + context + ") is not an android.content.Context!");
            }
        }
        throw new UnsupportedOperationException("Was not possible create instance of android.app.ActivityManager.MemoryInfo");
    }

    /**
     * Instance of memory info by context.
     * @param context system context, usage for android Context.
     * @return instance of memory info.
     */
    public synchronized static MemoryInfo getInstance(Object context) {
        MemoryInfo mi;
        if(instance == null || (mi = instance.get()) == null) {
            instance = new WeakReference<>(mi = context != null && SystemInfo.getInstance().isAndroid() ?
                    buildAndroid(context) :
                    buildDefault());
        }
        return mi;
    }

    /**
     * Instance of memory info without context.<br/>
     * <i>Warning: If using in Android, use {@link #getInstance(Object)} passing Context of activity.</i>
     * @return instance of memory info.
     */
    public static MemoryInfo getInstance() {
        return getInstance(null);
    }

    private MemoryInfo() { }

    /**
     * Get memory info in bytes amount.
     * @param amount amount type.
     * @return amount value
     */
    public long getBytes(Amount amount) {
        switch (Objects.requireNonNull(amount)) {
            case FREE:
                return freeMemoryInBytes;
            case TOTAL:
                return totalMemoryInBytes;
            case MAX:
                return maxMemoryInBytes;
            case ALLOCATED:
                return allocatedMemoryInBytes;
            case PRESUMABLE_FREE:
                return presumableFreeMemoryInBytes;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Get memory info in KBytes amount.
     * @param amount amount type.
     * @return amount value
     */
    public long getKBytes(Amount amount) {
        return getBytes(amount) / 0x400;
    }

    /**
     * Get memory info in MBytes amount.
     * @param amount amount type.
     * @return amount value
     */
    public long getMBytes(Amount amount) {
        return getBytes(amount) / 0x100000;
    }

    /**
     * Get memory info in GBytes amount.
     * @param amount amount type.
     * @return amount value
     */
    public long getGBytes(Amount amount) {
        return getBytes(amount) / 0x40000000;
    }

}
