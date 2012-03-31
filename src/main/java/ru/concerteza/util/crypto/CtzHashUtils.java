package ru.concerteza.util.crypto;

import java.security.*;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;


/**
 * User: alexey
 */
public class CtzHashUtils {

    public static String sha1Digest(String str) {
        byte[] dig = sha1DigestBytes(str);
        return Hex.encodeHexString(dig);
    }

    public static byte[] sha1DigestBytes(String str) {
        byte[] data = str.getBytes(UTF8_CHARSET);
        return sha1DigestBytes(data);
    }

    public static byte[] sha1DigestBytes(byte[] data) {
        Digest digest = new SHA1Digest();
        digest.update(data, 0, data.length);
        byte[] dig = new byte[digest.getDigestSize()];
        digest.doFinal(dig, 0);
        return dig;
    }

    public static String sha1JDKDigest(String str) throws NoSuchProviderException, NoSuchAlgorithmException {
        byte[] data = str.getBytes(UTF8_CHARSET);
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1", "SUN");
        byte[] hash = sha1.digest(data);
        return Hex.encodeHexString(hash);
    }
}