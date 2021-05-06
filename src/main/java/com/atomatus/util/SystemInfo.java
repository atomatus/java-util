package com.atomatus.util;

/**
 * Current system information.
 */
public final class SystemInfo {

    /**
     * Operation system type
     */
    public enum OS {
        /**
         * Unknown OS.
         */
        UNKNOWN,

        /**
         * Android
         */
        ANDROID,

        /**
         * Linux
         */
        LINUX,

        /**
         * MacOSX
         */
        MAC_OS,

        /**
         * Solaris
         */
        SOLARIS,

        /**
         * Windows
         */
        WINDOWS
    }

    /**
     * CPU Architecture type.
     */
    public enum Arch {

        /**
         * Unknown CPU Arch.
         */
        UNKNOWN,
        /**
         * Intel x64
         */
        IA64,

        /**
         * AMD x64
         */
        AMD64,

        /**
         * CPU x32
         */
        X86,

        /**
         * CPU ARM
         */
        ARM
    }

    /**
     * JVM Architecture type.
     */
    public enum JVMArch {
        /**
         * JVM x64
         */
        X64,

        /**
         * JVM x86 (32Bits)
         */
        X86,

        /**
         * JVm ARM
         */
        ARM
    }

    private final OS os;
    private final String osName;
    private final String osVersion;
    private final Arch osArch;
    private final JVMArch jvmArch;

    private static transient SystemInfo instance;

    /**
     * Transient instance of System info.
     * @return system info.
     */
    public synchronized static SystemInfo getInstance() {
        return (instance == null ? instance = new SystemInfo() : instance);
    }

    private SystemInfo() {
        this.osName     = System.getProperty("os.name");
        this.osVersion  = System.getProperty("os.version");
        this.os         = this.getOS();
        this.osArch     = this.getOSArch();
        this.jvmArch    = this.getJVMArch();
    }

    private OS getOS() {
        String osName = this.osName.toLowerCase();
        if(osName.contains("win")) {
            return OS.WINDOWS;
        } else if(osName.contains("mac")){
            return OS.MAC_OS;
        } else if (osName.contains("linux")) {
            String vmName = System.getProperty("java.vm.name");
            return vmName != null && vmName.contains("Dalvik") ?
                    OS.ANDROID : OS.LINUX;
        } else if (osName.contains("sunos")) {
            return OS.SOLARIS;
        } else {
            return OS.UNKNOWN;
        }
    }

    private Arch getOSArch(){
        String arch = System.getProperty("os.arch");
        if(!StringUtils.isNullOrEmpty(arch)) {
            if((arch = arch.toLowerCase()).endsWith("64")) {
                return arch.contains("amd") ? Arch.AMD64 : Arch.IA64;
            } else if(arch.contains("x86") || (arch.startsWith("i") && arch.endsWith("86"))) {
                return Arch.X86;
            } else if (arch.contains("arm")) {
                return Arch.ARM;
            }
        }

        return Arch.UNKNOWN;
    }

    /**
     * Get JVM arch
     * @return jvm arch.
     */
    public JVMArch getJVMArch(){
        String arch = System.getProperty("sun.arch.data.model");
        if(!StringUtils.isNullOrEmpty(arch)) {
            return (arch = arch.toLowerCase()).contains("64") ? JVMArch.X64 :
                    arch.contains("32") || arch.contains("x86") ? JVMArch.X86 :
                            JVMArch.ARM;
        }
        return JVMArch.ARM;
    }

    /**
     * CPU architecture.
     * @return cpu arch.
     */
    public Arch arch() {
        return osArch;
    }

    /**
     * JVM architecture.
     * @return JVM arch.
     */
    public JVMArch jvmArch() {
        return jvmArch;
    }

    /**
     * OS type
     * @return os type.
     */
    public OS os() {
        return os;
    }

    /**
     * OS name.
     * @return os name.
     */
    public String name() {
        return osName;
    }

    /**
     * OS version
     * @return os version.
     */
    public String osVersion() {
        return osVersion;
    }

    /**
     * Check current OS is Android.
     * @return true, is android, otherwise false.
     */
    public boolean isAndroid() {
        return os == OS.ANDROID;
    }

    /**
     * Check current OS is Linux.
     * @return true, is linux, otherwise false.
     */
    public boolean isLinux() {
        return os == OS.LINUX;
    }

    /**
     * Check current OS is Mac.
     * @return true, is Mac, otherwise false.
     */
    public boolean isMac() {
        return os == OS.MAC_OS;
    }

    /**
     * Check current OS is Solaris.
     * @return true, is solaris, otherwise false.
     */
    public boolean isSolaris() {
        return os == OS.SOLARIS;
    }

    /**
     * Check current OS is Windows.
     * @return true, is windows, otherwise false.
     */
    public boolean isWindows() {
        return os == OS.WINDOWS;
    }

    /**
     * Get app directory bin path indicate by OS.
     * @return app bin path indicate by OS.
     */
    public String getAppBinPath(){
        switch (os) {
            case WINDOWS:
                return jvmArch == JVMArch.X64 ?
                        System.getenv("ProgramFiles") :
                        System.getenv("ProgramFiles") + " (x86)";
            case LINUX:
                return "/usr/bin";
            default:
                return System.getProperty("user.home");
        }
    }
}
