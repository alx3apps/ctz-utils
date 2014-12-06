package ru.concerteza.util.net.diriterator;

import java.io.Closeable;
import java.util.Iterator;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public interface DirIterator<T extends RemoteFile> extends Iterator<T>, Closeable {

    DirIterator<T> open();

    @Override
    void close();
}
