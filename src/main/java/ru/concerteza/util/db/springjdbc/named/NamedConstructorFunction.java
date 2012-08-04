package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.UnhandledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.collection.maps.LowerKeysImmutableMapBuilder;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 7/6/12
 */
public class NamedConstructorFunction<T> implements Function<Map<String, ?>, T> {
    private static final Logger logger = LoggerFactory.getLogger(NamedConstructorFunction.class);
    private final List<NamedConstructor<T>> constructors;

    public NamedConstructorFunction(Class<T> clazz) {
        checkNotNull(clazz, "Provided class is null");
        this.constructors = extractNamedConstructors(clazz);
    }

    public static <T> NamedConstructorFunction<T> forClass(Class<T> clazz) {
        return new NamedConstructorFunction<T>(clazz);
    }

    @Override
    public T apply(@Nullable Map<String, ?> input) {
        checkNotNull(input, "Input data map is null");
        Map<String, ?> notNullValues = LowerKeysImmutableMapBuilder.copyOf(input);
        NamedConstructor<T> nc = findNamedConstructor(notNullValues.keySet());
        return invokeConstructor(nc.getConstructor(), notNullValues.values());
    }

    private List<NamedConstructor<T>> extractNamedConstructors(Class<T> clazz) {
        ImmutableList.Builder<NamedConstructor<T>> builder = ImmutableList.builder();
        for(Constructor<?> co : clazz.getDeclaredConstructors()) {
            Annotation[][] anArray = co.getParameterAnnotations();
            if(NamedSupport.isNamed(anArray, co.toGenericString())) {
                List<String> names = NamedSupport.extractNames(anArray);
                builder.add(new NamedConstructor<T>(co, names));
            }
        }
        List<NamedConstructor<T>> res = builder.build();
        checkArgument(res.size() > 0, "No named constructors found for class: '%s'", clazz);
        return res;
    }

    // linear search is deliberate here - lists are small
    private NamedConstructor<T> findNamedConstructor(Collection<String> names) {
        outerloop:
        for(NamedConstructor<T> co : constructors) {
            if(names.size() != co.getNames().size()) continue;
            int i = 0;
            for(String na : names) {
                if(!na.equalsIgnoreCase(co.getNames().get(i))) continue outerloop;
                i += 1;
            }
            return co;
        }
        throw new IllegalArgumentException(format("Cannot find constructor for names: '{}'", names));
    }

    private <T> T invokeConstructor(Constructor<T> co, Collection<?> arguments) {
        try {
            if(!co.isAccessible()) co.setAccessible(true);
            return co.newInstance(arguments.toArray());
        } catch(Exception e) {
            logger.error("co: '{}', arguments: '{}'", co, arguments);
            throw new UnhandledException(e);
        }
    }
}
