package ru.concerteza.util.string.predicate;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Predicate that checks that string is not empty
 *
 * @author alexkasko
 * Date: 7/13/13
 */
public class NotEmptyPredicate implements Predicate<String> {
    public static final Predicate<String> NOT_EMPTY_PREDICATE = new NotEmptyPredicate();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(@Nullable String input) {
        checkNotNull(input);
        return isNotEmpty(input);
    }
}
