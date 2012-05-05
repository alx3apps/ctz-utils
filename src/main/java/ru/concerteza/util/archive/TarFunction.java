package ru.concerteza.util.archive;

import com.google.common.base.Function;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FilenameUtils.separatorsToUnix;
import static ru.concerteza.util.archive.CtzArchiveUtils.relative;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class TarFunction implements Function<File, String> {
    private String parent;
    private final TarArchiveOutputStream tarStream;

    public TarFunction(File dir, TarArchiveOutputStream tarStream) {
        this.parent = separatorsToUnix(dir.getParent());
        this.tarStream = tarStream;
    }

    @Override
    public String apply(File input) {
        try {
            String path = relative(parent, input);
            tarStream.putArchiveEntry(new TarArchiveEntry(input, path));
            FileUtils.copyFile(input, tarStream);
            tarStream.closeArchiveEntry();
            return path;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
