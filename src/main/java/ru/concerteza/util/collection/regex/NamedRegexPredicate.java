package ru.concerteza.util.collection.regex;

import com.google.common.base.Predicate;
import ru.concerteza.util.namedregex.NamedMatcher;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class NamedRegexPredicate implements Predicate<NamedMatcher> {
    @Override
    public boolean apply(@Nullable NamedMatcher input) {
        checkNotNull(input);
        return input.matches();
    }
}
