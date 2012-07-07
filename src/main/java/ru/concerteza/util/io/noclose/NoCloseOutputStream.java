package ru.concerteza.util.io.noclose;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 11/19/11
 */

// prevent rough libs from closing or flushing my streams
public class NoCloseOutputStream extends OutputStream {
    private final OutputStream target;

    private NoCloseOutputStream(OutputStream target) {
        checkNotNull(target, "Provided output stream is null");
        this.target = target;
    }

    public static NoCloseOutputStream of(OutputStream target) {
        return new NoCloseOutputStream(target);
    }

    @Override
    public void write(int b) throws IOException {
        target.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        target.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        // this line is intentionally left blank
    }

    @Override
    public void close() throws IOException {
        // this line is intentionally left blank
    }
}
