package ru.concerteza.util.collection;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang.UnhandledException;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/28/11
 */
public class LimitedCatchErrorIterator<T> extends AbstractIterator<T> {
    private final Iterator<T> target;
    private final Class<? extends Error> limitErrorClass;

    private LimitedCatchErrorIterator(Iterator<T> target, Class<? extends Error> limitErrorClass) {
        checkNotNull(target);
        checkNotNull(limitErrorClass);
        this.target = target;
        this.limitErrorClass = limitErrorClass;
    }

    public static <T> LimitedCatchErrorIterator<T> wrap(Iterator<T> target, Class<? extends Error> limitErrorClass) {
        return new LimitedCatchErrorIterator<T>(target, limitErrorClass);
    }

    @Override
    protected T computeNext() {
        try {
            return target.hasNext() ? target.next() : endOfData();
        } catch (Error e) {
            if(e.getClass().getName().equals(limitErrorClass.getName())) {
                return endOfData();
            } else throw new UnhandledException(e);
        }
    }
}
