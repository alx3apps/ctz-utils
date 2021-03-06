package ru.concerteza.util.string.function;

import com.google.common.base.Function;

import javax.annotation.Nullable;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guava function, transforms provided String to lower case
 *
 * @author alexey
 * Date: 2/9/12
 */
public class LowerStringFunction implements Function<String, String> {
    @Deprecated
    public static final Function<String, String> INSTANCE = new LowerStringFunction();
    public static final Function<String, String> LOWER_STRING_FUNCTION = new LowerStringFunction();

    /**
     * @param input string
     * @return <code>input.toLowerCase()</code>
     */
    @Override
    public String apply(@Nullable String input) {
        checkNotNull(input);
        return input.toLowerCase(Locale.ENGLISH);
    }
}
