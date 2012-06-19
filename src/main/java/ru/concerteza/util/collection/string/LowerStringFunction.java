package ru.concerteza.util.collection.string;

import com.google.common.base.Function;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guava function, transforms provided String to lower case
 *
 * @author alexey
 * Date: 2/9/12
 */
public class LowerStringFunction implements Function<String, String> {
    /**
     * @param input string
     * @return <code>input.toLowerCase()</code>
     */
    @Override
    public String apply(@Nullable String input) {
        checkNotNull(input);
        return input.toLowerCase();
    }
}
