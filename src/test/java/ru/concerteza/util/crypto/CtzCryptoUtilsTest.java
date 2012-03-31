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

// todo fixme
public class CtzCryptoUtilsTest {
    private static final String AES_KEY = "One Ring to rule them all, .....";

    @Test
    public void dummy() {
        // I'm dummy
    }

//    @Test
//    public void testEncryptDecryptAESLong() {
//        String secret = random(42001);
//        String encrypted = CtzSHAUtils.encryptAES(secret, AES_KEY);
//        String decrypted = CtzSHAUtils.decryptAES(encrypted, AES_KEY);
//        assertEquals("decrypted not equals", secret, decrypted);
//    }
//
//    @Test
//    public void testEncryptDecryptAESShort() {
//        String secret = "foo";
//        String encrypted = CtzSHAUtils.encryptAES(secret, AES_KEY);
//        String decrypted = CtzSHAUtils.decryptAES(encrypted, AES_KEY);
//        assertEquals("decrypted not equals", secret, decrypted);
//    }
//
//    @Test
//    public void testSha1Sum() {
//        String fooSum = "0beec7b5ea3f0fdbc95d0dd47f3c5bc275da8a33";
//        String res = CtzSHAUtils.sha1Digest("foo");
//        assertEquals("sha1 fail", fooSum, res);
//    }
//
//    @Test
//    public void testSha256Sum() throws NoSuchProviderException, NoSuchAlgorithmException {
//        String fooSum = "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae";
//        String res = CtzSHAUtils.sha256Digest("foo");
//        String jdkRes = CtzSHAUtils.sha256JDKDigest("foo");
//        assertEquals("sha1 fail", fooSum, res);
//        assertEquals("sha1 jdk fail", fooSum, jdkRes);
//    }
//
//    @Test
//    public void testCreateKey() {
//        String secret = "foo";
//        String key1 = CtzSHAUtils.createKey("foo", "bar");
//        String encrypted = CtzSHAUtils.encryptAES(secret, key1);
//        String key2 = CtzSHAUtils.createKey("foo", "bar");
//        String decrypted = CtzSHAUtils.decryptAES(encrypted, key2);
//        assertEquals("decrypted not equals", secret, decrypted);
//    }
//
//        public static String sha256JDKDigest(String str) throws NoSuchProviderException, NoSuchAlgorithmException {
//        byte[] data = str.getBytes(UTF8_CHARSET);
//        MessageDigest sha256 = MessageDigest.getInstance("SHA-256", "SUN");
//        byte[] hash = sha256.digest(data);
//        return Hex.encodeHexString(hash);
//    }

}
