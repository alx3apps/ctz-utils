package ru.concerteza.util.io.copying;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class FullCopyingReaderTest {
    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        Reader is = new StringReader(source);
        StringWriter out1 = new StringWriter();
        Reader copying = new FullCopyingReader(is, out1);
        copying.read(new char[40000], 0,  40000);
        copying.close();
        out1.close();
        assertEquals("Copying fail", source, out1.toString());
    }
}
