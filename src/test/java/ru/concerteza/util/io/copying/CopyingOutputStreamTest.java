package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class CopyingOutputStreamTest {

    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        InputStream is = new ByteArrayInputStream(source.getBytes(UTF8_CHARSET));
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ByteArrayOutputStream copy = new ByteArrayOutputStream();
        OutputStream copying = new CopyingOutputStream(target, copy);
        IOUtils.copyLarge(is, copying);
        copying.close();
        copy.close();
        target.close();
        String target1 = new String(copy.toByteArray(), UTF8_CHARSET);
        String target2 = new String(target.toByteArray(), UTF8_CHARSET);
        assertEquals("Copying fail", source, target1);
        assertEquals("Main fail", source, target2);
    }
}
