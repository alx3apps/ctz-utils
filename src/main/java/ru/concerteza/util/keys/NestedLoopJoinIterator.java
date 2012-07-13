package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

/**
* User: alexey
* Date: 7/13/12
*/
class NestedLoopJoinIterator <S extends KeyEntry, T extends KeyEntry, R> extends AbstractIterator<R> {
    private enum State{CREATED, STARTED, FINISHED}

    private final Iterator<S> sourceIter;
    private final Iterable<T> targetIterable;
    private final KeyJoiner<S, T, R> joiner;

    private State state = State.CREATED;
    private S sourceEl;
    private Iterator<T> targetIter;

    NestedLoopJoinIterator(Iterator<S> sourceIter, Iterable<T> targetIter, KeyJoiner<S, T, R> joiner) {
        this.sourceIter = sourceIter;
        this.targetIterable = targetIter;
        this.joiner = joiner;
    }

    @Override
    protected R computeNext() {
        switch (state) {
            case CREATED:
                if(!(sourceIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                targetIter = targetIterable.iterator();
                state = State.STARTED;
            case STARTED:
                for(;;) { // source loop
                    while (targetIter.hasNext()) { // target loop
                        T targetEl = targetIter.next();
                        if(sourceEl.key().equals(targetEl.key())) {
                            return joiner.join(sourceEl, targetEl);
                        }
                    }
                    if(sourceIter.hasNext()) sourceEl = sourceIter.next();
                    else break;
                    targetIter = targetIterable.iterator();
                }
                state = State.FINISHED;
            case FINISHED:
                return endOfData();
            default: throw new IllegalStateException("Illegal state: " + state); // cannot happen
        }
    }
}
