package ru.concerteza.util.db.blob.tool;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.concerteza.util.db.blob.BlobException;
import ru.concerteza.util.db.blob.WritableBlob;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.io.TempFileOutputInputStream;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Client-side implementation of BLOB tool for databases, that don't support
 * {@code java.io.OutputStream java.sql.Blob#setBinaryStream(long pos)} method properly
 * (implement it on client in-memory or in similar fashion with temp file).
 * This implementation creates BLOB as compressed temp file and transfer data from it after BLOB closed by client.
 * Data is inserted from file into database using JDBC's streaming
 * {@code void java.sql.PreparedStatement#setBinaryStream(int parameterIndex, java.io.InputStream x, long length)}
 * method. So file data never goes fully into memory outside of JDBC driver,
 * but may go into memory inside some creepy JDBC implementation.
 * Not well suited for highload applications, may be used with H2database to simulate PostgreSQL-like BLOBs in tests.
 *
 * @author alexey
 * Date: 4/27/12
 * @see PostgreBlobTool
 */
@Deprecated // use com.alexkasko.springjdbc.blob
public class TmpFileJdbcBlobTool extends AbstractJdbcBlobTool {
    private final boolean useLongForBlobsLength;

    /**
     * @param dataSource data source
     * @param compressor compressor
     */
    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
        this.useLongForBlobsLength = true;
    }

    /**
     * @param dataSource data source
     * @param compressor compressor
     * @param generateIdSQL SQL to generate BLOB ID, default: {@code select nextval('blob_storage_id_seq')}
     * @param insertSQL SQL to insert blob into database, default: {@code insert into blob_storage(id, data) values(:id, :data)}
     * @param loadSQL SQL to load blob data from database, default: {@code select data from blob_storage where id = :id}
     * @param deleteSQL SQL to delete BLOB from database, default: {@code delete from blob_storage where id = :id}
     */
    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
        this.useLongForBlobsLength = true;
    }

    /**
     * Use this method for JTDS MSSQL JDBC driver that doesn't support Long size for BLOBs
     *
     * @param dataSource data source
     * @param compressor compressor
     * @param useLongForBlobsLength whether to use Long to provide BLOB size to JDBC driver
     * @param generateIdSQL SQL to generate BLOB ID, default: {@code select nextval('blob_storage_id_seq')}
     * @param insertSQL SQL to insert blob into database, default: {@code insert into blob_storage(id, data) values(:id, :data)}
     * @param loadSQL SQL to load blob data from database, default: {@code select data from blob_storage where id = :id}
     * @param deleteSQL SQL to delete BLOB from database, default: {@code delete from blob_storage where id = :id}
     */
    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor, boolean useLongForBlobsLength, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
        this.useLongForBlobsLength = useLongForBlobsLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WritableBlob create() {
        // temp file is already compressed, return its contents unwrapped
        try {
            Pair<Long, OutputStream> pair = createInternal();
            return new WritableBlob(pair.getFirst(), pair.getSecond());
        } catch (Exception e) {
            throw new BlobException(e, "Cannot create blob");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Pair<Long, OutputStream> createInternal() throws Exception {
        long id = jt.getJdbcOperations().queryForLong(generateIdSQL);
        OutputStream os = new TempFileOutputInputStream(new InsertDataFun(id), compressor);
        return new Pair<Long, OutputStream>(id, os);
    }

    private class InsertDataFun implements Function<TempFileOutputInputStream.TempFile, Void> {
        private final long id;

        private InsertDataFun(long id) {
            this.id = id;
        }

        @Override
        public Void apply(TempFileOutputInputStream.TempFile input) {
            jt.getJdbcOperations().update(new InsertPS(id, input.getCompressed(), input.getCompressedLength()));
            return null;
        }

        private class InsertPS implements PreparedStatementCreator {
            private final long id;
            private final InputStream data;
            private final long length;

            private InsertPS(long id, InputStream data, long length) {
                this.id = id;
                this.data = data;
                this.length = length;
            }

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                // substitute named params, see NamedParameterJdbcTemplate#getPreparedStatementCreator(String sql, SqlParameterSource paramSource)
                ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(insertSQL);
                Map<String, Object> paramsMap = ImmutableMap.of("id", id, "data", data);
                SqlParameterSource paramSource = new MapSqlParameterSource(paramsMap);
                String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
		        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
                PreparedStatement stmt = con.prepareStatement(sqlToUse);
                stmt.setLong(1, (Long) params[0]);
                if(useLongForBlobsLength) {
                    stmt.setBinaryStream(2, (InputStream) params[1], length);
                } else { //JTDS doesn't support long sized blobs
                    stmt.setBinaryStream(2, (InputStream) params[1], (int) length);
                }
                return stmt;
            }
        }
    }
}
