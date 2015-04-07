package ru.concerteza.util.except;

import com.google.common.base.Optional;
import org.junit.Test;
import ru.concerteza.util.option.Option;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.except.CtzExceptionUtils.check;
import static ru.concerteza.util.except.CtzExceptionUtils.extractMessage;

/**
 * User: alexey
 * Date: 11/19/11
 *
 * Wrapper over apache exception utils. Looks for MessageException in nested exceptions stack
 * and returns business error message, id any. Usage example is in CtzExceptionUtilsTest
 *
 */
public class CtzExceptionUtilsTest {
    @Test
    public void testMessage() {
        try {
            BusinessLogicException ex = new BusinessLogicException("IO error on some files");
            throw new RuntimeException(new RuntimeException(ex));
        } catch (Exception e) {
            Optional<BusinessLogicException> op = extractMessage(e, BusinessLogicException.class);
            assertTrue(op.isPresent());
            assertNotNull(op.get());
            assertTrue(BusinessLogicException.class.isInstance(op.get()));
        }
    }

    @Test
    public void testNoMessage() {
        try {
            throw new RuntimeException(new RuntimeException(new IOException("fail")));
        } catch (Exception e) {
            Optional<BusinessLogicException> op = extractMessage(e, BusinessLogicException.class);
            assertFalse(op.isPresent());
        }
    }

    @Test(expected = BusinessLogicException.class)
    public void testCheck2() {
        check(false, BusinessLogicException.class, "some message");
    }

    @Test(expected = BusinessLogicException.class)
    public void testCheck3() {
        check(false, BusinessLogicException.class, "some message: '{}'", "hello");
    }

    private static class BusinessLogicException extends RuntimeException {
        private BusinessLogicException() {
        }

        private BusinessLogicException(String s) {
            super(s);
        }
    }
}
