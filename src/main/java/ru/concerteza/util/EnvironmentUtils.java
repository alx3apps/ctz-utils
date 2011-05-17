package ru.concerteza.util;

import static java.lang.System.getProperty;
import static ru.concerteza.util.EnvironmentUtils.Platform.*;

/**
 * User: alexey
 * Date: 5/17/11
 */
public class EnvironmentUtils {

    public enum Platform {
        UNKNOWN, WINDOWS_X86_32, WINDOWS_X86_64, LINUX_X86_32, LINUX_X86_64,
        SOLARIS_X86_32, SOLARIS_X86_64, SOLARIS_SPARC_32, SOLARIS_SPARC_64, MACOSX_X86_32, MACOSX_X86_64
    }

    public static final Platform CURRENT_PLATFORM;

    static {
        String name = getProperty("os.name").toLowerCase();
        String arch = getProperty("os.arch").toLowerCase();
        if (name.startsWith("windows") && "x86".equals(arch)) {
            CURRENT_PLATFORM = WINDOWS_X86_32;
        } else if (name.startsWith("windows") && ("x86_64".equals(arch) || "amd64".equals(arch))) {
            CURRENT_PLATFORM = WINDOWS_X86_64;
        } else if ("linux".equals(name) && "i386".equals(arch)) {
            CURRENT_PLATFORM = LINUX_X86_32;
        } else if ("linux".equals(name) && "amd64".equals(arch)) {
            CURRENT_PLATFORM = LINUX_X86_64;
        } else if ("sunos".equals(name) && "x86".equals(arch)) {
            CURRENT_PLATFORM = SOLARIS_X86_32;
        } else if ("sunos".equals(name) && "amd".equals(arch)) {
            CURRENT_PLATFORM = SOLARIS_X86_64;
        } else if ("sunos".equals(name) && "sparc".equals(arch)) {
            CURRENT_PLATFORM = SOLARIS_SPARC_32;
        } else if ("sunos".equals(name) && "sparcv9".equals(arch)) {
            CURRENT_PLATFORM = SOLARIS_SPARC_64;
        } else if ("mac os x".equals(name) && "x86".equals(arch)) {
            CURRENT_PLATFORM = MACOSX_X86_32;
        } else if ("mac os x".equals(name) && ("x86_64".equals(arch) || "amd64".equals(arch))) {
            CURRENT_PLATFORM = MACOSX_X86_64;
        } else {
            CURRENT_PLATFORM = UNKNOWN;
        }
    }
}
