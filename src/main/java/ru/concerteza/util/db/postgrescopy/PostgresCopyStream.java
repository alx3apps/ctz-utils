package ru.concerteza.util.db.postgrescopy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.arraycopy;

/**
 * Wraps records iterator into postgres binary copy stream for using
 * copy API in "pull" mode (it also mey be used in "push" mode)
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public class PostgresCopyStream extends InputStream {
    private enum State {HEADER, BODY, EOF}

    private static final byte[] HEADER_BYTES = new byte[]{0x50, 0x47, 0x43, 0x4f, 0x50, 0x59, 0x0a, (byte)0xff, 0x0d, 0x0a,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] EOF_BYTES = new byte[] {(byte) 0xff, (byte) 0xff};

    private Iterator<byte[]> iter;
    private final byte[] data = new byte[1<<17];
    private int datalen;
    private int pos;
    private byte[] single = new byte[1];
    private PostgresCopyProvider record;
    private State state = State.HEADER;

    /**
     * Constructor
     *
     * @param data rows iterator
     * @param provider contains logic for copying row into copy buffer
     */
    public PostgresCopyStream(Iterator<byte[]> data, PostgresCopyProvider provider) {
        checkNotNull(data, "Provided data is null");
        checkNotNull(provider, "Provided provider is null");
        this.iter = data;
        this.record = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        int cp = read(single, 0, 1);
        if(-1 == cp) return -1;
        return single[0] & 0xff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int copied = 0;
        switch (state) {
            case HEADER:
                copied += copyStatic(b, off, len, HEADER_BYTES);
                if(len == copied) return len;
                state = State.BODY;
                pos = 0;
            case BODY:
                while (copied < len) {
                    int cp = copyData(b, off + copied, len - copied);
                    copied += cp;
                    if (0 == cp) {
                        if (!iter.hasNext()) {
                            state = State.EOF;
                            pos = 0;
                            break;
                        }
                        byte[] src = iter.next();
                        datalen = record.fillCopyBuf(src, data);
                        pos = 0;
                    }
                }
            case EOF:
                if(State.BODY.equals(state)) return copied;
                copied += copyStatic(b, off + copied, len - copied, EOF_BYTES);
                return 0 == copied ? -1 : copied;
            default: throw new IllegalStateException();
        }
    }

    private int copyStatic(byte[] b, int off, int len, byte[] src) {
        if(src.length == pos) return 0;
        int cp = Math.min(src.length - pos, len);
        arraycopy(src, pos, b, off, cp);
        pos += cp;
        return cp;
    }

    private int copyData(byte[] b, int off, int len) {
        if(datalen == pos || 0 == datalen) return 0;
        int cp = Math.min(datalen - pos, len);
        arraycopy(data, pos, b, off, cp);
        pos += cp;
        return cp;
    }
}
