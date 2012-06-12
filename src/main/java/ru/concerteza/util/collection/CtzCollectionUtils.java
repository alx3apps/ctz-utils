package ru.concerteza.util.collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzCollectionUtils {
    public static final Map<String, Object> EMPTY_MAP = ImmutableMap.of();

    // fire transform chain for iters with nullable elements
    public static <T> long fireTransform(Iterator<T> iter) {
        int counter = 0;
        while (iter.hasNext()) {
            iter.next();
            counter +=1;
        }
        return counter;
    }

    public static <T> long fireTransform(Iterable<T> iter) {
        return fireTransform(iter.iterator());
    }

    // set becomes map keys, values are products
    // todo: fixlink: see http://docs.guava-libraries.googlecode.com/git-history/v12.0/javadoc/index.html
    public static <K, V> ImmutableMap<K, V> keySetToMap(Set<K> keySet, Function<? super K, V> valueFunction) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for(K key : keySet) {
            builder.put(key, valueFunction.apply(key));
        }
        return builder.build();
    }
}
