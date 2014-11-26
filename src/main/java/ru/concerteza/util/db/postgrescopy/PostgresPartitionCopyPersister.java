package ru.concerteza.util.db.postgrescopy;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.text.StrSubstitutor;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.collection.CountingIterator;
import ru.concerteza.util.collection.SingleUseIterable;
import ru.concerteza.util.db.partition.Partition;
import ru.concerteza.util.db.partition.PartitionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static ru.concerteza.util.collection.maps.MapValueFunction.mapValueFunction;
import static ru.concerteza.util.db.postgrescopy.PostgresCopyPersister.EOF_BYTES;
import static ru.concerteza.util.db.postgrescopy.PostgresCopyPersister.HEADER_BYTES;
import static ru.concerteza.util.io.CtzIOUtils.closeQuietly;

/**
 * User: alexkasko
 * Date: 11/10/14
 */
public class PostgresPartitionCopyPersister {
    private static final Logger logger = LoggerFactory.getLogger(PostgresCopyPersister.class);

    private final DataSource ds;
    private final PartitionManager pm;

    public PostgresPartitionCopyPersister(DataSource ds, PartitionManager pm) {
        this.ds = ds;
        this.pm = pm;
    }

    public long persist(PostgresPartitionCopyProvider provider, String sqlTemplate, String table, String uid, Iterator<byte[]> data) {
        checkArgument(isNotBlank(sqlTemplate), "Provided sqlTemplate is blank");
        checkNotNull(data, "Provided data iter is null");
        checkNotNull(provider, "Provided copy provider is null");
        Map<String, CopySession> map = new HashMap<String, CopySession>();
        try {
            CountingIterator<byte[]> counter = CountingIterator.of(data);
//            long bytes = 0;
            for (byte[] input : SingleUseIterable.of(counter)) {
                Partition part = pm.ensurePartition(table, provider.date(input), uid);
                CopySession cs = cs(provider, map, sqlTemplate, part);
                cs.write(input);
//                bytes += input.length;
            }
            end(map);
            return counter.getCount();
        } catch (Exception e) {
            cancel(map);
            throw e instanceof PostgresCopyException ? (PostgresCopyException) e : new PostgresCopyException(e);
        }
    }

    private void cancel(Map<String, CopySession> map) {
        Collection<CopySession> list = Collections2.transform(map.entrySet(), mapValueFunction(String.class, CopySession.class));
        for (CopySession cs : list) cs.rollback();
    }

    private void end(Map<String, CopySession> map) {
        List<CopySession> list = ImmutableList.copyOf(Collections2.transform(map.entrySet(), mapValueFunction(String.class, CopySession.class)));
        for (CopySession cs : list) cs.flush();
        for (CopySession cs : list) cs.commit();
        for (CopySession cs : list) cs.close();
    }

    private CopySession cs(PostgresPartitionCopyProvider provider, Map<String, CopySession> map, String sqlTemplate, Partition part) throws SQLException {
        CopySession res = map.get(part.getPostfix());
        if (null != res) return res;
        CopySession cs = new CopySession(ds, provider, sqlTemplate, part);
        map.put(part.getPostfix(), cs);
        return cs;
    }

    private static class CopySession {
        private final Connection wrapper;
        private final CopyManager cm;
        private final Partition part;
        private final PostgresPartitionCopyProvider provider;
        private final byte[] buf;
        private CopyIn ci;
        private String sql;

        private CopySession(DataSource ds, PostgresPartitionCopyProvider provider, String sqlTemplate, Partition part) throws SQLException {
            this.part = part;
            this.provider = provider;
            this.buf = new byte[provider.maxSize()];
            this.wrapper = ds.getConnection();
            PGConnection bare = unwrap(wrapper);
            this.cm = bare.getCopyAPI();
            begin();
            this.sql = StrSubstitutor.replace(sqlTemplate, ImmutableMap.of("partition", part.getPostfix()));
            this.ci = cm.copyIn(sql);
            ci.writeToCopy(HEADER_BYTES, 0, HEADER_BYTES.length);
        }

        private void write(byte[] data) throws SQLException {
            int len = provider.fillCopyBuf(data, buf);
            ci.writeToCopy(buf, 0, len);
        }

        private void begin() throws SQLException {
            Statement stmt = null;
            try {
                stmt = wrapper.createStatement();
                stmt.executeUpdate("start transaction");
            } finally {
                closeQuietly(stmt);
            }
        }

        private void flush() {
            try {
                ci.writeToCopy(EOF_BYTES, 0, EOF_BYTES.length);
                ci.endCopy();
            } catch (SQLException e) {
                throw new PostgresCopyException("Exception on flushing copy for partition: [" + part + "]", e);
            }
        }

        private void commit() {
            Statement stmt = null;
            try {
                stmt = wrapper.createStatement();
                stmt.executeUpdate("commit");
            } catch (SQLException e) {
                throw new PostgresCopyException("Exception on committing copy for partition: [" + part + "]", e);
            } finally {
                closeQuietly(stmt);
            }
        }

        private void close() {
            closeQuietly(wrapper);
        }

        private void rollback() {
            Statement stmt = null;
            try {
                if (ci.isActive()) ci.cancelCopy();
                stmt = wrapper.createStatement();
                stmt.executeUpdate("rollback");
                logger.warn("Copy rolled back, session: [" + this + "]");
            } catch (Exception e1) {
                logger.warn("Error rolling back copy, session: [" + this + "]", e1);
            } finally {
                closeQuietly(stmt);
                closeQuietly(wrapper);
            }
        }

        private static PGConnection unwrap(Connection wrapper) throws SQLException {
            if (wrapper instanceof PGConnection) return (PGConnection) wrapper;
            return wrapper.unwrap(PGConnection.class);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                    append("wrapper", wrapper).
                    append("cm", cm).
                    append("ci", ci).
                    append("sql", sql).
                    toString();
        }
    }
}
