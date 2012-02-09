package ru.concerteza.util.collection.regex;

import com.google.common.base.Function;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class NamedRegexMapper implements Function<String, NamedMatcher> {
    private final NamedPattern pattern;

    public NamedRegexMapper(NamedPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public NamedMatcher apply(@Nullable String input) {
        checkNotNull(input);
        return pattern.matcher(input);
    }
}
