package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Contains constructor, list of all arguments names for it and list of
 * arguments with {@code com.google.common.base.Optional} type.
 * Object can be instantiated from unordered data map using arguments names.
 * Object instantiated through constructor invocation without any field access so
 * it can be immutable with all fields defined as {@code final}. {@code Optional}
 * constructor arguments values will be wrapped into {@code Optional} before constructor invocation.
 * Null values are allowed only for {@code com.google.common.base.Optional} arguments.
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
    final ImmutableSet<String> names;
    /**
     * list of argument names with {@code com.google.common.base.Optional} type
     */
    final ImmutableSet<String> optional;

    /**
     * @param constructor object constructor to use
     * @param arguments list of argument for constructor
     */
    @SuppressWarnings("unchecked")
    NamedConstructor(Constructor<?> constructor, Collection<NamedConstructorArgument> arguments) {
        this.constructor = (Constructor<T>) constructor;
        if(!this.constructor.isAccessible()) constructor.setAccessible(true);
        this.names = ImmutableSet.copyOf(Collections2.transform(arguments, NamedConstructorArgument.NAME_FUNCTION));
        Collection<NamedConstructorArgument> opts = Collections2.filter(arguments, NamedConstructorArgument.OPTIONAL_PREDICATE);
        this.optional = ImmutableSet.copyOf(Collections2.transform(opts, NamedConstructorArgument.NAME_FUNCTION));
    }

    /**
     * Instantiates object from unordered data map. Orders map values based on argument names order.
     * Map values must be non-null.
     *
     * @param input unordered data map, null values are allowed
     *              only for {@code com.google.common.base.Optional} arguments
     * @return instantiated object
     */
    T invoke(Map<String, ?> input) {
        try {
            Object[] args = new Object[input.size()];
            int ind = 0;
            for(String na : names) {
                checkArgument(input.containsKey(na), "Value not found for key: '%s', input data: '%s', named constructor: '%s'", na, input, this);
                Object obj = input.get(na);
                args[ind] = wrapOptional(na, obj);
                ind += 1;
            }
            return constructor.newInstance(args);
        } catch(Exception e) {
            throw new UnhandledException(format("Object instantiation error, named constructor: '{}', arguments: '{}'", this, input), e);
        }
    }

    private Object wrapOptional(String name, Object nullable) {
        if(optional.contains(name)) return Optional.fromNullable(nullable);
        checkNotNull(nullable, "Not optional value is null for key: '%s', named constructor: '%s'", name, this);
        return nullable;
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
