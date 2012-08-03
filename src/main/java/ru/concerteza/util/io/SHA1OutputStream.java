package ru.concerteza.util.io;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 3/30/12
 */
public class SHA1OutputStream extends OutputStream {
    private final Digest sha1 = new SHA1Digest();
    private byte[] digest;
    private final OutputStream target;

    public SHA1OutputStream(OutputStream target) {
        checkNotNull(target);
        this.target = target;
    }

    @Override
    public void write(int b) throws IOException {
        sha1.update((byte)b);
        target.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        sha1.update(b, off, len);
        target.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        target.flush();
    }

    @Override
    public void close() throws IOException {
        target.close();
    }

    public byte[] digestBytes() {
        if(null == digest) {
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
