package ru.concerteza.util.db.hibernate;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Iterator wrapper over hibernates query results.
 * Won't work with lazy collections, they cause LazyInitializationException after threshold exceed
 *
 * @author alexey
 * Date: 8/18/11
 */
public class EntityIterator<T> extends AbstractIterator<T> {
    private final ScrollableResults target;
    private final SessionFactory sf;
    private final int threshold;
    private int readCount = 0;

    /**
     * @param sf session factory
     * @param target scrollable results
     * @param threshold rows read before session clean threashold
     */
    public EntityIterator(SessionFactory sf, ScrollableResults target, int threshold) {
        this.target = target;
        this.sf = sf;
        this.threshold = threshold;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T computeNext() {
        final T res;
        boolean success = target.next();
        if(success) {
            readCount += 1;
            if (0 == readCount % threshold) {
                Session session = sf.getCurrentSession();
                session.flush();
                session.clear();
            }
            res = (T) target.get(0);
        } else {
            target.close();
            res = super.endOfData();
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("readCount", readCount).
                toString();
    }
}