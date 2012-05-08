package ru.concerteza.util.archive;

import org.junit.Test;
import ru.concerteza.util.CtzConstants;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.archive.CtzTarUtils.tarDirectory;
import static ru.concerteza.util.archive.CtzZipUtils.zipDirectory;
import static ru.concerteza.util.io.CtzIOUtils.createTmpDir;
import static ru.concerteza.util.io.CtzIOUtils.createTmpFile;

/**
 * User: alexey
 * Date: 5/4/12
 */


public class CtzZipUtilsTest {
    @Test
    public void testZip() throws IOException {
        File dir = create3FilesDir();
        File file = createTmpFile(getClass());
        int filesCount = zipDirectory(dir, file);
        file.delete();
        deleteDirectory(dir);
        assertEquals(3, filesCount);
    }

    @Test(expected = RuntimeIOException.class)
    public void testZipRIOE() throws IOException {
        File dir = createTmpDir(getClass());
        deleteDirectory(dir);
        tarDirectory(dir, new File(""));
    }

    private File create3FilesDir() throws IOException {
        File dir = createTmpDir(getClass());
        deleteDirectory(dir);
        dir.mkdir();
        writeStringToFile(new File(dir, "foo"), "foo", CtzConstants.UTF8);
        File barDir = new File(dir, "bar");
        writeStringToFile(new File(barDir, "baz"), "baz", CtzConstants.UTF8);
        File booDir = new File(barDir, "boo");
        booDir.mkdir();
        return dir;
    }
}