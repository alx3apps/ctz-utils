package ru.concerteza.util.option;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * User: alexey
 * Date: 8/15/11
 */

public final class Some<T> extends Option<T> {
    private final T value;

    Some(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public T getIfAny(T defaultValue) {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("value", value).
                toString();
    }
}

