package ru.concerteza.util.crypto;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.concerteza.util.io.SHA1InputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;
import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 3/30/12
 */

public class SHA1InputStreamTest {

    @Test
    public void test() throws IOException {
        byte[] data = "foo".getBytes("UTF-8");
        InputStream is = new ByteArrayInputStream(data);
        SHA1InputStream sha = new SHA1InputStream(is);
        IOUtils.copy(sha, NULL_OUTPUT_STREAM);
        String digest = sha.digest();
        Assert.assertEquals("0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33", digest);
    }
}
