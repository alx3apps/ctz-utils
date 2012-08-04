package ru.concerteza.util.io;

import org.apache.commons.io.LineIterator;
import ru.concerteza.util.string.CtzConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import static org.apache.commons.io.FileUtils.openInputStream;

/**
 * User: alexey
 * Date: 7/12/12
 */
public class CloseFileOnFinishLineIterator implements Iterator<String> {
    private final LineIterator li;

    public CloseFileOnFinishLineIterator(File file) {
        this(file, CtzConstants.UTF8);
    }

    public CloseFileOnFinishLineIterator(File file, String encoding) {
        try {
            this.li = new LineIterator(new InputStreamReader(openInputStream(file), encoding));
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if(li.hasNext()) return true;
        li.close();
        return false;
    }

    @Override
    public String next() {
        return li.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
