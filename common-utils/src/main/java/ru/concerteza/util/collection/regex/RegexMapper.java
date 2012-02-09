package ru.concerteza.util.collection.regex;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class RegexMapper implements Function<String, Matcher> {
    private final Pattern pattern;

    public RegexMapper(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Matcher apply(@Nullable String input) {
        checkNotNull(input);
        return pattern.matcher(input);
    }
}
