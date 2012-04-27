package ru.concerteza.util.db.hibernate;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * User: alexey
 * Date: 8/18/11
 */

// do not use with lazy collections, they cause LIE after threshold exceed
public class EntityIterator<T> extends AbstractIterator<T> {
    private final ScrollableResults target;
    private final SessionFactory sf;
    private final int threshold;
    private int readCount = 0;

    public EntityIterator(SessionFactory sf, ScrollableResults target, int threshold) {
        this.target = target;
        this.sf = sf;
        this.threshold = threshold;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("readCount", readCount).
                toString();
    }
}