package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.annotation.Nullable;

/**
 * Argument for named constructor, for inner usage
 *
 * @author alexey
 * Date: 8/7/12
 * @see NamedConstructor_OLD
 */
class NCArg {
    static final Function<NCArg, String> NAME_FUNCTION = new NameFun();
    static final Predicate<NCArg> OPTIONAL_PREDICATE = new OptionalPredicate();
    static final Predicate<NCArg> ITERABLE_PREDICATE = new IterablePredicate();

    final String name;
    final Class type;
    final boolean optional;

    /**
     * @param name argument name extracted from {@code @Named} annotation
     * @param type argument type
     * @param optional
     */
    NCArg(String name, Class type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NCArg that = (NCArg) o;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("name", name).
                append("type", type).
                toString();
    }

    private static final class NameFun implements Function<NCArg, String> {
        @Override
        public String apply(NCArg input) {
            return input.name;
        }
    }

    private static final class OptionalPredicate implements Predicate<NCArg> {
        @Override
        public boolean apply(NCArg input) {
            return Optional.class.getName().equals(input.type.getName());
        }
    }

    private static final class IterablePredicate implements Predicate<NCArg> {
        @Override
        public boolean apply(@Nullable NCArg input) {
            return Iterable.class.isAssignableFrom(input.type);
        }
    }
}
