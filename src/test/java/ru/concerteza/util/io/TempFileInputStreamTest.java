package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.string.CtzConstants.UTF8;
import static ru.concerteza.util.io.CtzIOUtils.createTmpFile;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class TempFileInputStreamTest {
    @Test
    public void test() throws IOException {
        File file = createTmpFile(getClass());
        FileUtils.writeStringToFile(file, "foobar", UTF8);
        String str = FileUtils.readFileToString(file, UTF8);
        assertEquals("foobar", str);
        InputStream is = new TempFileInputStream(file);
        assertTrue(file.exists());
        String streamed = IOUtils.toString(is, UTF8);
        assertEquals("foobar", streamed);
        assertTrue(file.exists());
        is.close();
        assertFalse(file.exists());
    }
}
