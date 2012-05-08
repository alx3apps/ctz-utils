package ru.concerteza.util.archive;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyFile;
import static ru.concerteza.util.io.CtzIOUtils.permissionsOctal;

/**
 * Archive function for creating <a href="http://en.wikipedia.org/wiki/Tar">TAR</a> archives,
 * uses TAR implementation from <a href="http://commons.apache.org/compress/">Apache Commons Compress library</a>.
 * For usage example see {@link CtzTarUtils}
 *
 * @author alexey,
 * Date: 5/4/12
 * @see CtzTarUtils
 */
public class TarFunction extends ArchiveFunction {
    private static final int DIR_MODE_TEMPLATE = 040055;
    private static final int FILE_MODE_TEMPLATE = 0100044;

    private final TarArchiveOutputStream tarStream;

    /**
     * Simplified constructor, with default {@code rootDirName = dir.getName()}
     * @param dir root directory to make archive of
     * @param tarStream target <a href="http://commons.apache.org/compress/apidocs/org/apache/commons/compress/archivers/tar/TarArchiveOutputStream.html">TarArchiveOutputStream</a>
     * to write files to
     */
    public TarFunction(File dir, TarArchiveOutputStream tarStream) {
        super(dir);
        this.tarStream = tarStream;
    }

    /**
     * Main constructor
     * @param dir root directory to make archive of
     * @param rootDirName root directory name that will be recorded in archive
     * @param tarStream target <a href="http://commons.apache.org/compress/apidocs/org/apache/commons/compress/archivers/tar/TarArchiveOutputStream.html">TarArchiveOutputStream</a>
     * to write files to
     */
    public TarFunction(File dir, String rootDirName, TarArchiveOutputStream tarStream) {
        super(dir, rootDirName);
        this.tarStream = tarStream;
    }

    /**
     * Writes input file or directory to <a href="http://commons.apache.org/compress/apidocs/org/apache/commons/compress/archivers/tar/TarArchiveOutputStream.html">TarArchiveOutputStream</a>,
     * trying to preserve FS permissions. Only current user's permissions are available in Java 6, other permissions
     * will be written as {@code x44} for files and {@code x55} for directories. Leaf directories are written as
     * {@code dirname/} records with empty content
     * @param input file or directory
     * @return path in archive input file were written onto
     */
    @Override
    public String apply(File input) {
        try {
            String relative = relative(input);
            String path = rootDirName + "/" + relative;
            TarArchiveEntry en = new TarArchiveEntry(input, path);
            en.setMode(mode(input));
            tarStream.putArchiveEntry(en);
            if(input.isFile()) copyFile(input, tarStream);
            tarStream.closeArchiveEntry();
            return path;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    // trying to preserve current user permissions
    private int mode(File file) {
        int mode = permissionsOctal(file);
        int tarmode = mode * 0100;
        int template = file.isDirectory() ? DIR_MODE_TEMPLATE : FILE_MODE_TEMPLATE;
        return template + tarmode;
    }
}
