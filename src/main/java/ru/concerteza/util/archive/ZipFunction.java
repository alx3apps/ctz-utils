package ru.concerteza.util.archive;

import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.copyFile;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class ZipFunction extends ArchiveFunction {
    private final ZipOutputStream zipStream;

    public ZipFunction(File dir, ZipOutputStream zipStream) {
        super(dir);
        this.zipStream = zipStream;
    }

    public ZipFunction(File dir, String zipRootDirName, ZipOutputStream zipStream) {
        super(dir, zipRootDirName);
        this.zipStream = zipStream;
    }

    @Override
    public String apply(File input) {
        try {
            String relative = relative(dir, input);
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
