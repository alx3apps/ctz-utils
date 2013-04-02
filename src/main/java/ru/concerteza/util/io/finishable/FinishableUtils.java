package ru.concerteza.util.io.finishable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities methods for {@link Finishable}
 *
 * @author alexkasko
 * Date: 4/2/13
 * @see Finishable
 */
public class FinishableUtils {
    private static final Logger logger = LoggerFactory.getLogger(FinishableUtils.class);

    /**
     * Calls nullable finishable with "yes" or "no" results
     *
     * @param finishable nullable finishable
     * @param success business operation result
     */
    public static void finishQuietly(Finishable<Void, Boolean> finishable, boolean success) {
        if(null == finishable) return;
        try {
            finishable.finish(new FinishableSuccess(success));
        } catch (Exception e) {
            logger.warn("Finish error: [" + finishable + "]", e);
        }
    }
}
