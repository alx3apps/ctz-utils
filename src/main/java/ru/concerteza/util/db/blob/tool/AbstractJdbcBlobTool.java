package ru.concerteza.util.db.blob.tool;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.concerteza.util.db.blob.compress.Compressor;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * Basic implementation of Postgre-flavoured BLOB operations for other databases using standard JDBC API.
 * Implementations may be true server-side (Oracle) or client-side with temp file.
 * To simulate Potgre BLOBs {@code blob_storage_id_seq} sequence and
 * {@code blob_storage(id bigint primary key, data blob)} must be created beforehand.
 *
 * @author alexey
 * Date: 4/13/12
 * @see BlobTool
 */
@Deprecated // use com.alexkasko.springjdbc.blob
public abstract class AbstractJdbcBlobTool extends AbstractBlobTool {
    protected final NamedParameterJdbcTemplate jt;
    protected final String generateIdSQL;
    protected final String insertSQL;
    protected final String loadSQL;
    protected final String deleteSQL;

    /**
     * Shortcut constructor with default SQL
     *
     * @param dataSource data source
     * @param compressor compressor
     */
    public AbstractJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        this(dataSource, compressor,
                "select nextval('blob_storage_id_seq')",
                "insert into blob_storage(id, data) values(:id, :data)",
                "select data from blob_storage where id = :id",
                "delete from blob_storage where id = :id");
    }

    /**
     * @param dataSource data source
     * @param compressor compressor
     * @param generateIdSQL SQL to generate BLOB ID, default: {@code select nextval('blob_storage_id_seq')}
     * @param insertSQL SQL to insert blob into database, default: {@code insert into blob_storage(id, data) values(:id, :data)}
     * @param loadSQL SQL to load blob data from database, default: {@code select data from blob_storage where id = :id}
     * @param deleteSQL SQL to delete BLOB from database, default: {@code delete from blob_storage where id = :id}
     */
    public AbstractJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor);
        this.jt = new NamedParameterJdbcTemplate(dataSource);
        this.generateIdSQL = generateIdSQL;
        this.insertSQL = insertSQL;
        this.loadSQL = loadSQL;
        this.deleteSQL = deleteSQL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream loadInternal(long id) throws SQLException {
        Blob created = jt.queryForObject(loadSQL, ImmutableMap.of("id", id), Blob.class);
        checkState(null != created, "No blob found for id: %s", id);
        return created.getBinaryStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteInternal(long id) {
        int count = jt.update(deleteSQL, ImmutableMap.of("id", id));
        checkState(1 == count, "One row must be deleted, but was: %s", count);
    }
}
