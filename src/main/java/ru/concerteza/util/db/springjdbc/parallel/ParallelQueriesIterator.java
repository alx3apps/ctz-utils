package ru.concerteza.util.db.springjdbc.parallel;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.UnhandledException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.concerteza.util.collection.accessor.Accessor;
import ru.concerteza.util.collection.accessor.RoundRobinAccessor;
import ru.concerteza.util.concurrency.FirstValueHolder;
import ru.concerteza.util.db.datasource.RoundRobinDataSourceAccessor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * Executes single SQL query to multiple data sources in parallel using provided executor.
 * Provides data to client as iterator, {@link ArrayBlockingQueue} is used for temporary buffering.
 * Iteration will block awaiting data loaded from sources.
 * Typical usage is to get new instance somewhere (spring prototype bean etc.), provide query params
 * with <code>start</code> method and iterate over until end.
 * Data source exceptions will be propagates as runtime exceptions thrown on 'next()' or 'hasNext()' call.
 * All parallel queries will be cancelled on one query error.
 * <b>NOT</b> thread-safe, instance may be reused calling <code>start</code> method, but only in one thread simultaneously.
 *
 * @author  alexey
 * Date: 6/8/12
 * @see ParallelQueriesForker
 * @see ParallelQueriesListener
 * @see Accessor
 * @see ParallelQueriesIteratorTest
 */

public class ParallelQueriesIterator<T> extends AbstractIterator<T> {
    private final Object endOfDataObject = new Object();
    private final FirstValueHolder<RuntimeException> exceptionHolder = new FirstValueHolder<RuntimeException>();

    private final Accessor<? extends DataSource> sources;
    private final String sql;
    private final ParallelQueriesForker forker;
    private final RowMapper<T> mapper;
    private final ExecutorService executor;
    // was made non-generic to allow endOfDataObject
    private final ArrayBlockingQueue<Object> dataQueue;
    private final Extractor extractor = new Extractor();
    private final List<ParallelQueriesListener> listeners = Lists.newArrayList();

    private List<Map<String, ?>> paramsList;

    private boolean started = false;
    private int sourcesRemained;
    private List<Future<?>> futures;

    /**
     * Shortcut constructor
     *
     * @param sources list of data sources, will be used in round-robin mode
     * @param sql query to execute using NamedParameterJdbcTemplate
     * @param forker function to divide provided parameters between threads
     */
    @SuppressWarnings("unchecked")
    public ParallelQueriesIterator(List<DataSource> sources, String sql, ParallelQueriesForker forker) {
        this.sources = RoundRobinAccessor.of(sources);
        this.sql = sql;
        this.forker = forker;
        this.mapper = (RowMapper) new ColumnMapRowMapper();
        this.executor = Executors.newCachedThreadPool();
        this.dataQueue = new ArrayBlockingQueue<Object>(1024);
    }

    /**
     * Main constructor
     *
     * @param sources data sources accessor
     * @param sql query to execute using NamedParameterJdbcTemplate
     * @param mapper will be used to get data from result sets
     * @param forker function to divide provided parameters between threads
     * @param executor executor service to run parallel queries into
     * @param bufferSize size of ArrayBlockingQueue data buffer
     */
    public ParallelQueriesIterator(Accessor<? extends DataSource> sources, String sql, ParallelQueriesForker forker, ExecutorService executor, RowMapper<T> mapper, int bufferSize) {
        this.sources = sources;
        this.sql = sql;
        this.forker = forker;
        this.mapper = mapper;
        this.executor = executor;
        this.dataQueue = new ArrayBlockingQueue<Object>(bufferSize);
    }

    /**
     * Starts parallel query execution in data sources. May be called multiple times to reuse iterator instance,
     *
     * @param params query params, will be dived using provided {@link ParallelQueriesForker}
     * @return iterator itself
     */
    public ParallelQueriesIterator<T> start(Map<String, ?> params) {
        cancel();
        this.dataQueue.clear();
        this.paramsList = this.forker.fork(params);
        this.sourcesRemained = this.paramsList.size();
        this.futures = ImmutableList.copyOf(Iterables.transform(this.paramsList, new SubmitFun()));
        this.started = true;
        return this;
    }

    /**
     * @return next already read record or block awaiting it
     * @throws ParallelQueriesException on exception in any data source
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T computeNext() {
        checkState(started, "Iterator wasn't started, call 'start' method first");
        Object ob;
        while(endOfDataObject == (ob = takeData())) {
            sourcesRemained -= 1;
            if(0 == sourcesRemained) return endOfData();
        }
        if(exceptionHolder == ob) {
            cancel();
            throw exceptionHolder.get();
        }
        return (T) ob;
    }

    /**
     * Cancels queries processing in all sources
     *
     * @return count of queries that were actually interrupted in processing
     */
    public int cancel() {
        if(!started) return 0;
        int res = 0;
        for (Future<?> fu : futures) {
            if(fu.cancel(true)) res += 1;
        }
        return res;
    }

    /**
     * @param listener data source query events will be reported to this listener
     * @return iterator itself
     */
    public ParallelQueriesIterator<T> addListener(ParallelQueriesListener listener) {
        this.listeners.add(listener);
        return this;
    }

    private void putData(Object data) {
        try {
            dataQueue.put(data);
        } catch(InterruptedException e) {
            throw new UnhandledException(e);
        }
    }

    private Object takeData() {
        try {
            return dataQueue.take();
        } catch(InterruptedException e) {
            throw new UnhandledException(e);
        }
    }

    private class Worker implements Runnable {
        private final DataSource ds;
        private final NamedParameterJdbcTemplate jt;
        private final Map<String, ?> params;

        private Worker(DataSource ds, Map<String, ?> params) {
            this.ds = ds;
            this.jt = new NamedParameterJdbcTemplate(ds);
            this.params = params;
        }

        @Override
        public void run() {
            try {
                jt.query(sql, params, extractor);
                for(ParallelQueriesListener li : listeners) li.success(ds, sql, params);
            } catch (Throwable e) { // we do not believe to JDBC drivers' error reporting
                exceptionHolder.set(new ParallelQueriesException(e));
                putData(exceptionHolder);
                for(ParallelQueriesListener li : listeners) li.error(ds, sql, params, e);
            }
        }
    }

    private class Extractor implements ResultSetExtractor<Void> {
        @Override
        public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
            try {
                int rowNum = 0;
                while(rs.next()) {
                    Object obj = mapper.mapRow(rs, rowNum++);
                    dataQueue.put(obj);
                }
                dataQueue.put(endOfDataObject);
            } catch(Throwable e) { // we do not believe to JDBC drivers' error reporting
                exceptionHolder.set(new ParallelQueriesException(e));
                putData(exceptionHolder);
            }
            return null;
        }
    }

    private class SubmitFun implements Function<Map<String, ?>, Future<?>> {
        @Override
        public Future<?> apply(Map<String, ?> params) {
            Worker worker = new Worker(sources.get(), params);
            return executor.submit(worker);
        }
    }
}
