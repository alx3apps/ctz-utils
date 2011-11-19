package ru.concerteza.util.except;

import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import ru.concerteza.util.option.Option;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class CtzExceptionUtilsTest {
    @Test
    public void testMessage() {
        try {
            throw new UnhandledException(new RuntimeException(new BusinessLogicException(new IOException("fail"))));
        } catch (Exception e) {
            Option<MessageException> op = CtzExceptionUtils.extractMessage(e);
            assertTrue(op.isSome());
            assertNotNull(op.get());
            assertTrue(BusinessLogicException.class.isInstance(op.get()));
        }
    }

    @Test
    public void testNoMessage() {
        try {
            throw new UnhandledException(new RuntimeException(new IOException("fail")));
        } catch (Exception e) {
            Option<MessageException> op = CtzExceptionUtils.extractMessage(e);
            assertTrue(op.isNone());
        }
    }

    private class BusinessLogicException extends MessageException {
        private BusinessLogicException(Throwable cause) {
            super(cause);
        }
    }
}
