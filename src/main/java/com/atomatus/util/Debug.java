package com.atomatus.util;

/**
 * Information when debugger connected.
 */
public final class Debug {

    private static transient Boolean debugMode;

    private Debug() { }

    private static boolean isDebugModeByAndroidPackageManager(Object context) {
        if(context != null) {
            Reflection ctxRef = Reflection.tryCast(context, "android.content.Context");

            if (ctxRef.inflated()) {
                final int FLAG_DEBUGGABLE = 1<<1;//ApplicationInfo.FLAG_DEBUGGABLE
                //region android.content.pm.PackageManager
                Object pm = ctxRef.method("getPackageManager").value();
                String packName = ctxRef.method("getPackageName").valueString();
                //endregion

                //region android.content.pm.ApplicationInfo
                Object appInfo = Reflection
                        .tryCast(pm, "android.content.pm.PackageManager")
                        .configDeflateAfterReturns()
                        .method("getApplicationInfo", packName, 0)
                        .value();

                int flags = Reflection
                        .tryCast(appInfo, "android.content.pm.ApplicationInfo")
                        .configDeflateAfterReturns()
                        .field("flags")
                        .valueInt();
                //endregion

                ctxRef.deflate();
                return 0 != (flags & FLAG_DEBUGGABLE);
            }
        }
        return false;
    }

    private static boolean isDebugModeByAndroidBuildConfigPackName(String packageName) {
        return packageName != null && Reflection
                .tryInflate(packageName.concat(".BuildConfig"))
                .configDeflateAfterReturns()
                .field("DEBUG")
                .valueBoolean();
    }

    private static boolean isDebugModeByAndroidBuildConfig(Object context) {
        if (context != null) {
            //region getApplicationContext#getPackageName
            String packName = Reflection.tryCast(context, "android.content.Context")
                    .configDeflateAfterReturns()
                    .method("getApplicationContext")
                    .method("getPackageName")
                    .valueString();
            //endregion

            //region BuildConfig.DEBUG
            return isDebugModeByAndroidBuildConfigPackName(packName);
            //endregion
        } else {
            StackTraceElement[] arr = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : arr) {
                try {
                    String className = stackTraceElement.getClassName();
                    Class<?> clazz = Class.forName(className);
                    Package pack = clazz.getPackage();
                    if (pack != null && isDebugModeByAndroidBuildConfigPackName(pack.getName())) {
                        return true;
                    }
                } catch (Exception ignored) { }
            }
            return false;
        }
    }

    private static boolean isDebugModeByAndroidDebuggerConnected() {
        return Reflection.tryInflate("android.os.Debug")
                .configDeflateAfterReturns()
                .method("isDebuggerConnected")
                .valueBoolean();
    }

    private static boolean isDebugModeByJavaManagementFactory() {
        Reflection ref = Reflection.tryInflate("java.lang.management.ManagementFactory");
        return ref.inflated() && ref.configDeflateAfterReturns()
                    .method("getRuntimeMXBean")
                    .method("getInputArguments")
                    .method("toString")
                    .method("indexOf", "jdwp")
                    .valueInt() > 0;
    }

    private static boolean isAndroidApp() {
        return "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name")) ||
                Reflection.tryInflate("android.app.Activity").inflated();
    }

    /**
     * Check if current App instance is running in debug mode.
     * @return true to inform that debug mode is enabled or app was built in debug mode, otherwise false.
     */
    public static boolean isDebugMode() {
        return isDebugMode(null);
    }

    /**
     * Check if current App instance is running in debug mode.
     * @param context app android context
     * @return true to inform that debug mode is enabled or app was built in debug mode, otherwise false.
     */
    public static synchronized boolean isDebugMode(Object context) {
        return debugMode != null ? debugMode : (debugMode =
                isDebugModeByJavaManagementFactory() || (isAndroidApp() &&
                        (isDebugModeByAndroidDebuggerConnected() ||
                         isDebugModeByAndroidBuildConfig(context) ||
                         isDebugModeByAndroidPackageManager(context))));
    }
}
