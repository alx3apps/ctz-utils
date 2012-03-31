package ru.concerteza.util.except;

import org.apache.commons.lang.exception.ExceptionUtils;
import ru.concerteza.util.option.Option;

import static org.apache.commons.lang.exception.ExceptionUtils.getThrowableList;

/**
 * User: alexey
 * Date: 11/19/11
 *
 * Wrapper over Apache's {@link <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/exception/ExceptionUtils.html">ExceptionUtils</a>}.
 * Usage example is in CtzExceptionUtilsTest.
 *
 */
public class CtzExceptionUtils {
    /**
     * Looks for {@link MessageException} in nested exceptions stack
     * and returns business error message, id any.
     * @param e some exception thrown by application, may contain
     * @return {@link MessageException} if found in stack, Option.none() otherwise
     */
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
