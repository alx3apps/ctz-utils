package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ru.concerteza.util.crypto.CtzHashUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8;
import static ru.concerteza.util.io.CtzIOUtils.copyResourceListToDir;
import static ru.concerteza.util.io.CtzIOUtils.createTmpDir;
import static ru.concerteza.util.io.CtzIOUtils.createTmpFile;

/**
 * User: alexey
 * Date: 4/30/12
 */
public class CtzIOUtilsTest {
    @Test
    public void testAppendToFile() throws IOException {
        File file = createTmpFile(getClass());
        writeStringToFile(file, "foo", UTF8);
        CtzIOUtils.appendToFile(file, "bar", UTF8);
        String res = FileUtils.readFileToString(file, UTF8);
        file.delete();
        assertEquals("foobar", res);
    }

    @Test
    public void testCopyResourceToDir() throws IOException {
        File dir = createTmpDir(getClass());
        String url = "classpath:/log4j.properties";
        String resSha1 = CtzHashUtils.sha1ResourceDigest(url);
        CtzIOUtils.copyResourceToDir(url, dir);
        File file = new File(dir, "log4j.properties");
        String fileSha1 = CtzHashUtils.sha1Digest(file);
        dir.delete();
        assertEquals(resSha1, fileSha1);
    }

    @Test
    public void testCopyResourceListToDir() throws IOException {
        File dir = createTmpDir(getClass());
        String pattern = "classpath:/*-blob-tool-test-ctx.xml";
        int count = copyResourceListToDir(pattern, dir);
        assertEquals("Copy fail", 4, dir.list().length);
        assertEquals("Report fail", 4, count);
        dir.delete();
    }
}
