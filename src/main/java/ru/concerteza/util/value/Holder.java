package ru.concerteza.util.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
     */
    public void set(T target) {
        this.target = target;
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
}
