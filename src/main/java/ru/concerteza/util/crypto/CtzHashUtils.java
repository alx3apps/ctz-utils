package ru.concerteza.util.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.NullOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.core.io.Resource;
import ru.concerteza.util.io.RuntimeIOException;
import ru.concerteza.util.io.SHA1InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;
import static ru.concerteza.util.string.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;


/**
 * Hash utility methods over Bouncy Castle SHA1 and MD5 implementations.
 *
 * @author alexey
 */
public class CtzHashUtils {

    /**
     * @param str input string
     * @return SHA1 digest
     */
    public static String sha1Digest(String str) {
        byte[] dig = sha1DigestBytes(str);
        byte[] hex = Hex.encode(dig);
        return new String(hex, UTF8_CHARSET);
    }

    /**
     * @param str input string
     * @return SHA1 digest
     */
    public static byte[] sha1DigestBytes(String str) {
        checkNotNull(str, "Provided string is null");
        byte[] data = str.getBytes(UTF8_CHARSET);
        return sha1DigestBytes(data);
    }

    /**
     * @param data input data
     * @return SHA1 digest
     */
    public static byte[] sha1DigestBytes(byte[] data) {
        checkNotNull(data, "Provided data is null");
        Digest digest = new SHA1Digest();
        digest.update(data, 0, data.length);
        byte[] dig = new byte[digest.getDigestSize()];
        digest.doFinal(dig, 0);
        return dig;
    }

    /**
     * @param file input file
     * @return SHA1 digest of file contents
     * @throws RuntimeIOException
     */
    public static String sha1Digest(File file) {
        String url = "file:" + file.getPath();
        return sha1ResourceDigest(url);
    }

    /**
     * @param path input resource path
     * @return SHA1 digest of resource contents
     * @throws RuntimeIOException
     */
    public static String sha1ResourceDigest(String path) {
        InputStream is = null;
        try {
            Resource resource = RESOURCE_LOADER.getResource(path);
            if(!resource.exists()) throw new RuntimeIOException(format("Resource: '{}' doesn't exist", path));
            is = resource.getInputStream();
            SHA1InputStream sha1 = new SHA1InputStream(is);
            copyLarge(sha1, new NullOutputStream());
            return sha1.digest();
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            closeQuietly(is);
        }
    }

    /**
     * @param data input data
     * @return MD5 digest as hex string
     */
    public static String md5Digest(byte[] data) {
        MD5Digest digest = new MD5Digest();
        digest.update(data, 0, data.length);
        byte[] dig = new byte[digest.getDigestSize()];
        digest.doFinal(dig, 0);
        byte[] hex = Hex.encode(dig);
        return new String(hex, UTF8_CHARSET);
    }
}