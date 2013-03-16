package ru.concerteza.util.db.springjdbc.parallel;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.apache.commons.lang.UnhandledException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.concerteza.util.collection.accessor.Accessor;
import ru.concerteza.util.collection.accessor.RoundRobinAccessor;
import ru.concerteza.util.concurrency.FirstValueHolder;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Executes single SQL query to multiple data sources in parallel using provided executor.
 * Provides data to client as iterator, {@link ArrayBlockingQueue} is used for temporary buffering.
 * Iteration will block awaiting data loaded from sources.
 * Typical usage is to get new instance somewhere (spring prototype bean etc.), provide query params
 * with <code>start</code> method and iterate over until end.
 * Data source exceptions will be propagates as runtime exceptions thrown on 'next()' or 'hasNext()' call.
 * All parallel queries will be cancelled on one query error.
 * <b>NOT</b> thread-safe (tbd: specify points that break thread safety), instance may be reused calling <code>start</code> method, but only in one thread simultaneously.
 *
 * @author  alexey
 * Date: 6/8/12
 * @see ParallelQueriesListener
 * @see Accessor
 * @see ParallelQueriesIteratorTest
 */

@Deprecated //use com.alexkasko.springjdbc:parallel-queries
public class ParallelQueriesIterator<T> extends AbstractIterator<T> {
    private final Object endOfDataObject = new Object();
    private final FirstValueHolder<RuntimeException> exceptionHolder = new FirstValueHolder<RuntimeException>();

    private final Accessor<? extends DataSource> sources;
    private final String sql;
    private final RowMapperFactory<T> mapperFactory;
    private final ExecutorService executor;
    // was made non-generic to allow endOfDataObject
    private final ArrayBlockingQueue<Object> dataQueue;
    private final List<ParallelQueriesListener> listeners = Lists.newArrayList();

    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicInteger sourcesRemained = new AtomicInteger(0);
    private ImmutableList<Future<?>> futures;

    /**
     * Shortcut constructor with mapper
     *
     * @param sources list of data sources, will be used in round-robin mode
     * @param sql query to execute using NamedParameterJdbcTemplate
     * @param mapper will be used to get data from result sets
     */
    @SuppressWarnings("unchecked")
    public ParallelQueriesIterator(List<DataSource> sources, String sql, RowMapper<T> mapper) {
        this(RoundRobinAccessor.of(sources), sql, Executors.newCachedThreadPool(), mapper, 1024);
    }

    /**
     * Shortcut constructor
     *
     * @param sources list of data sources, will be used in round-robin mode
     * @param sql query to execute using NamedParameterJdbcTemplate
     * @param executor executor service to run parallel queries into
     * @param mapper will be used to get data from result sets
     * @param bufferSize size of ArrayBlockingQueue data buffer
     */
    public ParallelQueriesIterator(Accessor<? extends DataSource> sources, String sql, ExecutorService executor, RowMapper<T> mapper, int bufferSize) {
        this(sources, sql, executor, SingletoneRowMapperFactory.of(mapper), bufferSize);
    }

    /**
     * Main constructor
     *
     * @param sources data sources accessor
     * @param sql query to execute using NamedParameterJdbcTemplate
     * @param mapperFactory will be used to get data from result sets
     * @param executor executor service to run parallel queries into
     * @param bufferSize size of ArrayBlockingQueue data buffer
     */
    public ParallelQueriesIterator(Accessor<? extends DataSource> sources, String sql, ExecutorService executor, RowMapperFactory<T> mapperFactory, int bufferSize) {
        checkNotNull(sources, "Provided data source accessor is null");
        checkArgument(sources.size() > 0, "No data sources provided");
        checkArgument(isNotBlank(sql), "Provided sql query is blank");
        checkNotNull(executor, "Provided executor is null");
        checkNotNull(mapperFactory, "Provided row mapper factory is null");
        checkArgument(bufferSize > 0, "Buffer size mat be positive, but was: '%s'", bufferSize);
        this.sources = sources;
        this.sql = sql;
        this.mapperFactory = mapperFactory;
        this.executor = executor;
        this.dataQueue = new ArrayBlockingQueue<Object>(bufferSize);
    }

    /**
     * Starts parallel query execution in data sources. May be called multiple times to reuse iterator instance,
     *
     * @param params query params
     * @return iterator itself
     */
    public ParallelQueriesIterator<T> start(Collection<? extends SqlParameterSource> params) {
        checkNotNull(params, "Provided parameters collection is null");
        checkArgument(params.size() > 0, "Provided collection is empty");
        cancel();
        this.dataQueue.clear();
        this.sourcesRemained.set(params.size());
        this.futures = ImmutableList.copyOf(Collections2.transform(params, new SubmitFun()));
        this.started.set(true);
        return this;
    }

    /**
     * @return next already read record or block awaiting it
     * @throws ParallelQueriesException on exception in any data source
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T computeNext() {
        checkState(started.get(), "Iterator wasn't started, call 'start' method first");
        Object ob;
        while(endOfDataObject == (ob = takeData())) {
            if(0 == sourcesRemained.decrementAndGet()) return endOfData();
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
        if(!started.get()) return 0;
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
        private final SqlParameterSource params;

        private Worker(DataSource ds, SqlParameterSource params) {
            this.ds = ds;
            this.jt = new NamedParameterJdbcTemplate(ds);
            this.params = params;
        }

        @Override
        public void run() {
            try {
                Extractor extractor = new Extractor(mapperFactory.produce(params));
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
        private final RowMapper<T> mapper;

        private Extractor(RowMapper<T> mapper) {
            this.mapper = mapper;
        }

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

    private class SubmitFun implements Function<SqlParameterSource, Future<?>> {
        @Override
        public Future<?> apply(@Nullable SqlParameterSource params) {
            Worker worker = new Worker(sources.get(), params);
            return executor.submit(worker);
        }
    }
}
