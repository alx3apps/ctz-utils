package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class CopyingReaderTest {

    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        Reader is = new StringReader(source);
        StringWriter out1 = new StringWriter();
        StringWriter out2 = new StringWriter();
        Reader copying = new CopyingReader(is, out1);
        IOUtils.copyLarge(copying, out2);
        copying.close();
        out1.close();
        out2.close();
        assertEquals("Copying fail", source, out1.toString());
        assertEquals("Main fail", source, out2.toString());
    }
}
