package ru.concerteza.util.collection.accessor;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract accessor implementation as a wrapper around provided collection.
 * Copies provided collection into inner immutable one on creation. Thread-safe.
 *
 * @author alexey
 * Date: 6/11/12
 * @see Accessor
 */
@Deprecated // use com.alexkasko.springjdbc.parallel.accessor
public abstract class AbstractAccessor<T> implements Accessor<T> {
    protected final List<T> target;

    /**
     * Protected constructor, copies provided collection into inner immutable one
     *
     * @param target collection to wrap
     */
    protected AbstractAccessor(Collection<T> target) {
        checkNotNull(target, "Target collection must be not null");
        checkArgument(target.size() > 0, "Target collection must be not empty");
        this.target = ImmutableList.copyOf(target);
    }

    /**
     * @return target collection size
     */
    @Override
    public int size() {
        return target.size();
    }

    /**
     * @return <code>false</code>
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * @param o object to check
     * @return whether target collection contains provided object
     */
    @Override
    public boolean contains(Object o) {
        return target.contains(0);
    }

    /**
     * @return target collection iterator
     */
    @Override
    public Iterator<T> iterator() {
        return target.iterator();
    }

    /**
     * @return target collection <code>toArray()</code> result
     */
    @Override
    public Object[] toArray() {
        return target.toArray();
    }

    /**
     * @param a array to copy values into
     * @param <T> target collection generic parameter
     * @return target collection <code>toArray</code> result
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return target.toArray(a);
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param c collection to check
     * @return whether target collection contains all provided values
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return target.containsAll(c);
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
