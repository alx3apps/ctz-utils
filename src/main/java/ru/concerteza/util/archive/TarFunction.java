package ru.concerteza.util.archive;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyFile;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class TarFunction extends ArchiveFunction {
    private final TarArchiveOutputStream tarStream;

    public TarFunction(File dir, TarArchiveOutputStream tarStream) {
        super(dir);
        this.tarStream = tarStream;
    }

    public TarFunction(File dir, String tarRootDirName, TarArchiveOutputStream tarStream) {
        super(dir, tarRootDirName);
        this.tarStream = tarStream;
    }

    @Override
    public String apply(File input) {
        try {
            String relative = relative(dir, input);
            String path = rootDirName + "/" + relative;
            tarStream.putArchiveEntry(new TarArchiveEntry(input, path));
            if(input.isFile()) copyFile(input, tarStream);
            tarStream.closeArchiveEntry();
            return path;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
