package ru.concerteza.util.db.blob.tool;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.PreparedStatementCreator;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.io.TempFileOutputInputStream;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 4/27/12
 */
public class TmpFileJdbcBlobTool extends AbstractJdbcBlobTool {
    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
    }

    public TmpFileJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
    }

    @Override
    protected Pair<Long, OutputStream> createInternal() throws Exception {
        long id = jt.getJdbcOperations().queryForLong(generateIdSQL);
        OutputStream os = new TempFileOutputInputStream(new InsertDataFun(id));
        return new Pair<Long, OutputStream>(id, os);
    }

    private class InsertDataFun implements Function<InputStream, Void> {
        private final long id;

        private InsertDataFun(long id) {
            this.id = id;
        }

        @Override
        public Void apply(InputStream input) {
            jt.update(insertSQL, ImmutableMap.of("id", id, "data", input));
            return null;
        }
    }

//    private class InsertPS implements PreparedStatementCreator {
//        private final long id;
//        private final InputStream data;
//
//        private InsertPS(long id, InputStream data) {
//            this.id = id;
//            this.data = data;
//        }
//
//        @Override
//        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//            PreparedStatement stmt = con.prepareStatement(insertSQL);
//            stmt.setLong(1, id);
//            stmt.setBinaryStream(2, data);
//            return stmt;
//        }
//    }
}
