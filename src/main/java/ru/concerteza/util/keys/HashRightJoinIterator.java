package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Zero copy, lazy hash right join implementation. Not thread-safe.
 * <b>NOTE!</b> removes all elements from provided multimap.
 *
 * @author alexey
 * Date: 7/13/12
 * @see ru.concerteza.util.keys.KeyOperations
 */
class HashRightJoinIterator<S extends KeyEntry, T, R> extends AbstractIterator<R> {
    private enum State{CREATED, RUNNING, EXHAUSTED, FINISHED}
    private final Iterator<S> sourceIter;
    private final Multimap<String, T> targetMap;
    private final KeyJoiner<T, S, R> joiner;

    private State state = State.CREATED;
    private S sourceEl;
    private Iterator<T> targetIter;

    /**
     * @param sourceIter source iterator, must be ordered by key
     * @param targetMap target multimap
     * @param joiner joiner instance
     */
    HashRightJoinIterator(Iterator<S> sourceIter, Multimap<String, T> targetMap, KeyJoiner<T, S, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(targetMap, "Target map must not be null");
        checkNotNull(joiner, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetMap = targetMap;
        this.joiner = joiner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected R computeNext() {
        switch(state) {
            case CREATED:
                targetIter = ImmutableList.<T>of().iterator();
                state = State.RUNNING;
            case RUNNING:
                if(targetIter.hasNext()) return joiner.join(targetIter.next(), sourceEl);
                while(sourceIter.hasNext()) {
                    S prevSourceEl = sourceEl;
                    sourceEl = nextOrdered(sourceIter, prevSourceEl);
                    Collection<T> targetCol = targetMap.get(sourceEl.key());
                    if(null != prevSourceEl && 0 != prevSourceEl.key().compareTo(sourceEl.key())) targetMap.removeAll(prevSourceEl.key());
                    if(targetCol.size() > 0) {
                        targetIter = targetCol.iterator();
                        return joiner.join(targetIter.next(), sourceEl);
                    }
                }
                if(null != sourceEl) targetMap.removeAll(sourceEl.key());
                targetIter = targetMap.values().iterator();
                state = State.EXHAUSTED;
            case EXHAUSTED:
                if(targetIter.hasNext()) return joiner.join(targetIter.next(), null);
                state = State.FINISHED;
            case FINISHED:
                return endOfData();
            default: throw new IllegalStateException("Illegal state: " + state); // cannot happen
        }
    }

    private <A extends KeyEntry> A nextOrdered(Iterator<A> iter, A current) {
        A res = iter.next();
        if(null != current) checkArgument(current.key().compareTo(res.key()) <= 0,
                "Iterator order error, current element: '%s', next element: '%s'", current, res);
        return res;
    }
}
