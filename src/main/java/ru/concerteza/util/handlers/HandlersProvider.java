package ru.concerteza.util.handlers;

/**
 * User: alexey
 * Date: 8/7/12
 */
public interface HandlersProvider {
    <T extends RequestHandler> T get(Class<T> clazz);
}
