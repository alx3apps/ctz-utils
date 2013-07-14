package ru.concerteza.util.string.predicate;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate than test regex pattern
 *
 * @author alexkasko
 * Date: 7/13/13
 */
public class RegexPredicate implements Predicate<String> {
    private final Pattern pattern;

    /**
     * Constructor
     *
     * @param regex regex string
     */
    public RegexPredicate(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(@Nullable String input) {
        checkNotNull(input);
        return pattern.matcher(input).matches();
    }
}
