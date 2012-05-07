package ru.concerteza.util.archive;

import com.google.common.base.Function;

import java.io.File;
import java.util.zip.ZipOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.FilenameUtils.separatorsToUnix;

/**
 * User: alexey
 * Date: 5/5/12
 */
public abstract class ArchiveFunction implements Function<File, String> {
    protected final String dir;
    protected final String rootDirName;

    public ArchiveFunction(File dir) {
        this(dir, dir.getName());
    }

    protected ArchiveFunction(File dir, String zipRootDirName) {
        this.dir = separatorsToUnix(dir.getPath());
        this.rootDirName = zipRootDirName;
    }

    protected String relative(String parent, File child) {
        String path = separatorsToUnix(child.getPath());
        // check whether actual child
        checkArgument(parent.equals(path.substring(0, parent.length())));
        String relative = path.substring(parent.length() + 1);
        return child.isDirectory() ? relative + "/" : relative;
    }
}
