package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;
import ru.concerteza.util.CtzConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.CtzConstants.UTF8;
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
    public void testListFiles() throws IOException {
        File dir = create3FilesDir();
        List<File> files = CtzIOUtils.listFiles(dir);
        assertEquals("Size fail", 3, files.size());
        assertEquals("Elem fail", "foo", files.get(0).getName());
        assertEquals("Elem fail", "woo", files.get(1).getName());
        assertEquals("Elem fail", "baz", files.get(2).getName());
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
        File wooDir = new File(booDir, "woo");
        wooDir.mkdir();
        return dir;
    }
}
