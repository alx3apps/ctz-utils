package ru.concerteza.util.archive;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyFile;
import static ru.concerteza.util.io.CtzIOUtils.permissionsOctal;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class TarFunction extends ArchiveFunction {
    private static final int DIR_MODE_TEMPLATE = 040055;
    private static final int FILE_MODE_TEMPLATE = 0100044;

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
