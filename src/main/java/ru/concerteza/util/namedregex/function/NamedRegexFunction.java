package ru.concerteza.util.namedregex.function;

import com.google.common.base.Function;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guava function, transforms input string into {@link NamedMatcher} using provided {@link NamedPattern}
 *
 * @author  alexey
 * Date: 2/9/12
 * @see NamedPattern
 */
@Deprecated // use com.github.tony19:named-regexp
public class NamedRegexFunction implements Function<String, NamedMatcher> {
    private final NamedPattern pattern;

    public NamedRegexFunction(NamedPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * @param input string to match regex
     * @return {@link NamedMatcher}
     */
    @Override
    public NamedMatcher apply(@Nullable String input) {
        checkNotNull(input);
        return pattern.matcher(input);
    }
}
