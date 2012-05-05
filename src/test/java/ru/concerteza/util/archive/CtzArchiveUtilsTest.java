package ru.concerteza.util.archive;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;
import ru.concerteza.util.CtzConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.archive.CtzArchiveUtils.tarDirectory;
import static ru.concerteza.util.archive.CtzArchiveUtils.zipDirectory;
import static ru.concerteza.util.io.CtzIOUtils.createTmpDir;

/**
 * User: alexey
 * Date: 5/4/12
 */


public class CtzArchiveUtilsTest {
    @Test
    public void testZip() throws IOException {
        File dir = create2FilesDir();
        ZipOutputStream zipStream = new ZipOutputStream(new NullOutputStream());
        int filesCount = zipDirectory(dir, zipStream);
        zipStream.close();
        deleteDirectory(dir);
        assertEquals(2, filesCount);
    }

    @Test
    public void testTar() throws IOException {
        File dir = create2FilesDir();
        TarArchiveOutputStream tarStream = new TarArchiveOutputStream(new NullOutputStream());
        tarStream.setLongFileMode(LONGFILE_GNU);
        int filesCount = tarDirectory(dir, tarStream);
        tarStream.close();
        deleteDirectory(dir);
        assertEquals(2, filesCount);
    }

    private File create2FilesDir() throws IOException {
        File dir = createTmpDir(getClass());
        writeStringToFile(new File(dir, "foo"), "foo", CtzConstants.UTF8);
        File barDir = new File(dir, "bar");
        writeStringToFile(new File(barDir, "baz"), "baz", CtzConstants.UTF8);
        return dir;
    }
}