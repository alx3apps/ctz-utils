package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link CopyingReader} implementation, copies unread source chars until EOF on
 * {@code close()} call. Use this class if client closes reader before EOF,
 * but you need full input copy
 *
 * @author  alexey
 * Date: 12/6/11
 * @see FullCopyingReaderTest
 */
public class FullCopyingReader extends CopyingReader {

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public FullCopyingReader(Reader target, Writer copy) {
        super(target, copy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (closed.get()) return;
        try {
            IOUtils.copyLarge(source, copy);
        } finally {
            IOUtils.closeQuietly(source);
            closed.set(true);
        }
    }
}
