package ru.concerteza.util.value;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * User: alexey
 * Date: 10/21/11
 */
public class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair other = (Pair) o;
        return new EqualsBuilder().
                append(first, other.first).
                append(second, other.second).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(first).
                append(second).
                toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("first", first).
                append("second", second).
                toString();
    }
}
