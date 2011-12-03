package ru.concerteza.util.crypto;

import org.apache.commons.lang.UnhandledException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.*;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 12/11/10
 */


class AESSupport {

    static byte[] encryptArray(byte[] message, byte[] key, byte[] iv) {
        ByteArrayInputStream in = new ByteArrayInputStream(message);
        ByteArrayOutputStream out = new ByteArrayOutputStream(message.length);
        encryptStream(in, out, key, iv);
        return out.toByteArray();
    }

    static byte[] decryptArray(byte[] encrypted, byte[] key, byte[] iv) {
        ByteArrayInputStream in = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream out = new ByteArrayOutputStream(encrypted.length);
        decryptStream(in, out, key, iv);
        return out.toByteArray();
    }

    static void encryptStream(InputStream messageStream, OutputStream encryptedStream, byte[] key, byte[] iv) {
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

    static void decryptStream(InputStream encryptedStream, OutputStream messageStream, byte[] key, byte[] iv) {
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

    private static BlockCipher createAESCipher(boolean encrypt, byte[] key, byte[] iv) {
        CipherParameters cipherKey = new ParametersWithIV(new KeyParameter(key), iv);
        BlockCipher cipher = new CFBBlockCipher(new AESFastEngine(), (key.length/2) * 8);
        cipher.init(encrypt, cipherKey);
        return cipher;
    }
}
