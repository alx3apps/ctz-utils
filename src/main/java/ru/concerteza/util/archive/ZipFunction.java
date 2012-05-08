package ru.concerteza.util.archive;

import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.copyFile;

/**
 * Archive function for creating <a href="http://en.wikipedia.org/wiki/Zip_%28file_format%29">ZIP</a>
 * archives, uses standard ZIP implementation form JDK. For usage example see {@link CtzZipUtils}
 *
 * @author alexey,
 * Date: 5/4/12
 * @see CtzZipUtils
 */
public class ZipFunction extends ArchiveFunction {
    private final ZipOutputStream zipStream;

    /**
     * Simplified constructor, with default {@code rootDirName = dir.getName()}
     * @param dir dir root directory to make archive of
     * @param zipStream target <a href="http://docs.oracle.com/javase/6/docs/api/java/util/zip/ZipOutputStream.html">ZipOutputStream</a>
     * to write files to
     */
    public ZipFunction(File dir, ZipOutputStream zipStream) {
        super(dir);
        this.zipStream = zipStream;
    }

    /**
     * Main constructor
     * @param dir dir root directory to make archive of
     * @param rootDirName root directory name that will be recorded in archive
     * @param zipStream target <a href="http://docs.oracle.com/javase/6/docs/api/java/util/zip/ZipOutputStream.html">ZipOutputStream</a>
     * to write files to
     */
    public ZipFunction(File dir, String rootDirName, ZipOutputStream zipStream) {
        super(dir, rootDirName);
        this.zipStream = zipStream;
    }

    /**
     * Writes input file or directory to <a href="http://docs.oracle.com/javase/6/docs/api/java/util/zip/ZipOutputStream.html">ZipOutputStream</a>,
     * leaf directories are written as {@code dirname/} records with empty content
     * @param input file or directory
     * @return path in archive input file were written onto
     */
    @Override
    public String apply(File input) {
        try {
            String relative = relative(input);
            String path = rootDirName + "/" + relative;
            zipStream.putNextEntry(new ZipEntry(path));
            if(input.isFile()) copyFile(input, zipStream);
            zipStream.closeEntry();
            return path;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
