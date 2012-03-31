package ru.concerteza.util.option;

/**
 * User: alexey
 * Date: 8/15/11
 */
public final class None<T> extends Option<T> {

    None() {
    }

    public T get() {
        throw new UnsupportedOperationException("Cannot resolve value on None");
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public T getIfAny(T defaultValue) {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "NONE";
    }
}
