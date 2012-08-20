package ru.concerteza.util.reflect.named;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.concerteza.util.string.function.LowerStringFunction;

import javax.annotation.Nullable;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.collection.CtzCollectionUtils.toLowerKeysMap;
import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeConstructor;
import static ru.concerteza.util.reflect.named.NamedConstructor.CaseType.INSENSITIVE;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Holds reflection information about specific class and can instantiate it using constructor with fields marked
 * {@link Named} or {@link NamedGenericRef}.
 * Designed to use with immutable classes (with final fields) - only constructor invocation is used without any field access.
 * Constructor arguments must be annotated with JSR330 {@link Named} annotations (there are other ways to access
 * constructor names in runtime, see <a href="http://paranamer.codehaus.org/">paranamer project</a>, but we use
 * {@code @Named} annotations only) or {@link NamedGenericRef} annotation for generic arguments.
 * Constructors without {@code @Named} or {@link NamedGenericRef} annotations on arguments will be ignored.
 * Constructors with {@code @Named} or {@link NamedGenericRef} annotations must have all they arguments annotated with
 * not blank values without duplicates. {@code Optional} constructor arguments values will be wrapped into {@code Optional} before
 * constructor invocation. All reflection introspection is done on named constructor instantiation.
 * For case insensitive names all {@code @Named} values must be locale insensitive. Null values are allowed
 * only for {@code com.google.common.base.Optional} arguments.
 *
 * @param <T> object type to instantiate
 * @author alexey
 *         Date: 7/6/12
 * @see ru.concerteza.util.db.springjdbc.named.NamedConstructorMapper
 */
public class NamedConstructor<T> {
    /**
     * Specifies, whether to treat names of provided constructor arguments as case sensitive on constructor invocation
     */
    public enum CaseType {SENSITIVE, INSENSITIVE}

    /**
     * Arguments match mode on constructor invocation
     *
     * {@code EXACT} - provided arguments must match one of the constructor arguments exactly
     * {@code OPTIONAL_MISSED_ALLOWED} - optional arguments may be missed -
     * they will be replaced with Optional.absent values
     * {@code ADDITIONAL_ALLOWED} - more arguments then needed may be provided,
     * arguments not used by selected constructor eill be ignored
     */
    public enum MatchMode {EXACT, OPTIONAL_MISSED_ALLOWED, ADDITIONAL_ALLOWED}

    // Lists (with linear search) is deliberate, they will be small
    private final List<SingleNamedConstr<T>> entries;
    private final boolean optional;

    /**
     * @param type class to instantiate
     * @param genericType optional generic argument for provided class
     */
    @SuppressWarnings("unchecked") // declared constructor generic type
    private NamedConstructor(Class<T> type, Optional<Class> genericType) {
        checkNotNull(type, "Provided type is null");
        checkNotNull(genericType, "Provided genericType is null");
        this.optional = Optional.class.isAssignableFrom(type);
        List<ConstrHolder> namedList = findNamed(genericType.isPresent() ? genericType.get() : type);
        if(0 == namedList.size()) {
            this.entries = ImmutableList.of();
        } else {
            ImmutableList.Builder<SingleNamedConstr<T>> entriesBuilder = ImmutableList.builder();
            ImmutableSet.Builder<String> namesBuilder = ImmutableSet.builder();
            for(ConstrHolder named : namedList) {
                ImmutableMap.Builder<String, NamedConstructor> builder = ImmutableMap.builder();
                ImmutableSet.Builder<String> mandArgsBuilder = ImmutableSet.builder();
                ImmutableSet.Builder<String> optArgsBuilder = ImmutableSet.builder();

                for(NamedConstructorArg nca : named.args) {
                    builder.put(nca.name, new NamedConstructor(nca.type, nca.genericType));
                    namesBuilder.add(nca.name);
                    if(Optional.class.isAssignableFrom(nca.type)) optArgsBuilder.add(nca.name);
                    else mandArgsBuilder.add(nca.name);
                }
                SingleNamedConstr<T> en = new SingleNamedConstr<T>((Constructor) named.constr, builder.build(),
                        mandArgsBuilder.build(), optArgsBuilder.build());
                entriesBuilder.add(en);
            }
            this.entries = entriesBuilder.build();
        }
    }

    /**
     * Factory method
     *
     * @param clazz class to instantiate
     * @param <T> clazz type
     * @return named constructor for that class
     * @throws IllegalArgumentException on unsuccessful search of appropriate constructors
     */
    public static <T> NamedConstructor<T> of(Class<T> clazz) {
        return new NamedConstructor<T>(clazz, Optional.<Class>absent());
    }

    /**
     * Invokes named constructor on arguments map using {@code MatchMode.EXACT} and {@code CaseType.SENSITIVE}
     *
     * @param map named arguments map
     * @return instantiated object
     */
    public T invoke(Map<String, ?> map) {
        return invoke(map, MatchMode.EXACT, CaseType.SENSITIVE);
    }

    /**
     * Invokes named constructor on arguments map
     *
     * @param map named arguments map
     * @param matchMode match mode
     * @param caseType case sensivity settings
     * @return instantiated object
     */
    public T invoke(Map<String, ?> map, MatchMode matchMode, CaseType caseType) {
        checkNotNull(map, "Provided map is null");
        Map<String, ?> inputMap = INSENSITIVE.equals(caseType) ? toLowerKeysMap(map) : map;
        SingleNamedConstr<T> snc = findSNC(inputMap.keySet(), matchMode, caseType);
        return invokeSNC(snc, inputMap, matchMode, caseType);
    }

    /**
     * Invokes named constructor on arguments iterable
     *
     * @param iter named arguments iterable
     * @param matchMode match mode
     * @param caseType case sensivity settings
     * @return iterable of instantiated object
     */
    @SuppressWarnings("unchecked")
    public Iterable<T> invoke(Iterable<?> iter, MatchMode matchMode, CaseType caseType) {
        checkNotNull(iter, "Provided iter is null, constructor: '%s'", this);
        Function<Object, T> invoker = new InvokerFun(matchMode, caseType, iter);
        if(iter instanceof List) return Lists.transform((List) iter, invoker);
        if(iter instanceof Collection) return Collections2.transform((Collection) iter, invoker);
        return Iterables.transform(iter, invoker);
    }

    @SuppressWarnings("unchecked")
    private T invoke(Object any, MatchMode matchMode, CaseType caseType) {
        checkNotNull(any, "Provided object is null, constructor: '%s'", this);
        final Object res;
        if(any instanceof Map) res = invoke((Map<String, ?>) any, matchMode, caseType);
        else if(any instanceof Iterable) res = invoke((Iterable<?>) any, matchMode, caseType);
            // must be primitive, check whether leaf
        else if(0 == this.entries.size()) res = any;
        else
            throw new IllegalArgumentException(format("Unsupported input type: '{}' provided to non-leaf constructor: '{}'", any, this));
        return (T) (optional ? Optional.of(res) : res);
    }

    private List<ConstrHolder> findNamed(Class<T> type) {
        ImmutableList.Builder<ConstrHolder> builder = ImmutableList.builder();
        for(Constructor<?> co : type.getDeclaredConstructors()) {
            LinkedHashSet<NamedConstructorArg> args = extractNames(co);
            if(args.size() > 0) {
                builder.add(new ConstrHolder(co, args));
            }
        }
        return builder.build();
    }

    private LinkedHashSet<NamedConstructorArg> extractNames(Constructor<?> co) {
        LinkedHashSet<NamedConstructorArg> res = Sets.newLinkedHashSet();
        String coStr = co.toGenericString();
        Iterator<Annotation[]> anIter = Iterators.forArray(co.getParameterAnnotations());
        Class<?>[] typesArr = co.getParameterTypes();
        Iterator<Class<?>> typesIter = Iterators.forArray(typesArr);
        while(anIter.hasNext() || typesIter.hasNext()) {
            Annotation[] anns = anIter.next();
            Class<?> type = typesIter.next();
            List<Annotation> namedList = ImmutableList.copyOf(Collections2.filter(asList(anns), NamedAnnPred.INSTANCE));
            if(0 == namedList.size()) continue; // no named or not consistent constructor
            checkArgument(1 == namedList.size(), "Multiple named annotations: '{}' found for type: '{}'", namedList, type);
            Annotation named = namedList.get(0);
            if(named instanceof Named) {
                Named na = (Named) named;
                checkArgument(isNotBlank(na.value()), "@Named annotation with empty value found, type: '%s'", type);
                boolean unique = res.add(new NamedConstructorArg(na.value(), type)); // matched on name only
                checkArgument(unique, "Not unique @Named or @NamedGenericRef value: '%s', constructor: '%s'", na.value(), coStr);
            } else if(named instanceof NamedGenericRef) {
                NamedGenericRef li = (NamedGenericRef) named;
                checkArgument(isNotBlank(li.name()), "@NamedGenericRef annotation with empty value found, type: '%s'", type);
                checkArgument(null != li.type(), "@NamedGenericRef annotation with null type found, parent type: '%s'", type);
                boolean unique = res.add(new NamedConstructorArg(li.name(), type, li.type())); // matched on name only
                checkArgument(unique, "Not unique @Named or @NamedGenericRef value: '%s', constructor: '%s'", li.name(), coStr);
            } else throw new IllegalStateException(format("Unknown annotation to process: '{}'", named));
        }
        checkArgument(0 == res.size() || typesArr.length == res.size(), "Not consistent @Named annotations found for constructor: '%s'", coStr);
        return res;
    }

    // todo: maybe move state checks to nc creation
    private SingleNamedConstr<T> findSNC(Set<String> keys, MatchMode matchMode, CaseType caseType) {
        ImmutableList.Builder<SingleNamedConstr<T>> builder = ImmutableList.builder();
        for(SingleNamedConstr<T> snc : entries) {
            if(matches(snc, keys, matchMode, caseType)) builder.add(snc);
        }
        List<SingleNamedConstr<T>> list = builder.build();
        checkArgument(list.size() > 0, "No named constructor found for keys: '%s', existed constructors: '%s'", keys, entries);
        checkState(1 == list.size(), "indistinct constructor for input: '%s', matchMode: '%s', caseType: '%s' matched: '%s'",
                keys, matchMode, caseType, list);
        return list.get(0);
    }

    private boolean matches(SingleNamedConstr<T> snc, Set<String> keys, MatchMode matchMode, CaseType caseType) {
        final Set<String> mandatory;
        final Set<String> optional;
        switch (caseType) {
            case SENSITIVE:
                mandatory = snc.mandatory;
                optional = snc.optional;
                break;
            case INSENSITIVE:
                mandatory = snc.mandatoryLower;
                optional = snc.optionalLower;
                break;
            default: throw new IllegalArgumentException(caseType.name());
        }
        Set<String> all = Sets.union(mandatory, optional);
        switch (matchMode) {
            case EXACT:
                return keys.containsAll(all) && all.containsAll(keys);
            case ADDITIONAL_ALLOWED:
                return keys.containsAll(all);
            case OPTIONAL_MISSED_ALLOWED:
                return all.containsAll(keys) && keys.containsAll(mandatory);
            default: throw new IllegalArgumentException(matchMode.name());
        }
    }

    private T invokeSNC(SingleNamedConstr<T> snc, Map<String, ?> map, MatchMode matchMode, CaseType caseType) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for(Map.Entry<String, NamedConstructor> pr : snc.children.entrySet()) {
            String key = CaseType.SENSITIVE.equals(caseType) ? pr.getKey() : pr.getKey().toLowerCase(Locale.ENGLISH);
            if(!pr.getValue().optional && !MatchMode.OPTIONAL_MISSED_ALLOWED.equals(matchMode))checkArgument(map.containsKey(key), "No value provided for named argument: '%s', constructor: '%s'", key, this);
            Object raw = map.get(key);
            final Object instantiated;
            if(null != raw) instantiated = pr.getValue().invoke(raw, matchMode, caseType);
            else {
                if(pr.getValue().optional) instantiated = Optional.absent();
                else throw new IllegalArgumentException(format("Not optional null value on key: '{}' " +
                        "in provided map: '{}', constructor: '{}'", key, map, this));
            }
            builder.add(instantiated);
        }
        return invokeConstructor(snc.constr, builder.build().toArray());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("entries", entries).
                append("optional", optional).
                toString();
    }

    private static class ConstrHolder {
        private final Constructor<?> constr;
        private final LinkedHashSet<NamedConstructorArg> args;

        private ConstrHolder(Constructor<?> constr, LinkedHashSet<NamedConstructorArg> args) {
            checkNotNull(constr, "Provided constrcutor is null");
            checkNotNull(args, "Provided constructor args are null");
            checkArgument(args.size() > 0, "Provided constructor args are empty");
            this.constr = constr;
            this.args = args;
        }
    }

    private static class SingleNamedConstr<T> {
        private final Constructor<T> constr;
        private final Map<String, NamedConstructor> children;
        private final Set<String> mandatory;
        private final Set<String> optional;
        private final Set<String> mandatoryLower;
        private final Set<String> optionalLower;

        private SingleNamedConstr(Constructor<T> constr, Map<String, NamedConstructor> children, Set<String> mandatoryArgNames, Set<String> optionalArgNames) {
            this.constr = constr;
            this.children = children;
            this.mandatory = mandatoryArgNames;
            this.optional = optionalArgNames;
            this.mandatoryLower = ImmutableSet.copyOf(Collections2.transform(mandatoryArgNames, LowerStringFunction.INSTANCE));
            this.optionalLower = ImmutableSet.copyOf(Collections2.transform(optionalArgNames, LowerStringFunction.INSTANCE));
        }

        @Override
        public String toString() {
            return children.keySet().toString();
        }
    }

    private static class NamedConstructorArg {
        private final String name;
        private final Class<?> type;
        private final Optional<Class> genericType;

        private NamedConstructorArg(String name, Class<?> type) {
            this(name, type, null);
        }

        private NamedConstructorArg(String name, Class<?> type, @Nullable Class genericType) {
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

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                    append("name", name).
                    append("type", type).
                    append("genericType", genericType).
                    toString();
        }
    }

    private enum NamedAnnPred implements Predicate<Annotation> {
        INSTANCE;
        @Override
        public boolean apply(Annotation input) {
            return input instanceof Named || input instanceof NamedGenericRef;
        }
    }

    private class InvokerFun implements Function<Object, T> {
        private final MatchMode mode;
        private final CaseType caseType;
        private final Iterable<?> iterForLogging;

        private InvokerFun(MatchMode mode, CaseType caseType, Iterable<?> iterForLogging) {
            this.mode = mode;
            this.caseType = caseType;
            this.iterForLogging = iterForLogging;
        }

        @Override
        public T apply(@Nullable Object input) {
            checkNotNull(input, "Null value in provided iterable: '%s', constructor: '%s'", iterForLogging, NamedConstructor.this);
            return invoke(input, mode, caseType);
        }
    }
}
