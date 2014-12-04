package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

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
    
    private boolean closed = false;
    
    public FullCopyingReader(Reader target, Writer copy) {
        super(target, copy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        IOUtils.copyLarge(source, copy);
        super.close();
    }
}
