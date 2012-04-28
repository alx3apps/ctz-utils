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
 * User: alexey
 * Date: 4/27/12
 */
public class TmpFileJdbcBlobTool extends AbstractJdbcBlobTool {
    private final boolean useLongForBlobsLength;

    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
        this.useLongForBlobsLength = true;
    }

    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
        this.useLongForBlobsLength = true;
    }

    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor, boolean useLongForBlobsLength, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
        this.useLongForBlobsLength = useLongForBlobsLength;
    }

    // temp file is already compressed, return its contents unwrapped
    @Override
    public WritableBlob create() {
        try {
            Pair<Long, OutputStream> pair = createInternal();
            return new WritableBlob(pair.getFirst(), pair.getSecond());
        } catch (Exception e) {
            throw new BlobException(e, "Cannot create blob");
        }
    }

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
