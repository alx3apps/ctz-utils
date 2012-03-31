package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * User: alexey
 * Date: 12/6/11
 */

// use this class if main reader closes reader before EOF, but you need exact input copy
public class FullCopyingReader extends CopyingReader {
    public FullCopyingReader(Reader target, Writer copy) {
        super(target, copy);
    }

    @Override
    public void close() throws IOException {
        IOUtils.copyLarge(source, copy);
        super.close();
    }
}
