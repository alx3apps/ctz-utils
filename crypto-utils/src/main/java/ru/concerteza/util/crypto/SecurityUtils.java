package ru.concerteza.util.crypto;

import java.security.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.crypto.prng.VMPCRandomGenerator;

import static java.lang.System.arraycopy;
import static org.apache.commons.lang.StringUtils.reverse;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;
import static ru.concerteza.util.CtzFormatUtils.format;


/**
 * User: mitrofan
 * Date: 25.08.2009
 */
public class SecurityUtils {
    // enables AES-256
    private static final int AES_KEY_SIZE = 32;
    private static final int AES_IV_SIZE = 16;
    private static final RandomGenerator RANDOM_GENERATOR;
    // init threadsafe random generator
    static {
        // large seed can hangs for long time cause of /dev/urandom
        byte[] seed = new SecureRandom().generateSeed(8);
        RANDOM_GENERATOR = new VMPCRandomGenerator();
        RANDOM_GENERATOR.addSeedMaterial(seed);
    }

    // first 16 bytes are base64 IV
    public static String encryptAES(String message, String keyString) {
        // convert to bytes
        byte[] key = keyString.getBytes(UTF8_CHARSET);
        validateAESKey(key);
        byte[] messageBytes = message.getBytes(UTF8_CHARSET);
        // create IV
        byte[] iv = new byte[AES_IV_SIZE];
        RANDOM_GENERATOR.nextBytes(iv);
        // encrypt
        byte[] encrypted = AESSupport.encryptArray(messageBytes, key, iv);
        // concat IV to encrypted
        byte[] res = new byte[iv.length + encrypted.length];
        arraycopy(iv, 0, res, 0, iv.length);
        arraycopy(encrypted, 0, res, iv.length, encrypted.length);
        // return base64 string
        return Base64.encodeBase64String(res);
    }

    // first 16 bytes must be base64 IV
    public static String decryptAES(String encryptedBase64, String keyString) {
        // convert to bytes
        byte[] key = keyString.getBytes(UTF8_CHARSET);
        validateAESKey(key);
        byte[] encryptedWithIV = Base64.decodeBase64(encryptedBase64);
        // get IV from input
        byte[] iv = new byte[AES_IV_SIZE];
        byte[] encrypted = new byte[encryptedWithIV.length - iv.length];
        arraycopy(encryptedWithIV, 0, iv, 0, iv.length);
        arraycopy(encryptedWithIV, iv.length, encrypted, 0, encrypted.length);
        // decrypt
        byte[] bytes = AESSupport.decryptArray(encrypted, key, iv);
        // return UTF-8 String
        return new String(bytes, UTF8_CHARSET);
    }

    private static void validateAESKey(byte[] key) {
        if (AES_KEY_SIZE != key.length) throw new IllegalArgumentException(format(
                "AES key must be UTF-8 string with length = {}, but was: {}. " +
                        "Length measured in BYTES, NOT CHARS", AES_KEY_SIZE, key.length));
    }

    public static String sha1Digest(String str) {
        byte[] data = str.getBytes(UTF8_CHARSET);
        Digest digest = new SHA1Digest();
        digest.update(data, 0, data.length);
        byte[] dig = new byte[digest.getDigestSize()];
        digest.doFinal(dig, 0);
        return Hex.encodeHexString(dig);
    }

    public static String createKey(String key, String salt) {
        String source = salt + reverse(key);
        String hash = sha1Digest(source);
        return hash.substring(8);
    }

}