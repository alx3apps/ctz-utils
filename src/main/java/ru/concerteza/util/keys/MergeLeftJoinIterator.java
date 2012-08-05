package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Zero copy, lazy merge left join implementation. Not thread-safe.
 *
 * @author  alexey
 * Date: 7/4/12
 * @see KeyOperations
 */
class MergeLeftJoinIterator<S extends KeyEntry, T extends KeyEntry, R> extends AbstractIterator<R> {
    private enum State {CREATED, SEARCHING, FOUND, TARGET_EXHAUSTED}

    private final Iterator<S> sourceIter;
    private final Iterator<T> targetIter;
    private final KeyJoiner<S, T, R> joiner;

    private State state = State.CREATED;
    private S sourceEl = null;
    private T targetEl = null;

    /**
     * @param sourceIter source iterator, must be ordered by key
     * @param targetIter target iterator, must be ordered by key
     * @param joiner joiner instance
     */
    MergeLeftJoinIterator(Iterator<S> sourceIter, Iterator<T> targetIter, KeyJoiner<S, T, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(targetIter, "Target iterator must not be null");
        checkNotNull(joiner, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetIter = targetIter;
        this.joiner = joiner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected R computeNext() {
        switch(state) {
            case CREATED:
                if(!(sourceIter.hasNext() && targetIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                targetEl = targetIter.next();
                state = State.SEARCHING;
            case SEARCHING:
            case FOUND:
                for(;;) {
                    int comp = sourceEl.key().compareTo(targetEl.key());
                    if(comp < 0) {
                        S prev = sourceEl;
                        if(sourceIter.hasNext()) sourceEl = nextOrdered(sourceIter, sourceEl);
                        if(State.SEARCHING.equals(state)) return joiner.join(prev, null);
                        state = State.SEARCHING;
                    } else if(comp > 0 && targetIter.hasNext()) {
                        targetEl = nextOrdered(targetIter, targetEl);
                        state = State.SEARCHING;
                    } else if(0 == comp) {
                        R res = joiner.join(sourceEl, targetEl);
                        if(targetIter.hasNext()) {
                            targetEl = nextOrdered(targetIter, targetEl);
                            state = State.FOUND;
                        } else state = State.TARGET_EXHAUSTED;
                        return res;
                    } else break;
                }
            case TARGET_EXHAUSTED:
                if(!sourceIter.hasNext()) return endOfData();
                sourceEl = nextOrdered(sourceIter, sourceEl);
                return joiner.join(sourceEl, null);
            default:
                throw new IllegalStateException("Illegal state: " + state); // cannot happen
        }
    }

    private <A extends KeyEntry> A nextOrdered(Iterator<A> iter, A current) {
        A res = iter.next();
        checkArgument(current.key().compareTo(res.key()) <= 0,
                "Iterator order error, current element: '%s', next element: '%s'", current, res);
        return res;
    }
}
