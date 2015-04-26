package ru.concerteza.util.db.springjdbc;

import com.alexkasko.springjdbc.iterable.CloseableIterable;
import com.alexkasko.springjdbc.iterable.CloseableIterator;
import com.google.common.collect.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Iterator;

/**
 * Iterator wrapper for closeable iterables list,
 * NOT thread-safe.
 *
 * @author alexkasko
 * Date: 5/11/13
 */
public class CloseableIteratorList<T> extends AbstractIterator<T> implements CloseableIterator<T> {
    private final ImmutableList<CloseableIterable<T>> list;
    private CloseableIterator<T> iter = null;
    private int index = 0;

    /**
     * Constructor
     *
     * @param list closeable iterables list
     */
    public CloseableIteratorList(Collection<CloseableIterable<T>> list) {
        this.list = ImmutableList.copyOf(list);
    }

    /**
     * Vararg constructor
     *
     * @param iters closeable iterables
     */
    public CloseableIteratorList(CloseableIterable<T>... iters) {
        this.list = ImmutableList.copyOf(iters);
    }

    /**
     * Generic friendly factory method
     *
     * @param list closeable iterables list
     * @param <T> element type
     * @return iterator instance
     */
    public static <T> CloseableIteratorList<T> of(Collection<CloseableIterable<T>> list) {
        return new CloseableIteratorList<T>(list);
    }

    /**
     * Generic friendly factory method
     *
     * @param iters closeable iterables
     * @param <T> element type
     * @return iterator instance
     */
    public static <T> CloseableIteratorList<T> of(CloseableIterable<T>... iters) {
        return new CloseableIteratorList<T>(iters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        for (CloseableIterable<T> ci : list) {
            ci.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() {
        for (CloseableIterable<T> ci : list) {
            if (!ci.isClosed()) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T computeNext() {
        if(0 == index) {
            if(0 == list.size()) return endOfData();
            // actual first iter opening is here
            iter = list.get(index).iterator();
            index += 1;
        }
        while (!iter.hasNext()) {
            iter.close();
            if (index >= list.size()) return endOfData();
            iter = list.get(index).iterator();
            index += 1;
        }
        return iter.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("list", list).
                append("index", index).
                toString();
    }
}
