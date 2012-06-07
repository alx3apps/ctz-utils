package ru.concerteza.util.db.springjdbc.parallel;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.UnhandledException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * User: alexey
 * Date: 6/8/12
 */

// todo generify me
public class ParallelQueryIterator extends AbstractIterator<Map<String, Object>> {
    private final Map<String, Object> endOfForkedData = ImmutableMap.of();
    private Executor executor;
    private List<Map<String, Object>> paramsList;
    private int bufferCapacity;
    String sql;
    Map<String, Object> params;
    List<DataSource> sources;
    int forkSize;
    boolean started;

    private ArrayBlockingQueue<Map<String, Object>> resultsQueue = new ArrayBlockingQueue<Map<String, Object>>(bufferCapacity);

    private void fire(){
        for(int i = 0; i < paramsList.size(); i++) {
            Worker worker = new Worker(sql, sources.get(i % sources.size()), paramsList.get(i));
            executor.execute(worker);
        }
        forkSize = paramsList.size();
        started = true;
    }

    @Override
    protected Map<String, Object> computeNext() {
        if(!started) fire();
        try {
            Map<String, Object> map = resultsQueue.take();
            if(map == endOfForkedData) {
                forkSize -= 1;
                if(0 == forkSize) endOfData();
            }
            return map;
        } catch(InterruptedException e) {
            throw new UnhandledException(e);
        }
    }

    private class Worker implements Runnable {
        private final Extractor extractor = new Extractor();
        private final String sql;
        private final NamedParameterJdbcTemplate jt;
        private final Map<String, Object> params;

        private Worker(String sql, DataSource ds, Map<String, Object> params) {
            this.sql = sql;
            this.jt = new NamedParameterJdbcTemplate(ds);
            this.params = params;
        }

        @Override
        public void run() {
            jt.query(sql, params, extractor);
        }
    }

    private class Extractor implements ResultSetExtractor<Void> {
        private final ColumnMapRowMapper mapper = new ColumnMapRowMapper();

        @Override
        public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
            try {
                while(rs.next()) {
                    resultsQueue.put(mapper.mapRow(rs, 0));
                }
                resultsQueue.put(endOfForkedData);
                return null;
            } catch(InterruptedException e) {
                throw new SQLException(e);
            }
        }
    }
}
