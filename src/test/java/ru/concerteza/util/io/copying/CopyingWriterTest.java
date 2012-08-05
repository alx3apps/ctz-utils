package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 12/6/11
 */
public class CopyingWriterTest {

    @Test
    public void test() throws IOException {
        String source = RandomStringUtils.random(42001);
        Reader is = new StringReader(source);
        StringWriter copy = new StringWriter();
        StringWriter target = new StringWriter();
        Writer copying = new CopyingWriter(target, copy);
        IOUtils.copyLarge(is, copying);
        copying.close();
        copy.close();
        target.close();
        assertEquals("Copying fail", source, copy.toString());
        assertEquals("Main fail", source, target.toString());
    }
}
