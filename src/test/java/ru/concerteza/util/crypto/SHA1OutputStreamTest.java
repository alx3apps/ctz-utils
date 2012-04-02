package ru.concerteza.util.crypto;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;

/**
 * User: alexey
 * Date: 4/2/12
 */
public class SHA1OutputStreamTest {

    @Test
    public void test() throws IOException {
        byte[] data = "foo".getBytes("UTF-8");
        InputStream is = new ByteArrayInputStream(data);
        SHA1OutputStream sha = new SHA1OutputStream(NULL_OUTPUT_STREAM);
        IOUtils.copy(is, sha);
        String digest = sha.digest();
        Assert.assertEquals("0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33", digest);
    }
}
