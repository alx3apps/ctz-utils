package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.RestHandler;

import java.io.Reader;

/**
 * Interface for handling input readers
 *
 * @author alexkasko
 * Date: 7/13/13
 */
public interface JsonReaderHandler<O> extends RestHandler {
    /**
     * App specific request processing
     *
     * @param reader input reader
     * @return output object
     */
    O handle(Reader reader);
}
