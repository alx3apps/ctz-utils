package ru.concerteza.util.collection.string;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class LowerStringMapper implements Function<String, String> {
    @Override
    public String apply(@Nullable String input) {
        checkNotNull(input);
        return input.toLowerCase();
    }
}
