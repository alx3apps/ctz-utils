package ru.concerteza.util.option;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 8/15/11
 */

final class Some<T> extends Option<T> {
    private final T value;

    /**
     * Constrcutor for inner use
     *
     * @param value non null value to hold
     */
    Some(T value) {
        checkNotNull(value, "Some value cannot be null");
        this.value = value;
    }

    /**
     * @return holded
     */
    public T get() {
        return value;
    }

    /**
     * @return false
     */
    @Override
    public boolean isNone() {
        return false;
    }

    /**
     * @return true
     */
    @Override
    public boolean isSome() {
        return true;
    }

    /**
     * @param defaultValue value to return from {@link None}
     * @return holded value
     */
    @Override
    public T getIfAny(T defaultValue) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value.toString();
    }
}

