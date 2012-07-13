package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/4/12
 */

// not thread-safe
class MergeJoinIterator<S extends KeyEntry, T extends KeyEntry, R> extends AbstractIterator<R> {
    private enum State{CREATED, RUNNING, FINISHED}

    private final Iterator<S> sourceIter;
    private final Iterator<T> targetIter;
    private final KeyJoiner<S, T, R> joiner;

    private State state = State.CREATED;
    private S sourceEl = null;
    private T targetEl = null;

    public MergeJoinIterator(Iterator<S> sourceIter, Iterator<T> targetIter, KeyJoiner<S, T, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(sourceIter, "Target iterator must not be null");
        checkNotNull(sourceIter, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetIter = targetIter;
        this.joiner = joiner;
    }

    @Override
    protected R computeNext() {
        switch (state) {
            case CREATED:
                if(!(sourceIter.hasNext() && targetIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                targetEl = targetIter.next();
                state = State.RUNNING;
            case RUNNING:
                for(;;) {
                    int comp = sourceEl.key().compareTo(targetEl.key());
                    if(comp < 0 && sourceIter.hasNext()) {
                        sourceEl = nextOrdered(sourceIter, sourceEl);
                    } else if(comp > 0 && targetIter.hasNext()) {
                        targetEl = nextOrdered(targetIter, targetEl);
                    } else if(0 == comp) {
                        R res = joiner.join(sourceEl, targetEl);
                        if(targetIter.hasNext()) targetEl = nextOrdered(targetIter, targetEl);
                        else state = State.FINISHED;
                        return res;
                    } else break;
                }
                state = State.FINISHED;
            case FINISHED:
                return endOfData();
            default: throw new IllegalStateException("Illegal state: " + state); // cannot happen
        }
    }

    private <A extends KeyEntry> A nextOrdered(Iterator<A> iter, A current) {
        A res = iter.next();
        checkArgument(current.key().compareTo(res.key()) <= 0,
                "Iterator order error, current element: '%s', next element: '%s'", current, res);
        return res;
    }
}
