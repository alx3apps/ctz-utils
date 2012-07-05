package ru.concerteza.util.collection.join;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/4/12
 */

// not thread-safe
public class JoinedIterator<I extends Comparable<I>, S extends I, T extends I, R> extends AbstractIterator<R> {
    private enum State{CREATED, STARTED, FINISHED}

    private final Iterator<S> sourceIter;
    private final Iterator<T> targetIter;
    private final Joiner<S, T, R> joiner;

    private State state = State.CREATED;
    private S sourceEl = null;
    private T targetEl = null;

    public JoinedIterator(Iterator<S> sourceIter, Iterator<T> targetIter, Joiner<S, T, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(sourceIter, "Target iterator must not be null");
        checkNotNull(sourceIter, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetIter = targetIter;
        this.joiner = joiner;
    }

    public static <I extends Comparable<I>, S extends I, T extends I, R>
        JoinedIterator<I, S, T, R> of(Iterator<S> sourceIter, Iterator<T> targetIter, Joiner<S, T, R> joiner) {
        return new JoinedIterator<I, S, T, R>(sourceIter, targetIter, joiner);
    }

    @Override
    protected R computeNext() {
        switch (state) {
            case CREATED:
                if(!(sourceIter.hasNext() && targetIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                targetEl = targetIter.next();
                state = State.STARTED;
            case STARTED:
                for(;;) {
                    int comp = sourceEl.compareTo(targetEl);
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

    private <T extends I> T nextOrdered(Iterator<T> iter, T current) {
        T res = iter.next();
        checkArgument(current.compareTo(res) <= 0,
                "Iterator order error, current element: '%s', next element: '%s'", current, res);
        return res;
    }
}
