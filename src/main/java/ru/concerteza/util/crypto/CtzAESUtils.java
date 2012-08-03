package ru.concerteza.util.crypto;

import com.google.common.base.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.UnhandledException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.crypto.prng.VMPCRandomGenerator;

import java.io.*;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.arraycopy;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.reverse;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.crypto.CtzHashUtils.sha1Digest;

/**
 * AES-256 encryption front-end over Bouncy Castle AES implementation.
 * Supports streaming with PKCS7 padding.
 *
 * @author alexey
 * Date: 12/11/10
 */
public class CtzAESUtils {
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

    /**
     * @param messageString message, will be read as UTF-8
     * @param keyString encryption key
     * @return encrypted string in base-64 format
     */
    public static String encrypt(String messageString, String keyString) {
        checkNotNull(messageString, "Provided message is null");
        checkArgument(isNotEmpty(keyString), "Provided key is empty");
        byte[] key = createKey(keyString);
        // convert to bytes
        byte[] message = messageString.getBytes(UTF8_CHARSET);
        byte[] encrypted = encrypt(message, key);
        // return base64 string
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * @param encryptedBase64 encrypted string in base-64 format
     * @param keyString encryption key
     * @return decrypted key
     */
    public static String decrypt(String encryptedBase64, String keyString) {
        checkNotNull(encryptedBase64, "Provided encrypted base-64 message is null");
        checkArgument(isNotEmpty(keyString), "Provided key is empty");
        byte[] key = createKey(keyString);
        // convert to bytes
        byte[] encryptedWithIV = Base64.decodeBase64(encryptedBase64);
        byte[] message = decrypt(encryptedWithIV, key);
        // return UTF-8 String
        return new String(message, UTF8_CHARSET);
    }

    /**
     * @return 32-bytes random key
     */
    public static byte[] createRandomKey() {
        byte[] res = new byte[AES_KEY_SIZE];
        RANDOM_GENERATOR.nextBytes(res);
        return res;
    }

    /**
     * @param key non-secure encryption key
     * @param salt salt for the key
     * @return salted, hashed and transformed encryption key, non-secure,
     * but better (through obscurity) than input one
     */
    @Deprecated // use createHashedKey(String, Function<String, String>) instead
    public static byte[] createHashedKey(String key, String salt) {
        String source = salt + reverse(key);
        String hash = sha1Digest(source);
        return createKey(hash);
    }

    /**
     * @param key non-secure encryption key
     * @param transformation transformation to apply to provided key before hashing; must NOT be available in public
     * @return salted, hashed and transformed encryption key, non-secure,
     * but better (through obscurity) than input one
     */
    public static byte[] createHashedKey(String key, Function<String, String> transformation) {
        String source = transformation.apply(key);
        String hash = sha1Digest(source);
        return createKey(hash);
    }

    /**
     * @param keyString encryption key, will be read as UTF-8
     * @return 32-bytes key representation
     */
    public static byte[] createKey(String keyString) {
        byte[] key = keyString.getBytes(UTF8_CHARSET);
        if (AES_KEY_SIZE > key.length) {
            throw new IllegalArgumentException(format(
                    "AES key must be UTF-8 string with length >= '{}', but was: '{}'. " +
                            "Length measured in bytes, not chars", AES_KEY_SIZE, key.length));
        } else if(AES_KEY_SIZE == key.length) {
            return key;
        } else {
            byte[] res = new byte[AES_KEY_SIZE];
            arraycopy(key, 0, res, 0, AES_KEY_SIZE);
            return res;
        }
    }

    /**
     * @return 16-bytes random initialization vector
     */
    public static byte[] createIV() {
        byte[] iv = new byte[AES_IV_SIZE];
        RANDOM_GENERATOR.nextBytes(iv);
        return iv;
    }

    /**
     * @param message message to encrypt
     * @param key 32-bytes encryption key
     * @return decrypted data with IV in first 16 bytes
     */
    public static byte[] encrypt(byte[] message, byte[] key) {
        validateKey(key);
        // create IV
        byte[] iv = createIV();
        // encrypt
        ByteArrayInputStream in = new ByteArrayInputStream(message);
        ByteArrayOutputStream out = new ByteArrayOutputStream(message.length);
        encryptStream(in, out, key, iv);
        byte[] encrypted = out.toByteArray();
        // concat IV to encrypted
        byte[] res = new byte[iv.length + encrypted.length];
        arraycopy(iv, 0, res, 0, iv.length);
        arraycopy(encrypted, 0, res, iv.length, encrypted.length);
        return res;
    }

    /**
     * @param encryptedWithIV decrypted data with IV in first 16 bytes
     * @param key 32-bytes encryption key
     * @return decrypted data
     */
    public static byte[] decrypt(byte[] encryptedWithIV, byte[] key) {
        validateKey(key);
        // get IV from input
        byte[] iv = new byte[AES_IV_SIZE];
        byte[] encrypted = new byte[encryptedWithIV.length - iv.length];
        arraycopy(encryptedWithIV, 0, iv, 0, iv.length);
        arraycopy(encryptedWithIV, iv.length, encrypted, 0, encrypted.length);
        // decrypt
        ByteArrayInputStream in = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream out = new ByteArrayOutputStream(encrypted.length);
        decryptStream(in, out, key, iv);
        return out.toByteArray();
    }

    /**
     * @param messageStream input stream to encrypt
     * @param encryptedStream stream to write encrypted data to
     * @param key 32-bytes encryption key
     * @param iv 16-bytes initialization vector
     */
    public static void encryptStream(InputStream messageStream, OutputStream encryptedStream, byte[] key, byte[] iv) {
        validateKey(key);
        validateIV(iv);
        try {
            int blockSize = key.length/2;
            // init
            BlockCipher cipher = createAESCipher(true, key, iv);
            byte[] inBuffer = new byte[blockSize];
            byte[] outBuffer = new byte[blockSize];
            int bytesRead;
            for (;;) {
                bytesRead = messageStream.read(inBuffer, 0, blockSize);
                if (bytesRead < blockSize) break; // last block
                cipher.processBlock(inBuffer, 0, outBuffer, 0);
                encryptedStream.write(outBuffer);
            }
            // process tail
            if(bytesRead > 0) { // tail exists
                new PKCS7Padding().addPadding(inBuffer, bytesRead);
                cipher.processBlock(inBuffer, 0, outBuffer, 0);
                encryptedStream.write(outBuffer);
            } else { // no tail
                byte[] paddingBuffer = new byte[blockSize];
                new PKCS7Padding().addPadding(paddingBuffer, 0);
                cipher.processBlock(paddingBuffer, 0, outBuffer, 0);
                encryptedStream.write(outBuffer);
            }
            encryptedStream.flush();
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * @param encryptedStream input stream to decrypt
     * @param messageStream stream to write decrypted data to
     * @param key 32-bytes encryption key
     * @param iv 16-bytes initialization vector
     */
    public static void decryptStream(InputStream encryptedStream, OutputStream messageStream, byte[] key, byte[] iv) {
        validateKey(key);
        validateIV(iv);
        try {
            int blockSize = key.length/2;
            // init
            BlockCipher cipher = createAESCipher(false, key, iv);
            byte[] inBuffer = new byte[blockSize];
            byte[] outBuffer = new byte[blockSize];
            encryptedStream.read(inBuffer, 0, blockSize);
            int bytesRead;
            for (;;) {
                cipher.processBlock(inBuffer, 0, outBuffer, 0);
                bytesRead = encryptedStream.read(inBuffer, 0, blockSize);
                if (bytesRead < blockSize) break; // last block reached
                messageStream.write(outBuffer);
            }
            // process tail
            if (-1 != bytesRead) throw new IllegalStateException(format("encryptedStream length with tail: {} doesn't divisible by block size: {}", bytesRead, key.length));
            int padStartPosition = new PKCS7Padding().padCount(outBuffer);
            messageStream.write(outBuffer, 0, blockSize - padStartPosition);
            messageStream.flush();
        } catch (IOException e) {
            throw new UnhandledException(e);
        } catch (InvalidCipherTextException e) {
            throw new UnhandledException(e);
        }
    }

    private static void validateKey(byte[] key) {
        if (AES_KEY_SIZE != key.length) throw new IllegalArgumentException(format(
                "AES key must have length = {}, but was: {}. " +
                        "Length measured in BYTES, NOT CHARS", AES_KEY_SIZE, key.length));
    }

    private static void validateIV(byte[] iv) {
        if (AES_IV_SIZE != iv.length) throw new IllegalArgumentException(format(
                "AES IV must must have length = {}, but was: {}.", AES_IV_SIZE, iv.length));
    }

    private static BlockCipher createAESCipher(boolean encrypt, byte[] key, byte[] iv) {
        CipherParameters cipherKey = new ParametersWithIV(new KeyParameter(key), iv);
        BlockCipher cipher = new CFBBlockCipher(new AESFastEngine(), (key.length/2) * 8);
        cipher.init(encrypt, cipherKey);
        return cipher;
    }
}
