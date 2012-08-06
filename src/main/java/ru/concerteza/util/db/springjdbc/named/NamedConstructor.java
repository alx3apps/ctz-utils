package ru.concerteza.util.db.springjdbc.named;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Contains constructor and list of arguments names for it.
 * Object can be instantiated from unordered data map using arguments names.
 * Object instantiated through constructor invocation without any field access so
 * it can be immutable with all fields defined as {@code final}
 *
 * @author alexey
 * Date: 7/5/12
 * @see NamedConstructorMapper
 * @see NamedConstructorFunction
 */
class NamedConstructor<T> {
    /**
     * object constructor to use
     */
    final Constructor<T> constructor;
    /**
     * list of argument names for constructor
     */
    final LinkedHashSet<String> names;

    /**
     * @param constructor object constructor to use
     * @param names list of argument names for constructor
     */
    @SuppressWarnings("unchecked")
    NamedConstructor(Constructor<?> constructor, LinkedHashSet<String> names) {
        this.constructor = (Constructor<T>) constructor;
        if(!this.constructor.isAccessible()) constructor.setAccessible(true);
        this.names = names;
    }

    /**
     * Instantiates object from unordered data map. Orders map values based on argument names order.
     * Map values must be non-null.
     *
     * @param input unordered data map
     * @return instantiated object
     */
    T invoke(Map<String, ?> input) {
        try {
            Object[] args = new Object[input.size()];
            int ind = 0;
            for(String na : names) {
                Object ar = input.get(na);
                checkNotNull(ar, "Value is null for key: '%s', input data: '%s', named constructor: '%s'", na, input, this);
                args[ind] = ar;
                ind += 1;
            }
            if(!constructor.isAccessible()) constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch(Exception e) {
            throw new UnhandledException(format("Object instantiation error, named constructor: '{}', arguments: '{}'", this, input), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("constructor", constructor).
                append("names", names).
                toString();
    }
}
