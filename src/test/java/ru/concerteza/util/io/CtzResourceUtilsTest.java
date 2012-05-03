package ru.concerteza.util.io;

import org.junit.Test;
import org.springframework.core.io.Resource;
import ru.concerteza.util.crypto.CtzHashUtils;
import ru.concerteza.util.io.CtzResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.io.CtzIOUtils.codeSourceDir;
import static ru.concerteza.util.io.CtzIOUtils.createTmpDir;
import static ru.concerteza.util.io.CtzIOUtils.createTmpFile;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.io.CtzResourceUtils.isDirectory;
import static ru.concerteza.util.io.CtzResourceUtils.path;

/**
 * User: alexey
 * Date: 5/3/12
 */
public class CtzResourceUtilsTest {
    @Test
    public void testCopyResourceToDir() throws IOException {
        File dir = createTmpDir(getClass());
        String url = "classpath:/log4j.properties";
        String resSha1 = CtzHashUtils.sha1ResourceDigest(url);
        CtzResourceUtils.copyResourceToDir(url, dir);
        File file = new File(dir, "log4j.properties");
        String fileSha1 = CtzHashUtils.sha1Digest(file);
        dir.delete();
        assertEquals(resSha1, fileSha1);
    }

    @Test
    public void testIsDirectory() {
        assertTrue(isDirectory(RESOURCE_LOADER.getResource("classpath:/")));
        File dir = codeSourceDir(getClass());
        assertTrue(isDirectory(RESOURCE_LOADER.getResource("file:" + dir.getPath())));
        assertFalse(isDirectory(RESOURCE_LOADER.getResource("classpath:/log4j.properties")));
        File file = createTmpFile(getClass());
        assertFalse(isDirectory(RESOURCE_LOADER.getResource("file:" + file.getPath())));
        file.delete();
    }

    @Test
    public void testPath() {
        {
            String path = "classpath:/foo/bar.baz";
            Resource res = RESOURCE_LOADER.getResource(path);
            assertEquals("Classpath fail", path, path(res));
        }
        {
            String path = "file:/foo/bar.baz";
            Resource res = RESOURCE_LOADER.getResource(path);
            assertEquals("File fail", path, path(res));
        }
    }
}
