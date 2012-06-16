package ru.concerteza.util.db.datasource;

import org.apache.tomcat.jdbc.pool.DataSource;
import ru.concerteza.util.collection.accessor.RoundRobinAccessor;

import java.util.Collection;

/**
 * User: alexey
 * Date: 6/16/12
 */
public class RoundRobinDataSourceAccessor extends RoundRobinAccessor<DataSource> {

    public RoundRobinDataSourceAccessor(Collection<DataSource> target) {
        super(target);
    }

    public void close() {
        for(DataSource ds : target) ds.close(true);
    }
}
