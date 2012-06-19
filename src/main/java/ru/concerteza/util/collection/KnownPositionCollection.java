package ru.concerteza.util.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrapper for collection, that knows current collection position on iteration.
 * May be useful for special processing of first/last elements (e.g. last comma etc.).
 * Thread-safe if target collection is thread-safe,
 *
 * @author alexey
 * Date: 8/11/11
 * @see KnownPositionCollectionTest
 */
public class KnownPositionCollection<T> extends AbstractCollection<T> {
    private final Collection<T> target;
    private final int size;
    private KnownPositionIterator<T> kpIterator;

    /**
     * Protected constructor, use {@link KnownPositionCollection#of(java.util.Collection)} instead
     *
     * @param target target collection
     */
    protected KnownPositionCollection(Collection<T> target) {
        this.target = target;
        this.size = target.size();
    }

    /**
     * Creates {@link KnownPositionCollection} as a wrapper over provided collection
     *
     * @param target target collection
     * @param <T> target collection generic parameter
     * @return KnownPositionCollection
     */
    public static <T> KnownPositionCollection<T> of(Collection<T> target) {
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

    /**
     * @return current iteration position
     */
    public int position() {
        return kpIterator.position();
    }

    /**
     * @return whether current iteration position is first position
     */
    public boolean isFirstPosition() {
        return 0 == kpIterator.position();
    }

    /**
     * @return whether current iteration position is last position
     */
    public boolean isLastPosition() {
        return size - 1 == kpIterator.position();
    }

    /**
     * @return whether current iteration position is not last position
     */
    public boolean isNotLastPosition() {
        return size - 1 != kpIterator.position();
    }

    private class KnownPositionIterator<T> implements Iterator<T> {
        private final Iterator<T> target;
        private AtomicInteger position = new AtomicInteger(-1);

        public KnownPositionIterator(Iterator<T> target) {
            this.target = target;
        }

        @Override
        public boolean hasNext() {
            return target.hasNext();
        }

        @Override
        public T next() {
            position.incrementAndGet();
            return target.next();
        }

        @Override
        public void remove() {
            target.remove();
        }

        /**
         * @return current iterator position
         */
        int position() {
            return position.get();
        }
    }
}
