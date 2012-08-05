package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Zero copy, lazy hash left join implementation. Not thread-safe.
 *
 * @author alexey
 * Date: 7/13/12
 * @see KeyOperations
 */
class HashLeftJoinIterator<S extends KeyEntry, T, R> extends AbstractIterator<R> {
    private final Iterator<S> sourceIter;
    private final Multimap<String, T> targetMap;
    private final KeyJoiner<S, T, R> joiner;

    private S sourceEl;
    private Iterator<T> targetIter = ImmutableList.<T>of().iterator();

    HashLeftJoinIterator(Iterator<S> sourceIter, Multimap<String, T> targetMap, KeyJoiner<S, T, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(targetMap, "Target map must not be null");
        checkNotNull(joiner, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetMap = targetMap;
        this.joiner = joiner;
    }

    @Override
    protected R computeNext() {
        if(targetIter.hasNext()) return joiner.join(sourceEl, targetIter.next());
        if(!sourceIter.hasNext()) return endOfData();
        sourceEl = sourceIter.next();
        Collection<T> targetCol = targetMap.get(sourceEl.key());
        if(targetCol.size() > 0) {
            targetIter = targetCol.iterator();
            return joiner.join(sourceEl, targetIter.next());
        }
        return joiner.join(sourceEl, null);
    }
}
