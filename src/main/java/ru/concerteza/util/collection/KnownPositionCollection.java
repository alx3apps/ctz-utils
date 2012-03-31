package ru.concerteza.util.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class KnownPositionCollection<T> extends AbstractCollection<T> {
    private final Collection<T> target;
    private final int size;
    private KnownPositionIterator<T> kpIterator;

    private KnownPositionCollection(Collection<T> target) {
        this.target = target;
        this.size = target.size();
    }

    public static <T> KnownPositionCollection<T> wrap(Collection<T> target) {
        return new KnownPositionCollection<T>(target);
    }

    @Override
    public Iterator<T> iterator() {
        kpIterator = new KnownPositionIterator<T>(target.iterator());
        return kpIterator;
    }

    @Override
    public int size() {
        return size;
    }

    public int position() {
        return kpIterator.position();
    }

    public boolean isFirstPosition() {
        return 0 == kpIterator.position();
    }

    public boolean isLastPosition() {
        return size - 1 == kpIterator.position();
    }

    public boolean isNotLastPosition() {
        return size - 1 != kpIterator.position();
    }

    private class KnownPositionIterator<T> implements Iterator<T> {
        private final Iterator<T> target;
        private int position = -1;

        public KnownPositionIterator(Iterator<T> target) {
            this.target = target;
        }

        @Override
        public boolean hasNext() {
            return target.hasNext();
        }

        @Override
        public T next() {
            position += 1;
            return target.next();
        }

        @Override
        public void remove() {
            target.remove();
        }

        int position() {
            return position;
        }
    }
}
