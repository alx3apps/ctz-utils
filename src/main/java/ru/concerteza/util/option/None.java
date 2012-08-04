package ru.concerteza.util.option;

/**
 * Options without value
 *
 * @author alexey
 * Date: 8/15/11
 * @see Option
 */
final class None<T> extends Option<T> {

    /**
     * Constructor for inner use
     */
    None() {
    }

    /**
     * @return throws exception
     * @throws UnsupportedOperationException
     */
    @Override
    public T get() {
        throw new UnsupportedOperationException("Cannot resolve value on None");
    }

    /**
     * @return true
     */
    @Override
    public boolean isNone() {
        return true;
    }

    /**
     * @return false
     */
    @Override
    public boolean isSome() {
        return false;
    }

    /**
     * @param defaultValue value to return from {@link None}
     * @return provided value
     */
    @Override
    public T getIfAny(T defaultValue) {
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "None";
    }
}
