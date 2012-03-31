package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 12/6/11
 */


// use this class if main reader closes input stream before EOF, but you need exact input copy
public class FullCopyingInputStream extends CopyingInputStream {

    public FullCopyingInputStream(InputStream source, OutputStream copy) {
        super(source, copy);
    }

    @Override
    public void close() throws IOException {
        IOUtils.copyLarge(source, copy);
        super.close();
    }
}
