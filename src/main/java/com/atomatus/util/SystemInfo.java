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

    public enum JVMArch {
        X64,
        X86,
        ARM
    }

    private final OS os;
    private final String osName;
    private final String osVersion;
    private final Arch osArch;
    private final JVMArch jvmArch;

    private static transient SystemInfo instance;

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

    public JVMArch getJVMArch(){
        String arch = System.getProperty("sun.arch.data.model");
        if(!StringUtils.isNullOrEmpty(arch)) {
            return (arch = arch.toLowerCase()).contains("64") ? JVMArch.X64 :
                    arch.contains("32") || arch.contains("x86") ? JVMArch.X86 :
                            JVMArch.ARM;
        }
        return JVMArch.ARM;
    }

    public Arch arch() {
        return osArch;
    }

    public JVMArch jvmArch() {
        return jvmArch;
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
