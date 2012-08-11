package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import org.apache.commons.lang.UnhandledException;

import javax.annotation.Nullable;
import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * todo: update
 * Guava function, converts unordered data map into object instance using {@link NamedConstructor_OLD}. Designed
 * to use with immutable classes (with final fields) - only constructor invocation is used without any field access.
 * Constructor arguments must be annotated with JSR330 {@link Named} annotations (there are other ways to access
 * constructor names in runtime, see <a href="http://paranamer.codehaus.org/">paranamer project</a>, but we use
 * {@code @Named} annotations only). Constructors without {@code @Named} annotations on arguments will be ignored.
 * Constructors with {@code @Named} annotations must have all they arguments annotated with not blank values without
 * duplicates. {@code Optional} constructor arguments values will be wrapped into {@code Optional} before
 * constructor invocation. All reflection introspection is done on function instantiation.
 * For case insensitive names all {@code @Named} values must be locale insensitive. Null values are allowed
 * only for {@code com.google.common.base.Optional} arguments.
 *
 * @param <T> object type to instantiate
 * @author alexey
 * Date: 7/6/12
 * @see NamedConstructor_OLD
 * @see NamedConstructorMapper
 */
public class NamedConstructorFunction<T> implements Function<Map<String, ?>, T> {
    private final Function<Object, Object> processFun = new ProcessFun();
    private final Class<T> clazz;
    private final Constructor<T> constr;
    private final Map<String, NamedConstructorFunction> processors;
    private final boolean optional;
//
    /**
     * Constructor
     *
     * @param clazz class to introspect and instantiate
     * @param caseSensitiveNames whether too keep case of {@link javax.inject.Named} values
     * @param optional
     */
    public NamedConstructorFunction(Class<T> clazz, boolean caseSensitiveNames, boolean optional) {
        this.optional = optional;
        checkNotNull(clazz, "Provided class is null");
        this.clazz = clazz;
        Optional<NamedConstructor<T>> opt = NamedSupport.extractNamedArgsConstructor(clazz, caseSensitiveNames);
        if(opt.isPresent()) {
            this.constr = opt.get().constr;
            if(!constr.isAccessible()) constr.setAccessible(true);
            ImmutableMap.Builder<String, NamedConstructorFunction> builder = ImmutableMap.builder();
            for (NCArg nca : opt.get().args) {
                builder.put(nca.name, new NamedConstructorFunction(nca.type, caseSensitiveNames, nca.optional));
            }
            this.processors = builder.build();

        } else {
            this.constr = null;
            this.processors = null;
        }
    }

    public static <T> NamedConstructorFunction<T> of(Class<T> clazz) {
        return new NamedConstructorFunction<T>(clazz, true, false);
    }

    /**
     * Converts unordered data map into object instance using constructor {@link Named} annotated arguments.
     *
     * @param input unordered data map
     * @return instantiated object
     */
    @Override
    public T apply(Map<String, ?> input) {
        checkNotNull(input);
        Object[] args = new Object[processors.size()];
        int ind = 0;
        for(Map.Entry<String, NamedConstructorFunction> pr : processors.entrySet()) {
            checkArgument(input.containsKey(pr.getKey()));
            Object raw = input.get(pr.getKey());
            Object processed = pr.getValue().process(raw);
            args[ind] = processed;
            ind += 1;
        }
        return invokeConstructor(args);
    }

    private T invokeConstructor(Object[] args) {
        try {
            return constr.newInstance(args);
        } catch(InstantiationException e) {
            throw new UnhandledException(e);
        } catch(IllegalAccessException e) {
            throw new UnhandledException(e);
        } catch(InvocationTargetException e) {
            throw new UnhandledException(e);
        }
    }

    public Object process(Object input) {
        if(null == input) {
            if(optional) return Optional.absent();
            throw new IllegalArgumentException("Must be not null");
        }
        final Object res;
        if(input instanceof Map) res = apply((Map<String, ?>) input);
        // make lazy
        else if(input instanceof Iterable) res = ImmutableList.copyOf(Iterables.transform((Iterable) input, processFun));
        // must be primitive
        else res = input;
        return optional ? Optional.of(res) : res;
    }


    private String hashNames(Collection<String> names) {
        List<String> ordered = Ordering.natural().immutableSortedCopy(names);
        return Joiner.on(", ").join(ordered);
    }

    private class ProcessFun implements Function<Object, Object> {
        @Override
        public Object apply(@Nullable Object input) {
            return process(input);
        }
    }
}
