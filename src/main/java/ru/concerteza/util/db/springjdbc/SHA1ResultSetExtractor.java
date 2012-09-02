package ru.concerteza.util.db.springjdbc;

import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.concerteza.util.io.RuntimeIOException;
import ru.concerteza.util.io.SHA1InputStream;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;

/**
 * Result set extractor implementation that computes SHA1 hash sum from all rows
 *
 * @author alexey
 * Date: 8/31/12
 */
public class SHA1ResultSetExtractor implements ResultSetExtractor<String> {
    public static final SHA1ResultSetExtractor SHA1_EXTRACTOR = new SHA1ResultSetExtractor();

    @Override
    public String extractData(ResultSet rs) throws SQLException, DataAccessException {
        InputStream is = null;
        try {
            is = InputStreamOverResultSet.of(rs);
            SHA1InputStream sha1 = new SHA1InputStream(is);
            IOUtils.copyLarge(sha1, NULL_OUTPUT_STREAM);
            return sha1.digest();
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
