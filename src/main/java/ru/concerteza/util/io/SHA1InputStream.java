package ru.concerteza.util.io;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 3/31/12
 */
public class SHA1InputStream extends InputStream {
    private final Digest sha1 = new SHA1Digest();
    private byte[] digest;
    private final InputStream target;

    public SHA1InputStream(InputStream target) {
        checkNotNull(target);
        this.target = target;
    }

    @Override
    public int read() throws IOException {
        int res = target.read();
        if(-1 != res) sha1.update((byte) res);
        return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = target.read(b, off, len);
        if(-1 != res) sha1.update(b, off, res);
        return res;
    }

    @Override
    public long skip(long n) throws IOException {
        return target.skip(n);
    }

    @Override
    public int available() throws IOException {
        return target.available();
    }

    @Override
    public void close() throws IOException {
        target.close();
    }

    public byte[] digestBytes() {
        if (null == digest) {
            digest = new byte[sha1.getDigestSize()];
            sha1.doFinal(digest, 0);
        }
        return digest;
    }

    public String digest() {
        byte[] dig = digestBytes();
        return Hex.encodeHexString(dig);
    }
}
