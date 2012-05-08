package ru.concerteza.util.archive;

import com.google.common.base.Function;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.FilenameUtils.separatorsToUnix;

/**
 * Base class for archive functions, that works on files and directories.
 * Designed to always have root directory in archive.
 *
 * @author alexey,
 * Date: 5/5/12
 * @see ZipFunction
 * @see TarFunction
 */
public abstract class ArchiveFunction implements Function<File, String> {
    /**
     * Root directory path to make archive of
     */
    protected final String dir;
    /**
     * Root directory name that will be recorded in archive,
     * default: {@code dir.getName()}
     */
    protected final String rootDirName;

    /**
     * Simplified constructor with default {@code rootDirName = dir.getName()}
     * @param dir root directory path to make archive of
     */
    public ArchiveFunction(File dir) {
        this(dir, dir.getName());
    }

    /**
     * Main constructor
     * @param dir root directory to make archive of
     * @param rootDirName root directory name that will be recorded in archive
     */
    protected ArchiveFunction(File dir, String rootDirName) {
        this.dir = separatorsToUnix(dir.getPath());
        this.rootDirName = rootDirName;
    }

    /**
     * Makes relative (to root dir) paths for archive
     * @param child file or directory
     * @return path relative to {@code dir}, with {@code /} for directories
     */
    protected String relative(File child) {
        String path = separatorsToUnix(child.getPath());
        // check whether actual child
        checkArgument(dir.equals(path.substring(0, dir.length())));
        String relative = path.substring(dir.length() + 1);
        return child.isDirectory() ? relative + "/" : relative;
    }
}
