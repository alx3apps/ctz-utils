package ru.concerteza.util.value;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generic holder class to keep references for objects, e.g. for out argument to poorly designed void methods
 * @author alexey,
 * Date: 8/11/11
 */
public class Holder<T> {
    private T target;

    /**
     * Default constructor
     */
    public Holder() {
    }

    /**
     * Generic friendly factory method
     * @param <T> value parameter
     * @return holder instance
     */
    public static <T> Holder<T> create() {
        return new Holder<T>();
    }

    /**
     * Constructor with default value
     * @param target default value
     */
    public Holder(T target) {
        this.target = target;
    }

    /**
     * Value getter
     * @return holding value
     */
    public T get() {
        return target;
    }

    /**
     * Value setter
     * @param target value to hold
     * @throws NullPointerException on null input
     */
    public void set(T target) {
        checkNotNull(target, "Provided value is null");
        this.target = target;
    }

    /**
     * Checks whether value is not null
     * @return whether value is not null
     */
    public boolean isPresent() {
        return target != null;
    }

    /**
     * Checks whether value is null
     * @return whether value is null
     */
    public boolean isAbsent() {
        return target == null;
    }

    /**
     * {@code toString} implementation
     * @return string representation using <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/builder/ToStringStyle.html#SHORT_PREFIX_STYLE">SHORT_PREFIX_STYLE</a>
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("target", target).
                toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Holder holder = (Holder) o;
        return !(target != null ? !target.equals(holder.target) : holder.target != null);
    }

    @Override
    public int hashCode() {
        return target != null ? target.hashCode() : 0;
    }
}
