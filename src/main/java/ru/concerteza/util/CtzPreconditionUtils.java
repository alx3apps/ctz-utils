package ru.concerteza.util;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzPreconditionUtils {
    public static void checkArg(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
        }
    }
}
