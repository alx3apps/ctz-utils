package ru.concerteza.util.string.predicate;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Predicate that checks that string is not blank
 *
 * @author alexkasko
 * Date: 7/13/13
 */
public class NotBlankPredicate implements Predicate<String> {
    public static final Predicate<String> NOT_BLANK_PREDICATE = new NotBlankPredicate();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(@Nullable String input) {
        checkNotNull(input);
        return isNotBlank(input);
    }
}
