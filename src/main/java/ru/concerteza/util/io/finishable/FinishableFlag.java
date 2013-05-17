package ru.concerteza.util.io.finishable;


import com.google.common.base.Function;

/**
 * Finishable helper functions for "yes"/"no" results
 *
 * @author alexkasko
 * Date: 4/2/13
 * @see Finishable
 */
public class FinishableFlag implements Function<Void, Boolean> {
    /**
     * Success function
     */
    public static final Function<Void, Boolean> SUCCESS = new FinishableFlag(true);
    /**
     * Fail function
     */
    public static final Function<Void, Boolean> FAIL = new FinishableFlag(false);

    private final boolean success;

    /**
     * Constructor
     *
     * @param success whether operation succeeded
     */
    public FinishableFlag(boolean success) {
        this.success = success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean apply(Void input) {
        return success;
    }
}
