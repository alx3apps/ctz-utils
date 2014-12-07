package ru.concerteza.util.net;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import ru.concerteza.util.io.CtzIOUtils;
import ru.concerteza.util.net.diriterator.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static junit.framework.Assert.assertEquals;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.concerteza.util.io.CtzIOUtils.createTmpDir;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class DirIteratorTest {

    @Test
    public void dummy() {

    }

//    @Test
    public void testSftp() throws IOException {
        DirIterator it = null;
        InputStream is = null;
        try {
            DirIteratorPaths paths = preparePaths();
            it = new SftpDirIterator("127.0.0.1", 22, "alex", "hunter2", paths).open();
            List<RemoteFile> files = ImmutableList.copyOf(it);
            assertEquals(2, files.size());
            assertEquals("bar.txt", files.get(0).getName());
            assertEquals("foo.txt", files.get(1).getName());
            is = files.get(1).open();
            String val = IOUtils.toString(is, "UTF-8");
            assertEquals("42", val);
            is.close();
            it.close();
        } finally {
            closeQuietly(it);
            closeQuietly(is);
        }
    }

//    @Test
    public void testFtp() throws IOException {
        DirIterator it = null;
        InputStream is = null;
        try {
            DirIteratorPaths paths = preparePaths();
            it = new FtpDirIterator("127.0.0.1", 21, "alex", "hunter2", paths).open();
            List<RemoteFile> files = ImmutableList.copyOf(it);
            assertEquals(2, files.size());
            assertEquals("bar.txt", files.get(0).getName());
            assertEquals("foo.txt", files.get(1).getName());
            is = files.get(1).open();
            String val = IOUtils.toString(is, "UTF-8");
            assertEquals("42", val);
            is.close();
            it.close();
        } finally {
            closeQuietly(it);
            closeQuietly(is);
        }
    }

    private DirIteratorPaths preparePaths() throws IOException {
        File dir = createTmpDir(getClass());
        writeStringToFile(new File(dir, "foo.txt"), "42", UTF_8.name());
        writeStringToFile(new File(dir, "bar.txt"), "43", UTF_8.name());
        File successDir = new File(dir, "success");
        File errorDir = new File(dir, "error");
        return new DirIteratorPaths(dir.getAbsolutePath(), successDir.getAbsolutePath(), errorDir.getAbsolutePath());
    }
}
