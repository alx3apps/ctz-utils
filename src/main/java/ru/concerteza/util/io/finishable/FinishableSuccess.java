package ru.concerteza.util.io.finishable;


import com.google.common.base.Function;

/**
 * Finishable helper functions for "yes"/"no" results
 *
 * @author alexkasko
 * Date: 4/2/13
 * @see Finishable
 */
public class FinishableSuccess implements Function<Void, Boolean> {
    /**
     * Success function
     */
    public static final Function<Void, Boolean> SUCCESS = new FinishableSuccess(true);
    /**
     * Fail function
     */
    public static final Function<Void, Boolean> FAIL = new FinishableSuccess(false);

    private final boolean success;

    /**
     * Constructor
     *
     * @param success whether operation succeeded
     */
    public FinishableSuccess(boolean success) {
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
