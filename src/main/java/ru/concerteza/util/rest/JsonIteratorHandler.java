package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.RestHandler;

import java.util.Iterator;

/**
 * Interface for handling input JSON iterators
 *
 * @author alexkasko
 * Date: 7/12/13
 */
public interface JsonIteratorHandler<I, O> extends RestHandler {

    /**
     * App specific request processing
     *
     * @param input input object iterator parsed from json
     * @return output object
     */
    O handle(Iterator<I> input);

    /**
     * Returns input object class
     *
     * @return input object class
     */
    Class<I> inputClass();
}
