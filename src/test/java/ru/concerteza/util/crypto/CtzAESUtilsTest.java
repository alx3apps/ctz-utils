package ru.concerteza.util.crypto;

import com.google.common.base.Function;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import ru.concerteza.util.string.CtzConstants;

import javax.annotation.Nullable;
import java.io.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.apache.commons.lang3.StringUtils.reverse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 12/11/10
 */

public class CtzAESUtilsTest {
    private static final byte[] KEY = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
    private static final byte[] IV = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private static final String KEY_STRING = "One Ring to rule them all, .....";

    @Test
    public void testEncryptDecryptShort() {
        String secret = "foo";
        String encrypted = CtzAESUtils.encrypt(secret, KEY_STRING);
        String decrypted = CtzAESUtils.decrypt(encrypted, KEY_STRING);
        assertEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptExact() {
        String secret = "16byteslongstrin";
        String encrypted = CtzAESUtils.encrypt(secret, KEY_STRING);
        String decrypted = CtzAESUtils.decrypt(encrypted, KEY_STRING);
        assertEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptEmpty() {
        String secret = "";
        String encrypted = CtzAESUtils.encrypt(secret, KEY_STRING);
        String decrypted = CtzAESUtils.decrypt(encrypted, KEY_STRING);
        assertEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptLong() {
        String secret = randomAscii(42001);
        String encrypted = CtzAESUtils.encrypt(secret, KEY_STRING);
        String decrypted = CtzAESUtils.decrypt(encrypted, KEY_STRING);
        assertEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptHashed() {
        byte[] secret = randomAscii(42).getBytes(CtzConstants.UTF8_CHARSET);
        byte[] key = CtzAESUtils.createHashedKey(KEY_STRING, SecretFun.INSTANCE);
        byte[] encrypted = CtzAESUtils.encrypt(secret, key);
        byte[] decrypted = CtzAESUtils.decrypt(encrypted, key);
        assertArrayEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptRandom() {
        byte[] secret = randomAscii(42).getBytes(CtzConstants.UTF8_CHARSET);
        byte[] key = CtzAESUtils.createRandomKey();
        byte[] encrypted = CtzAESUtils.encrypt(secret, key);
        byte[] decrypted = CtzAESUtils.decrypt(encrypted, key);
        assertArrayEquals("decrypted not equals", secret, decrypted);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptDecryptKeyIAE() {
        CtzAESUtils.createKey("foo");
    }

    @Test
    public void testEncryptDecryptKey() {
        byte[] secret = randomAscii(42).getBytes(CtzConstants.UTF8_CHARSET);
        byte[] key = CtzAESUtils.createKey("foo" + KEY_STRING);
        byte[] encrypted = CtzAESUtils.encrypt(secret, key);
        byte[] decrypted = CtzAESUtils.decrypt(encrypted, key);
        assertArrayEquals("decrypted not equals", secret, decrypted);
    }

    @Test
    public void testEncryptDecryptStream() throws IOException {
        byte[] secret = randomAscii(42).getBytes(CtzConstants.UTF8_CHARSET);
        InputStream secretIn = new ByteArrayInputStream(secret);
        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        CtzAESUtils.encryptStream(secretIn, encryptedOut, KEY, IV);
        encryptedOut.close();
        byte[] encrypted = encryptedOut.toByteArray();
        InputStream encryptedStream = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();
        CtzAESUtils.decryptStream(encryptedStream, decryptedOut, KEY, IV);
        decryptedOut.close();
        byte[] decrypted = decryptedOut.toByteArray();
        assertArrayEquals("decrypted not equals", secret, decrypted);
    }

    private enum SecretFun implements Function<String, String> {
        INSTANCE;
        @Override
        public String apply(@Nullable String input) {
            // don't do this in code, use real random generators instead
            String salt = RandomStringUtils.random(42);
            return salt + reverse(input);
        }
    }
}
