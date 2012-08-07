package ru.concerteza.util.handlers;

import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * User: alexey
 * Date: 8/7/12
 */
public class HandlersMappingEntry {
    private final NamedPattern pattern;
    private final Class<? extends RequestHandler> clazz;

    public HandlersMappingEntry(String pattern, Class<? extends RequestHandler> clazz) {
        checkArgument(isNotBlank(pattern), "Provided pattern is blank");
        checkNotNull(clazz, "Provided handler class is null");
        this.pattern = NamedPattern.compile(pattern);
        this.clazz = clazz;
    }

    public static HandlersMappingEntry of(String pattern, Class<? extends RequestHandler> clazz) {
        return new HandlersMappingEntry(pattern, clazz);
    }

    public NamedMatcher matcher(String url) {
        return pattern.matcher(url);
    }

    public Class<? extends RequestHandler> handlerClass() {
        return clazz;
    }
}
