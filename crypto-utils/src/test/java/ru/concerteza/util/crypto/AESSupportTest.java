package ru.concerteza.util.crypto;

import org.junit.Test;

import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 12/11/10
 */
public class AESSupportTest {
    private static final byte[] KEY = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
    private static final byte[] IV = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

    @Test
    public void testEncryptDecryptShort() {
        byte[] payload = "foo".getBytes(UTF8_CHARSET);
        byte[] encrypted = AESSupport.encryptArray(payload, KEY, IV);
        byte[] decrypted = AESSupport.decryptArray(encrypted, KEY, IV);
        String res = new String(decrypted, UTF8_CHARSET);
        assertEquals("foo", res);
    }

    @Test
    public void testEncryptDecryptExact() {
        String secret = "16byteslongstrin";
        byte[] payload = secret.getBytes(UTF8_CHARSET);
        byte[] encrypted = AESSupport.encryptArray(payload, KEY, IV);
        byte[] decrypted = AESSupport.decryptArray(encrypted, KEY, IV);
        String res = new String(decrypted, UTF8_CHARSET);
        assertEquals(secret, res);
    }

    @Test
    public void testEncryptDecryptEmpty() {
        String secret = "";
        byte[] payload = secret.getBytes(UTF8_CHARSET);
        byte[] encrypted = AESSupport.encryptArray(payload, KEY, IV);
        byte[] decrypted = AESSupport.decryptArray(encrypted, KEY, IV);
        String res = new String(decrypted, UTF8_CHARSET);
        assertEquals(secret, res);
    }

    @Test
    public void testEncryptDecryptLong() {
        String secret = random(42001);
        byte[] payload = secret.getBytes(UTF8_CHARSET);
        byte[] encrypted = AESSupport.encryptArray(payload, KEY, IV);
        byte[] decrypted = AESSupport.decryptArray(encrypted, KEY, IV);
        String res = new String(decrypted, UTF8_CHARSET);
        assertEquals(secret, res);
    }


}
