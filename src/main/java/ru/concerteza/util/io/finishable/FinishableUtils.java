package ru.concerteza.util.io.finishable;

import com.google.common.base.Function;
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
     * @param result business operation result
     */
    public static <F, T> void finishQuietly(Finishable<F, T> finishable, Function<F, T> result) {
        if(null == finishable) return;
        try {
            finishable.finish(result);
        } catch (Exception e) {
            logger.warn("Finish error: [" + finishable + "]", e);
        }
    }
}
