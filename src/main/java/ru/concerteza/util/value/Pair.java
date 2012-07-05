package ru.concerteza.util.value;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Generic pair implementation for returning tuples, do not use for public api returns.
 * @author alexey,
 * Date: 10/21/11
 */
public class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    /**
     * Constructor
     * @param first first value
     * @param second second value
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    // todo
    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return new Pair<T1, T2>(first, second);
    }

    /**
     * First value getter
     * @return first value
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Secong value getter
     * @return second value
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Standard {@code equals} implementation using <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/builder/EqualsBuilder.html">EqualsBuilder</a>
     * @param o object to check equality to
     * @return whether equal
     */
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

    /**
     * Standard {@code hashCode} implementation using <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/builder/HashCodeBuilder.html">HashCodeBuilder</a>
     * @return hash code
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(first).
                append(second).
                toHashCode();
    }

    /**
     * {@code toString} implementation
     * @return string representation using <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/builder/ToStringStyle.html#SHORT_PREFIX_STYLE">SHORT_PREFIX_STYLE</a>
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("first", first).
                append("second", second).
                toString();
    }
}