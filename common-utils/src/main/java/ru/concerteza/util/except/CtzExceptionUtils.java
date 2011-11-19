package ru.concerteza.util.except;

import org.apache.commons.lang.exception.ExceptionUtils;
import ru.concerteza.util.option.Option;

import static org.apache.commons.lang.exception.ExceptionUtils.getThrowableList;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class CtzExceptionUtils {
    public static Option<MessageException> extractMessage(Exception e) {
        if (e instanceof MessageException) return Option.some((MessageException) e);
        int index = ExceptionUtils.indexOfType(e, MessageException.class);
        if (index > 0) {
            MessageException cause = (MessageException) getThrowableList(e).get(index);
            return Option.some(cause);
        }
        return Option.none();
    }
}
