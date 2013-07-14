package ru.concerteza.util.rest;

import com.alexkasko.rest.handlers.RestHandler;

/**
 * Generic JSON handler interface
 *
 * @author alexkasko
 * Date: 5/22/13
 */
public interface JsonHandler<I, O> extends RestHandler {

    /**
     * App specific request processing
     *
     * @param input input object parsed from json
     * @return output object
     */
    O handle(I input);

    /**
     * Returns input object class
     *
     * @return input object class
     */
    Class<I> inputClass();
}
