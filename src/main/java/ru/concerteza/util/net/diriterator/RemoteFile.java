package ru.concerteza.util.net.diriterator;

import ru.concerteza.util.io.finishable.Finishable;

import java.io.InputStream;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public interface RemoteFile extends Finishable<Void, Boolean> {

    InputStream open();

    String getName();

    String getPath();

    String getSuccessPath();

    String getErrorPath();
}
