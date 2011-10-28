package ru.concerteza.util.collection;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/28/11
 */

// stateful
public class LimitedSizeIterator<T> extends AbstractIterator<T> {
    private final Iterator<T> target;
    private final int limit;
    private int counter;

    private LimitedSizeIterator(Iterator<T> target, int limit) {
        checkNotNull(target);
        checkArgument(limit > 0, "Limit must be >= zero, was: %s", limit);
        this.target = target;
        this.limit = limit;
    }

    public static <T> LimitedSizeIterator<T> wrap(Iterator<T> target, int limit) {
        return new LimitedSizeIterator<T>(target, limit);
    }

    @Override
    protected T computeNext() {
        if(counter < limit && target.hasNext()) {
            counter +=1;
            return target.next();
        } else return endOfData();
    }
}
