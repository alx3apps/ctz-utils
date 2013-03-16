package ru.concerteza.util.string.function;

import com.google.common.base.Function;

/**
 * User: alexkasko
 * Date: 12/25/12
 */
public class EnumNameFunction implements Function<Enum<?>, String> {
    public static Function<Enum<?>, String> ENUM_NAME_FUNCTION = new EnumNameFunction();

    @Override
    public String apply(Enum<?> input) {
        return input.name();
    }
}
