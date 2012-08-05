package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Zero copy, lazy nested loop left join implementation. Not thread-safe.
 *
 * @author alexey
 * Date: 7/13/12
 * @see  KeyOperations
 */
class NestedLoopLeftJoinIterator<S extends KeyEntry, T extends KeyEntry, R> extends AbstractIterator<R> {
    private enum State{CREATED, RUNNING, FOUND, FINISHED}

    private final Iterator<S> sourceIter;
    private final Iterable<T> targetIterable;
    private final KeyJoiner<S, T, R> joiner;

    private State state = State.CREATED;
    private S sourceEl;
    private Iterator<T> targetIter;

    /**
     * @param sourceIter source iterator
     * @param targetIter target iterable
     * @param joiner joiner instance
     */
    NestedLoopLeftJoinIterator(Iterator<S> sourceIter, Iterable<T> targetIter, KeyJoiner<S, T, R> joiner) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(targetIter, "Target iterator must not be null");
        checkNotNull(joiner, "Joiner must not be null");
        this.sourceIter = sourceIter;
        this.targetIterable = targetIter;
        this.joiner = joiner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected R computeNext() {
        switch (state) {
            case CREATED:
                if(!(sourceIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                targetIter = targetIterable.iterator();
                state = State.RUNNING;
            case RUNNING:
            case FOUND:
                for(;;) { // source loop
                    while(targetIter.hasNext()) { // target loop
                        T targetEl = targetIter.next();
                        if(sourceEl.key().equals(targetEl.key())) {
                            state = State.FOUND;
                            return joiner.join(sourceEl, targetEl);
                        }
                    }
                    if(State.RUNNING.equals(state)) {
                        S prev = sourceEl;
                        if(!advance()) state = State.FINISHED;
                        return joiner.join(prev, null);
                    }
                    state = State.RUNNING;
                    if(!advance()) break;
                }
                state = State.FINISHED;
            case FINISHED:
                return endOfData();
            default: throw new IllegalStateException("Illegal state: " + state); // cannot happen
        }
    }

    private boolean advance() {
        if(sourceIter.hasNext()) {
            sourceEl = sourceIter.next();
            targetIter = targetIterable.iterator();
            return true;
        }
        return false;
    }
}
