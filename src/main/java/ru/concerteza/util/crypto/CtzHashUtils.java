package ru.concerteza.util.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.output.NullOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.springframework.core.io.Resource;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;


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

    public static String sha1Digest(File file) throws IOException {
        String url = format("file:{}", file.getPath());
        return sha1ResourceDigest(url);
    }

    public static String sha1ResourceDigest(String url) throws IOException {
        InputStream is = null;
        try {
            Resource resource = RESOURCE_LOADER.getResource(url);
            if(!resource.exists()) throw new IOException(format("Resource: '{}' doesn't exist", url));
            is = resource.getInputStream();
            SHA1InputStream sha1 = new SHA1InputStream(is);
            copyLarge(sha1, new NullOutputStream());
            return sha1.digest();
        } finally {
            closeQuietly(is);
        }
    }
}