package ru.concerteza.util.crypto;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 12/11/10
 */

public class CtzHashUtilsTest {

    @Test
    public void testSha1Sum() throws NoSuchProviderException, NoSuchAlgorithmException {
        String fooSum = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33";
        String jdkRes = sha1JDKDigest("foo");
        assertEquals("sha1 fail", fooSum, jdkRes);
        String res = CtzHashUtils.sha1Digest("foo");
        assertEquals("sha1 fail", fooSum, res);
    }

    private String sha1JDKDigest(String str) throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] data = str.getBytes(UTF8_CHARSET);
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1", "SUN");
        byte[] hash = sha1.digest(data);
        return Hex.encodeHexString(hash);
    }
}
