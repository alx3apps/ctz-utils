package ru.concerteza.util.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * User: alexey
 * Date: 8/9/12
 */
@Deprecated// use com.alexkasko.rest:com.alexkasko.rest
public interface HandlersProcessor<T> {
    void process(Class<? extends T> handlerClass, Map<String, String> urlParams, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
