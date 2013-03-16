package ru.concerteza.util.except;

import com.google.common.base.Optional;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.option.Option;
import ru.concerteza.util.reflect.CtzReflectionUtils;

import javax.annotation.Nullable;

import java.lang.reflect.Constructor;

import static org.apache.commons.lang.exception.ExceptionUtils.getThrowableList;
import static org.apache.commons.lang.exception.ExceptionUtils.indexOfType;
import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeConstructor;
import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeDefaultConstructor;
import static ru.concerteza.util.string.CtzFormatUtils.format;

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
     * @return {@link MessageException} found in cause stack wrapped into {@code Present}, {@code Absent} otherwise
     */
    @SuppressWarnings("unchecked")
    public static <T extends Exception> Optional<T> extractMessage(Exception e, Class<T> clazz) {
        if (clazz.isAssignableFrom(e.getClass())) return Optional.of((T) e);
        int index = indexOfType(e, clazz);
        if (index > 0) {
            T cause = (T) getThrowableList(e).get(index);
            return Optional.of(cause);
        }
        return Optional.absent();
    }

    /**
     * @param e throwable to wrap
     * @return input throwable, wrapped into runtime exception if necessary
     */
    public static RuntimeException runtimeException(Throwable e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new UnhandledException(e);
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param exceptionClass exception class to throw
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    @Deprecated // tamper stacktraces
    public static <T extends RuntimeException> void check(boolean expression, Class<T> exceptionClass,
                                                          @Nullable Object errorMessage) {
      if (!expression) throw instantiate(exceptionClass, String.valueOf(errorMessage));
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param exceptionClass exception class to throw
     * @param errorMessageTemplate a template for the exception message should the
     *     check fail. The message is formed by replacing each {@code {}}
     *     placeholder in the template with an argument. These are matched by
     *     position - the first {@code {}} gets {@code errorMessageArgs[0]}, etc.
     *     Unmatched arguments will be appended to the formatted message in square
     *     braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message
     *     template. Arguments are converted to strings using
     *     {@link String#valueOf(Object)}.
     * @throws IllegalStateException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code
     *     errorMessageTemplate} or {@code errorMessageArgs} is null (don't let
     *     this happen)
     */
    @Deprecated // tamper stacktraces
    public static <T extends RuntimeException> void check(boolean expression, Class<T> exceptionClass,
                                                          @Nullable String errorMessageTemplate,
                                                          @Nullable Object... errorMessageArgs) {
        if(!expression) throw instantiate(exceptionClass, format(errorMessageTemplate, errorMessageArgs));
    }

    @SuppressWarnings("unchecked")
    @Deprecated // tamper stacktraces
    private static <T extends RuntimeException> T instantiate(Class<T> clazz, String message) {
        try {
            Constructor<T> constr = clazz.getDeclaredConstructor(String.class);
            return invokeConstructor(constr, message);
        } catch(NoSuchMethodException e) {
            return (T) new RuntimeException(format("Cannot instantiate exception: '{}' with message: '{}'", clazz, message));
        }
    }
}
