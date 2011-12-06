package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import ru.concerteza.util.io.copying.CopyingInputStream;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class CopyingInputStreamTest {

    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        InputStream is = new ByteArrayInputStream(source.getBytes(UTF8_CHARSET));
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        InputStream copying = new CopyingInputStream(is, out1);
        IOUtils.copyLarge(copying, out2);
        copying.close();
        out1.close();
        out2.close();
        String target1 = new String(out1.toByteArray(), UTF8_CHARSET);
        String target2 = new String(out2.toByteArray(), UTF8_CHARSET);
        assertEquals("Copying fail", source, target1);
        assertEquals("Main fail", source, target2);
    }
}
