package com.atomatus.util;

public final class SystemInfo {

    public enum OS {
        UNKNOWN,
        ANDROID,
        LINUX,
        MAC_OS,
        SOLARIS,
        WINDOWS
    }

    public enum Arch {
        UNKNOWN,
        IA64,
        AMD64,
        X86,
        ARM
    }

    private final OS os;
    private final String osName;
    private final String osVersion;
    private final Arch osArch;

    private static transient SystemInfo instance;

    public synchronized static SystemInfo getInstance() {
        return (instance == null ? instance = new SystemInfo() : instance);
    }

    private SystemInfo() {
        this.osName     = System.getProperty("os.name");
        this.osVersion  = System.getProperty("os.version");
        this.os         = this.getOS();
        this.osArch     = this.getOSArch();
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
            arch = arch.toLowerCase();
            if(arch.endsWith("64")) {
                return arch.contains("amd") ? Arch.AMD64 : Arch.IA64;
            } else if(arch.contains("x86") || (arch.startsWith("i") && arch.endsWith("86"))) {
                return Arch.X86;
            } else if (arch.contains("arm")) {
                return Arch.ARM;
            }
        }

        return Arch.UNKNOWN;
    }

    public Arch arch() {
        return osArch;
    }

    public OS os() {
        return os;
    }

    public String name() {
        return osName;
    }

    public String osVersion() {
        return osVersion;
    }

    public boolean isAndroid() {
        return os == OS.ANDROID;
    }

    public boolean isLinux() {
        return os == OS.LINUX;
    }

    public boolean isMac() {
        return os == OS.MAC_OS;
    }

    public boolean isSolaris() {
        return os == OS.SOLARIS;
    }

    public boolean isWindows() {
        return os == OS.WINDOWS;
    }
}
