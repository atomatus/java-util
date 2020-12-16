package com.atomatus.util;

/**
 * Information when debugger connected.
 */
public final class Debug {

    private static Boolean debugMode;

    private Debug() { }

    /**
     * Check if current instance is running in debug mode.
     * @return true to inform that debug mode is enabled
     */
    public static synchronized boolean isDebugMode() {
        if(debugMode == null) {
            Reflection ref;
            if ((ref = Reflection.tryInflate("java.lang.management.ManagementFactory")).inflated()) {
                debugMode = ref.configDeflateAfterReturns()
                        .method("getRuntimeMXBean")
                        .method("getInputArguments")
                        .method("toString")
                        .method("indexOf", "jdwp")
                        .valueInt() > 0;
            } else if ((ref = Reflection.tryInflate("android.os.Debug")).inflated()) {
                debugMode = ref.method("isDebuggerConnected").valueBoolean();
            } else {
                debugMode = false;
            }
        }

        return debugMode;
    }
}
