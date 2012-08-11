//package ru.concerteza.util.db.springjdbc.named;
//
//import com.google.common.base.Function;
//import com.google.common.base.Optional;
//import com.google.common.collect.Collections2;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//import org.apache.commons.lang.UnhandledException;
//import org.apache.commons.lang.builder.ToStringBuilder;
//import org.apache.commons.lang.builder.ToStringStyle;
//
//import javax.annotation.Nullable;
//import java.lang.reflect.Constructor;
//import java.util.*;
//
//import static com.google.common.base.Preconditions.checkArgument;
//import static com.google.common.base.Preconditions.checkNotNull;
//import static ru.concerteza.util.string.CtzFormatUtils.format;
//
///**
//* todo: update docs
//* todo: refactor hardly
//* Contains constructor, list of all arguments names for it and list of
//* arguments with {@code com.google.common.base.Optional} type.
//* Object can be instantiated from unordered data map using arguments names.
//* Object instantiated through constructor invocation without any field access so
//* it can be immutable with all fields defined as {@code final}. {@code Optional}
//* constructor arguments values will be wrapped into {@code Optional} before constructor invocation.
//* Null values are allowed only for {@code com.google.common.base.Optional} arguments.
//*
//* @author alexey
//* Date: 7/5/12
//* @see NamedConstructorMapper
//* @see NamedConstructor2
//*/
//class NamedConstructor_OLD<T> {
//    static final Function<NamedConstructor_OLD, Iterator<String>> NAMES_FUNCTION = new NamesFun();
//
//    /**
//     * object constructor to use
//     */
//    final Constructor<T> constructor;
//    /**
//     * list of argument names for constructor
//     */
//    final ImmutableSet<NamedConstructorArgument> coargs;
//    /**
//     * list of argument names with {@code com.google.common.base.Optional} type
//     */
//    final ImmutableSet<String> optional;
//    final ImmutableMap<String, NamedConstructor2> children;
//
//    /**
//     * @param constructor object constructor to use
//     * @param arguments list of argument for constructor
//     */
//    @SuppressWarnings("unchecked")
//    NamedConstructor_OLD(Constructor<?> constructor, Collection<NamedConstructorArgument> arguments) {
//        this.constructor = (Constructor<T>) constructor;
//        if(!this.constructor.isAccessible()) constructor.setAccessible(true);
//        this.coargs = ImmutableSet.copyOf(arguments); // todo
//        Collection<NamedConstructorArgument> opts = Collections2.filter(arguments, NamedConstructorArgument.OPTIONAL_PREDICATE);
//        this.optional = ImmutableSet.copyOf(Collections2.transform(opts, NamedConstructorArgument.NAME_FUNCTION));
//        Collection<NamedConstructorArgument> iterables = Collections2.filter(arguments, NamedConstructorArgument.ITERABLE_PREDICATE);
//        ImmutableMap.Builder<String, NamedConstructor2> builder = ImmutableMap.builder();
//        for(NamedConstructorArgument ar : arguments) {
//            List<NamedConstructor_OLD<Object>> ncs = NamedSupport.<Object>extractNamedConstructors(ar.type, true);
//            if(ncs.size() > 0) builder.put(ar.name, new NamedConstructor2(ncs));
//        }
//        for(NamedConstructorArgument ar : iterables) {
//            builder.put(ar.name, NamedConstructor2.of(ar.type));
//        }
//        this.children = builder.build();
//    }
//
//    /**
//     * Instantiates object from unordered data map. Orders map values based on argument names order.
//     * Map values must be non-null.
//     *
//     * @param input unordered data map, null values are allowed
//     *              only for {@code com.google.common.base.Optional} arguments
//     * @return instantiated object
//     */
//    T invoke(Map<String, ?> input) {
//        try {
//            Object[] args = new Object[coargs.size()];
//            int ind = 0;
//            for(NamedConstructorArgument na : coargs) {
//                checkArgument(input.containsKey(na.name), "Value not found for key: '%s', input data: '%s', named constructor: '%s'", na, input, this);
//                Object obj = input.get(na.name);
//                NamedConstructor2<?> fu = children.get(na.name);
//
//                final Object val;
//                if(null != fu) {
//                    if(obj instanceof Map) val = fu.apply((Map) obj);
//                    else if(obj instanceof Iterable) {
//                        ImmutableList.Builder builder = ImmutableList.builder();
//                        for(Object el : (Iterable) obj) {
//                            builder.add(fu.apply())
//                        }
//
//                    }
//
//                }
//                else val = wrapOptional(na.name, obj);
//
//                args[ind] = val;
//                ind += 1;
//            }
//            return constructor.newInstance(args);
//        } catch(Exception e) {
//            throw new UnhandledException(format("Object instantiation error, named constructor: '{}', arguments: '{}'", this, input), e);
//        }
//    }
//
//    private Object apply()
//
//    private Object wrapOptional(String name, Object nullable) {
//        if(optional.contains(name)) return Optional.fromNullable(nullable);
//        checkNotNull(nullable, "Not optional value is null for key: '%s', named constructor: '%s'", name, this);
//        return nullable;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public String toString() {
//        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
//                append("constructor", constructor).
//                append("names", names).
//                toString();
//    }
//
//
//
////    private enum ExtractNC implements Function<NamedConstructorArgument, List<NamedConstructor<?>>> {
////        INSTANCE;
////
////        @Override
////        public List<NamedConstructor<?>> apply(@Nullable NamedConstructorArgument input) {
////            return NamedSupport.extractNamedConstructors(input.type, true);
////        }
////    }
////
////    private enum NotEmptyPredicate implements Predicate<List<?>> {
////        INSTANCE;
////        @Override
////        public boolean apply(@Nullable List<?> input) {
////            return input.size() > 0;
////        }
////    }
//
//    private static class NamesFun implements Function<NamedConstructor_OLD, Iterator<String>> {
//        @Override
//        public Iterator<String> apply(@Nullable NamedConstructor_OLD input) {
//            return input.names.iterator();
//        }
//    }
//}
