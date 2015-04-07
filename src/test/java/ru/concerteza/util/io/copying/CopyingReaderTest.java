package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
        StringWriter copy = new StringWriter();
        StringWriter target = new StringWriter();
        Reader copying = new CopyingReader(is, copy);
        IOUtils.copyLarge(copying, target);
        copying.close();
        copy.close();
        target.close();
        assertEquals("Copying fail", source, copy.toString());
        assertEquals("Main fail", source, target.toString());
    }
}
