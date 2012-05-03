package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8;
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
}
