package ru.concerteza.util.keys;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/18/12
 */
class GroupOrderedByKeyIterator<S extends KeyEntry, R> extends AbstractIterator<R> {
    private enum State{CREATED, RUNNING, FINISHED}

    private final Iterator<S> sourceIter;
    private final KeyAggregator<S, R> aggregator;

    private State state = State.CREATED;
    private String key;
    private S sourceEl;

    GroupOrderedByKeyIterator(Iterator<S> sourceIter, KeyAggregator<S, R> aggregator) {
        checkNotNull(sourceIter, "Source iterator must not be null");
        checkNotNull(aggregator, "Aggregator must not be null");
        this.sourceIter = sourceIter;
        this.aggregator = aggregator;
    }

    @Override
    protected R computeNext() {
        switch (state) {
            case CREATED:
                if(!(sourceIter.hasNext())) return endOfData();
                sourceEl = sourceIter.next();
                key = sourceEl.key();
                state = State.RUNNING;
            case RUNNING:
                R aggr = null;
                while (key.equals(sourceEl.key())) {
                    aggr = aggregator.aggregate(sourceEl, aggr);
                    if(!sourceIter.hasNext()) {
                        state = State.FINISHED;
                        return aggr;
                    }
                    sourceEl = nextOrdered(sourceIter, sourceEl);
                }
                key = sourceEl.key();
                return aggr;
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