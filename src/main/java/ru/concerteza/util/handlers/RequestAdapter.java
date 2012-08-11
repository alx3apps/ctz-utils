package ru.concerteza.util.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * User: alexey
 * Date: 8/9/12
 */

public interface RequestAdapter<T> {
    void handle(Class<? extends T> clazz, Map<String, String> urlParams, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
