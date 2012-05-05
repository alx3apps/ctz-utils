package ru.concerteza.util.archive;

import com.google.common.base.Function;
import org.apache.commons.io.FileUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FilenameUtils.separatorsToUnix;
import static ru.concerteza.util.archive.CtzArchiveUtils.relative;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class ZipFunction implements Function<File, String> {
    private String parent;
    private final ZipOutputStream zipStream;

    public ZipFunction(File dir, ZipOutputStream zipStream) {
        this.parent = separatorsToUnix(dir.getParent());
        this.zipStream = zipStream;
    }

    @Override
    public String apply(File input) {
        try {
            String path = relative(parent, input);
            zipStream.putNextEntry(new ZipEntry(path));
            FileUtils.copyFile(input, zipStream);
            zipStream.closeEntry();
            return path;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
