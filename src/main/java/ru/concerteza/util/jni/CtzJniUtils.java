package ru.concerteza.util.jni;

import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static java.lang.System.getProperty;
import static ru.concerteza.util.string.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzIOUtils.codeSourceDir;

/**
 * Utility methods for loading JNI libraries without specifying {@code java.library.path}
 *
 * @author alexey
 * Date: 5/17/11
 */
public class CtzJniUtils {
    private static final Platform CURRENT_PLATFORM;

    // todo: use GNU triplets
    private enum Platform {
        UNKNOWN("unknown", "unknown"),
        WINDOWS_X86_32("windows-x86_32", "dll"), WINDOWS_X86_64("windows-x86_64", "dll"),
        LINUX_X86_32("linux-x86_32", "so"), LINUX_X86_64("linux-x86_64", "so"),
        SOLARIS_X86_32("sol-x86_32", "so"), SOLARIS_X86_64("sol-x86_64", "so"),
        SOLARIS_SPARC_32("sol-sparc_32", "so"), SOLARIS_SPARC_64("sol-sparc_64", "so"),
        MACOSX_X86_32("mac-x86_32", "jnilib"), MACOSX_X86_64("mac-x86_64", "jnilib");

        // maven features
        private final String classifier;
        private final String type;

        Platform(String classifier, String type) {
            this.classifier = classifier;
            this.type = type;
        }

        public String getClassifier() {
            return classifier;
        }

        public String getType() {
            return type;
        }
    }

    static {
        String name = getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String arch = getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        if (name.startsWith("windows") && "x86".equals(arch)) {
            CURRENT_PLATFORM = Platform.WINDOWS_X86_32;
        } else if (name.startsWith("windows") && ("x86_64".equals(arch) || "amd64".equals(arch))) {
            CURRENT_PLATFORM = Platform.WINDOWS_X86_64;
        } else if ("linux".equals(name) && "i386".equals(arch)) {
            CURRENT_PLATFORM = Platform.LINUX_X86_32;
        } else if ("linux".equals(name) && "amd64".equals(arch)) {
            CURRENT_PLATFORM = Platform.LINUX_X86_64;
        } else if ("sunos".equals(name) && "x86".equals(arch)) {
            CURRENT_PLATFORM = Platform.SOLARIS_X86_32;
        } else if ("sunos".equals(name) && "amd64".equals(arch)) {
            CURRENT_PLATFORM = Platform.SOLARIS_X86_64;
        } else if ("sunos".equals(name) && "sparc".equals(arch)) {
            CURRENT_PLATFORM = Platform.SOLARIS_SPARC_32;
        } else if ("sunos".equals(name) && "sparcv9".equals(arch)) {
            CURRENT_PLATFORM = Platform.SOLARIS_SPARC_64;
        } else if ("mac os x".equals(name) && "x86".equals(arch)) {
            CURRENT_PLATFORM = Platform.MACOSX_X86_32;
        } else if ("mac os x".equals(name) && ("x86_64".equals(arch) || "amd64".equals(arch))) {
            CURRENT_PLATFORM = Platform.MACOSX_X86_64;
        } else {
            CURRENT_PLATFORM = Platform.UNKNOWN;
        }
    }

    /**
     * Loads JNI library with proper platform postfix from provided directory
     *
     * @param name library name
     * @param dirPath directory to load library from
     * @throws ru.concerteza.util.io.RuntimeIOException
     */
    public static void loadJniLib(String name, File dirPath) {
        final String filename;
        switch (CURRENT_PLATFORM) {
            case UNKNOWN:
                throw new RuntimeIOException(format(
                        "Cannot determine platform, os.name: {}, os.arch: {}", getProperty("os.name"), getProperty("os.arch")));
            default:
                filename = format("{}-{}.{}", name, CURRENT_PLATFORM.getClassifier(), CURRENT_PLATFORM.getType());
        }
        File target = new File(dirPath, filename);
        if (!(target.exists() && target.isFile())) throw new RuntimeIOException(format("File not found: '{}'", target.getPath()));
        try {
            System.load(target.getPath());
        } catch (Exception e) {
            throw new RuntimeIOException(e);
        } catch (Error e) {
            throw new RuntimeIOException(new UnhandledException(e));
        }
    }

    /**
     * Loads JNI library with proper platform postfix from {@code <provided_class_jar>/lib/native} directory
     *
     * @param mainClass class to build path from
     * @param names list of libraries names
     */
    public static void loadJniLibsFromStandardPath(Class<?> mainClass, String... names) {
        File jarPath = codeSourceDir(mainClass);
        String postfix = "lib" + File.separator + "native";
        File nativeLibsPath = new File(jarPath, postfix).getAbsoluteFile();
        for(String na : names) loadJniLib(na, nativeLibsPath);
    }
}
