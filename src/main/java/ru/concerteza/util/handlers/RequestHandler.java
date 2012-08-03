package ru.concerteza.util.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * User: alexey
 * Date: 7/22/12
 */
public interface RequestHandler {
    Pattern pattern();

    void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
