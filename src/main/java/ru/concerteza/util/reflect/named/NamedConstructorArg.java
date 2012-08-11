package ru.concerteza.util.reflect.named;

import com.google.common.base.Optional;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Argument for named constructor, for inner usage
 *
 * @author alexey
 * Date: 8/7/12
 * @see NamedConstructor_OLD
 */
class NamedConstructorArg {
//    static final Function<NamedConstructorArg, String> NAME_FUNCTION = new NameFun();
//    static final Predicate<NamedConstructorArg> OPTIONAL_PREDICATE = new OptionalPredicate();
//    static final Predicate<NamedConstructorArg> ITERABLE_PREDICATE = new IterablePredicate();

    final String name;
    final Class<?> type;
    final Optional<Class> genericType;

    /**
     * @param name argument name extracted from {@code @Named} annotation
     * @param type argument type
     */
    NamedConstructorArg(String name, Class<?> type) {
        this(name, type, null);
    }

    NamedConstructorArg(String name, Class<?> type, @Nullable Class genericType) {
        checkArgument(isNotBlank(name), "Provided name is blank");
        checkNotNull(type, "Provided type is null");
        this.name = name;
        this.type = type;
        this.genericType = Optional.fromNullable(genericType);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        NamedConstructorArg that = (NamedConstructorArg) o;
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
                append("genericType", genericType).
                toString();
    }

//    private static final class NameFun implements Function<NamedConstructorArg, String> {
//        @Override
//        public String apply(NamedConstructorArg input) {
//            return input.name;
//        }
//    }
//
//    private static final class OptionalPredicate implements Predicate<NamedConstructorArg> {
//        @Override
//        public boolean apply(NamedConstructorArg input) {
//            return Optional.class.getName().equals(input.type.getName());
//        }
//    }
//
//    private static final class IterablePredicate implements Predicate<NamedConstructorArg> {
//        @Override
//        public boolean apply(@Nullable NamedConstructorArg input) {
//            return Iterable.class.isAssignableFrom(input.type);
//        }
//    }
}
