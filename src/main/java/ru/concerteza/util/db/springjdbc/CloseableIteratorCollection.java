package ru.concerteza.util.db.springjdbc;

import com.alexkasko.springjdbc.iterable.CloseableIterator;
import com.google.common.collect.Iterators;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Iterator;

/**
 * Concat wrapper for closeable iterator's
 *
 * @author alexkasko
 * Date: 5/11/13
 */
@Deprecated // use CloseableIteratorList
public class CloseableIteratorCollection<T> implements CloseableIterator<T> {
    private final Collection<CloseableIterator<T>> list;
    private final Iterator<T> iter;

    public CloseableIteratorCollection(Collection<CloseableIterator<T>> list) {
        this.list = list;
        this.iter = Iterators.concat(list.iterator());
    }

    public static <T> CloseableIteratorCollection<T> of(Collection<CloseableIterator<T>> list) {
        return new CloseableIteratorCollection<T>(list);
    }

    @Override
    public void close() {
        for (CloseableIterator<T> ci : list) ci.close();
    }

    @Override
    public boolean isClosed() {
        for (CloseableIterator<T> ci : list) {
            if (!ci.isClosed()) return false;
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public T next() {
        return iter.next();
    }

    @Override
    public void remove() {
        iter.remove();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("list", list).
                toString();
    }
}
