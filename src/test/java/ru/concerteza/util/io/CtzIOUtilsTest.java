package ru.concerteza.util.io;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;
import ru.concerteza.util.string.CtzConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.string.CtzConstants.UTF8;
import static ru.concerteza.util.io.CtzIOUtils.*;

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
    public void testListFiles() throws IOException {
        File dir = createDir();
        {
            List<File> files = listFiles(dir, true);
            assertEquals("Size fail", 3, files.size());
            assertEquals("Elem fail", "foo", files.get(0).getName());
            assertEquals("Elem fail", "baz", files.get(1).getName());
            assertEquals("Elem fail", "woo", files.get(2).getName());
        }
        {
            List<File> files = listFiles(dir, false);
            assertEquals("Size fail", 2, files.size());
            assertEquals("Elem fail", "foo", files.get(0).getName());
            assertEquals("Elem fail", "baz", files.get(1).getName());
        }
        {
            List<File> files = listFiles(dir, new NotFileFilter(new SuffixFileFilter("oo")), true);
            assertEquals("Size fail", 2, files.size());
            assertEquals("Elem fail", "baz", files.get(0).getName());
            assertEquals("Elem fail", "woo", files.get(1).getName());
        }
        {
            List<File> files = ImmutableList.copyOf(iterateFiles(dir, TrueFileFilter.TRUE, new NotFileFilter(new SuffixFileFilter("ar")), true));
            assertEquals("Size fail", 1, files.size());
            assertEquals("Elem fail", "foo", files.get(0).getName());
        }
        {
            List<File> files = ImmutableList.copyOf(iterateFiles(dir, TrueFileFilter.TRUE, new NotFileFilter(new SuffixFileFilter("oo")), true));
            assertEquals("Size fail", 2, files.size());
            assertEquals("Elem fail", "foo", files.get(0).getName());
            assertEquals("Elem fail", "baz", files.get(1).getName());
        }
        deleteDirectory(dir);
    }

    @Test
    public void testPermissionsOctal() {
        File file = createTmpFile(getClass());
        boolean res1 = file.setExecutable(false);
        boolean res2 = file.setReadable(false);
        boolean res3 = file.setWritable(false);
        // if that fails, there is nothing to test
        // works in linux
        if(!(res1 && res2 && res3)) return;
        assertEquals(0, permissionsOctal(file));
        file.setReadable(true);
        assertEquals(4, permissionsOctal(file));
        file.setWritable(true);
        assertEquals(6, permissionsOctal(file));
        file.setExecutable(true);
        assertEquals(7, permissionsOctal(file));
        file.delete();
    }

    private File createDir() throws IOException {
        File dir = createTmpDir(getClass());
        deleteDirectory(dir);
        dir.mkdir();
        writeStringToFile(new File(dir, "foo"), "foo", CtzConstants.UTF8);
        File barDir = new File(dir, "bar");
        writeStringToFile(new File(barDir, "baz"), "baz", CtzConstants.UTF8);
        File booDir = new File(barDir, "boo");
        booDir.mkdir();
        File wooDir = new File(booDir, "woo");
        wooDir.mkdir();
        return dir;
    }
}
