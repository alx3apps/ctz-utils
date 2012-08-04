package ru.concerteza.util.io.copying;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class FullCopyingInputStreamTest {
    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        InputStream is = new ByteArrayInputStream(source.getBytes(UTF8_CHARSET));
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        InputStream copying = new FullCopyingInputStream(is, out1);
        copying.read(new byte[40000], 0 , 40000);
        copying.close();
        out1.close();
        String target1 = new String(out1.toByteArray(), UTF8_CHARSET);
        assertEquals("Copying fail", source, target1);
    }
}
