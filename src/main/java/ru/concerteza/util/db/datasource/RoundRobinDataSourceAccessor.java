package ru.concerteza.util.db.datasource;

import org.apache.tomcat.jdbc.pool.DataSource;
import ru.concerteza.util.collection.accessor.RoundRobinAccessor;

import java.util.Collection;

/**
 * Round robin accessor for tomcat data sources with support to {@code close()} method
 *
 * @author alexey
 * Date: 6/16/12
 */
public class RoundRobinDataSourceAccessor extends RoundRobinAccessor<DataSource> {

    /**
     * @param target list of data sources
     */
    public RoundRobinDataSourceAccessor(Collection<DataSource> target) {
        super(target);
    }

    /**
     * closes all data sources in accessor
     */
    public void close() {
        for(DataSource ds : target) ds.close(true);
    }
}
