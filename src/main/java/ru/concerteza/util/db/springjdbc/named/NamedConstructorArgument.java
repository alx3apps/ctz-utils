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
 * @see NamedConstructor
 */
class NamedConstructorArgument {
    static final Function<NamedConstructorArgument, String> NAME_FUNCTION = new NameFun();
    static final Predicate<NamedConstructorArgument> OPTIONAL_PREDICATE = new OptionalPredicate();

    final String name;
    final Class<?> type;

    /**
     * @param name argument name extracted from {@code @Named} annotation
     * @param type argument type
     */
    NamedConstructorArgument(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NamedConstructorArgument that = (NamedConstructorArgument) o;
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

    private static final class NameFun implements Function<NamedConstructorArgument, String> {
        @Override
        public String apply(NamedConstructorArgument input) {
            return input.name;
        }
    }

    private static final class OptionalPredicate implements Predicate<NamedConstructorArgument> {
        @Override
        public boolean apply(NamedConstructorArgument input) {
            return Optional.class.getName().equals(input.type.getName());
        }
    }
}
