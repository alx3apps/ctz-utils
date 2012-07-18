package ru.concerteza.util.keys;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import ru.concerteza.util.value.Holder;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/18/12
 */
class GroupByKeyCollection<S extends KeyEntry, R> extends ForwardingCollection<R> {
    private final Collection<R> delegate;

    public GroupByKeyCollection(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        checkNotNull(source, "Source iterator must not be null");
        checkNotNull(aggregator, "Aggregator must not be null");
        // holder to prevent tree traversal on update
        TreeMap<String, Holder<R>> map = new TreeMap<String, Holder<R>>();
        while(source.hasNext()) {
            S s = source.next();
            final Holder<R> existed = map.get(s.key());
            if(null == existed) {
                R r = aggregator.aggregate(s, null);
                Holder<R> created = new Holder<R>(r);
                map.put(s.key(), created);
            } else {
                R r = aggregator.aggregate(s, existed.get());
                existed.set(r);
            }
        }
        this.delegate = Collections2.transform(map.values(), new UnholderFun<R>());
    }

    @Override
    protected Collection<R> delegate() {
        return this.delegate;
    }

    private static class UnholderFun<T> implements Function<Holder<T>, T> {
        @Override
        public T apply(Holder<T> input) {
            return input.get();
        }
    }
}