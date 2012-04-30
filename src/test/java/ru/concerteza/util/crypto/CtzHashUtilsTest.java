package ru.concerteza.util.crypto;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import ru.concerteza.util.CtzConstants;
import ru.concerteza.util.io.CtzIOUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;
import static ru.concerteza.util.io.CtzIOUtils.createTmpFile;

/**
 * User: alexey
 * Date: 12/11/10
 */

public class CtzHashUtilsTest {
    private static final String FOO_SHA1 = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33";

    @Test
    public void testSha1Sum() throws NoSuchProviderException, NoSuchAlgorithmException {

        String jdkRes = sha1JDKDigest("foo");
        assertEquals("sha1 fail", FOO_SHA1, jdkRes);
        String res = CtzHashUtils.sha1Digest("foo");
        assertEquals("sha1 fail", FOO_SHA1, res);
    }

    private String sha1JDKDigest(String str) throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] data = str.getBytes(UTF8_CHARSET);
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1", "SUN");
        byte[] hash = sha1.digest(data);
        return Hex.encodeHexString(hash);
    }

    @Test
    public void testFileSha1() throws IOException {
        File file = createTmpFile(getClass());
        writeStringToFile(file, "foo", CtzConstants.UTF8);
        String sha1 = CtzHashUtils.sha1Digest(file);
        file.delete();
        assertEquals(FOO_SHA1, sha1);
    }
}
