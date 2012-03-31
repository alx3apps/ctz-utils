package ru.concerteza.util.collection.regex;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class RegexPredicate implements Predicate<Matcher> {
    @Override
    public boolean apply(@Nullable Matcher input) {
        checkNotNull(input);
        return input.matches();
    }
}
