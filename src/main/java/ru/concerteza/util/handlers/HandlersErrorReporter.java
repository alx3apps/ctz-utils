package ru.concerteza.util.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: alexey
 * Date: 8/8/12
 */
@Deprecated// use com.alexkasko.rest:com.alexkasko.rest
public interface HandlersErrorReporter {
    void reportException(HttpServletRequest req, HttpServletResponse res, Exception e);

    void report404(HttpServletRequest req, HttpServletResponse res);
}
