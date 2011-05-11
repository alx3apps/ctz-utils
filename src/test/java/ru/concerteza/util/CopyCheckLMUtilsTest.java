package ru.concerteza.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static ru.concerteza.util.CommonConstants.*;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class CopyCheckLMUtilsTest {

    @Test
    public void testNotOverwritten() throws IOException {
        File[] files = prepareFiles();
        File f1 = files[0];
        File f2 = files[1];
        CopyCheckLMUtils.copyFile(f1, f2);
        final String contCheck = FileUtils.readFileToString(f2, UTF8);
        assertEquals("LM fail", "bar", contCheck);
    }

    @Test
    public void testOverwritten() throws IOException {
        File[] files = prepareFiles();
        File f1 = files[0];
        File f2 = files[1];
        final boolean res = f2.setLastModified(f2.lastModified() + 1000);
        assertTrue("setLastModified fail", res);
        CopyCheckLMUtils.copyFile(f1, f2);
        final String contCheck = FileUtils.readFileToString(f2, UTF8);
        assertEquals("copy fail", "foo", contCheck);
    }

    private File[] prepareFiles() throws IOException {
        File f1 = File.createTempFile("foo", "bar");
        f1.deleteOnExit();
        File f2 = File.createTempFile("foo", "baz");
        f2.deleteOnExit();
        FileUtils.write(f1, "foo", UTF8);
        FileUtils.write(f2, "bar", UTF8);
        long now = System.currentTimeMillis();
        final boolean res1 = f1.setLastModified(now);
        assertTrue("setLastModified fail", res1);
        final boolean res2 = f2.setLastModified(now);
        assertTrue("setLastModified fail", res2);
        final String cont1 = FileUtils.readFileToString(f1, UTF8);
        assertEquals("first write fail", "foo", cont1);
        final String cont2 = FileUtils.readFileToString(f2, UTF8);
        assertEquals("first write fail", "bar", cont2);
        return new File[]{f1, f2};
    }

}
