package ru.concerteza.util.except;

import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.option.Option;

import static org.apache.commons.lang.exception.ExceptionUtils.getThrowableList;
import static org.apache.commons.lang.exception.ExceptionUtils.indexOfType;

/**

 *
 * Wrapper over Apache's <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/exception/ExceptionUtils.html">ExceptionUtils</a>
 * See usage example in {@link CtzExceptionUtilsTest}
 *
 * @author alexey,
 * Date: 11/19/11
 * @see MessageException
 */
public class CtzExceptionUtils {
    /**
     * Looks for {@link MessageException} in nested exceptions stack
     * and returns business error message, if any
     *
     * @param e some exception, thrown by application, that may contain {@link MessageException} in cause stack
     * @return {@link MessageException} found in cause stack wrapped into {@link ru.concerteza.util.option.Some},
     * {@link ru.concerteza.util.option.None} otherwise
     */
    public static Option<MessageException> extractMessage(Exception e) {
        if (MessageException.class.isAssignableFrom(e.getClass())) return Option.some((MessageException) e);
        int index = indexOfType(e, MessageException.class);
        if (index > 0) {
            MessageException cause = (MessageException) getThrowableList(e).get(index);
            return Option.some(cause);
        }
        return Option.none();
    }

    /**
     * @param e throwable to wrap
     * @return input throwable, wrapped into runtime exception if necessary
     */
    public static RuntimeException runtimeException(Throwable e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new UnhandledException(e);
    }
}
