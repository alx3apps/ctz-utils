package ru.concerteza.util.db.blob.tool;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

import static com.google.common.base.Preconditions.checkState;

/**
* User: alexey
* Date: 4/13/12
*/


// create sequence blob_storage_id_seq;
// create table blob_storage (id bigint primary key, data blob);
public abstract class AbstractJdbcBlobTool extends AbstractBlobTool {
    protected final NamedParameterJdbcTemplate jt;
    protected final String generateIdSQL;
    protected final String insertSQL;
    protected final String loadSQL;
    protected final String deleteSQL;

    public AbstractJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        this(dataSource, compressor,
                "select nextval('blob_storage_id_seq')",
                "insert into blob_storage(id, data) values(:id, :data)",
                "select data from blob_storage where id = :id",
                "delete from blob_storage where id = :id");
    }

    public AbstractJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor);
        this.jt = new NamedParameterJdbcTemplate(dataSource);
        this.generateIdSQL = generateIdSQL;
        this.insertSQL = insertSQL;
        this.loadSQL = loadSQL;
        this.deleteSQL = deleteSQL;
    }

    @Override
    protected InputStream loadInternal(long id) throws SQLException {
        Blob created = jt.queryForObject(loadSQL, ImmutableMap.of("id", id), Blob.class);
        checkState(null != created, "No blob found for id: %s", id);
        return created.getBinaryStream();
    }

    @Override
    public void deleteInternal(long id) {
        int count = jt.update(deleteSQL, ImmutableMap.of("id", id));
        checkState(1 == count, "One row must be deleted, but was: %s", count);
    }
}
