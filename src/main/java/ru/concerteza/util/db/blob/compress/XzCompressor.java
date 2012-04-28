package ru.concerteza.util.db.blob.compress;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class XzCompressor extends AbstractCompressor {
    private final int level;

    public XzCompressor() {
        this(3);
    }

    public XzCompressor(int level) {
        this.level = level;
    }

    @Override
    protected OutputStream wrapCompressInternal(OutputStream out) throws IOException {
        return new XZOutputStream(out, new LZMA2Options(level));
    }

    @Override
    protected InputStream wrapDecompressInternal(InputStream in) throws IOException {
        return new XZInputStream(in);
    }
}
