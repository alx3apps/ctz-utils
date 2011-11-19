package ru.concerteza.util.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * User: alexey
 * Date: 8/11/11
 */

public class Holder<T> {
    private T target;

    public T get() {
        return target;
    }

    public void set(T target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("target", target).
                toString();
    }
}
