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

    /**
     * Finishes list with the same result
     *
     * @param result result function
     * @param finishables list
     * @param <F> generic param
     * @param <T> generic param
     */
    public static <F, T> void finish(Function<F, T> result, Finishable<F, T>... finishables) {
        for(Finishable<F, T> fi : finishables) {
            fi.finish(result);
        }
    }

    /**
     * Finishes quietly list with the same result
     *
     * @param result result function
     * @param finishables list
     * @param <F> generic param
     * @param <T> generic param
     */
    public static <F, T> void finishQuietly(Function<F, T> result, Finishable<F, T>... finishables) {
        for(Finishable<F, T> fi : finishables) {
            finishQuietly(fi, result);
        }
    }
}
