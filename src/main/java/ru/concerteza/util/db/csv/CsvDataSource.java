package ru.concerteza.util.db.csv;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.springframework.core.io.Resource;
import ru.concerteza.util.db.jdbcstub.AbstractDataSource;
import ru.concerteza.util.io.CtzResourceUtils;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static ru.concerteza.util.CtzConstants.UTF8;

/**
 * {@link javax.sql.DataSource} implementation for CSV file with fixed data
 * 
 * @author  alexey
 * Date: 6/29/12
 */
public class CsvDataSource extends AbstractDataSource {
    private static final String NULL_VALUE = "NULL";
    private final CsvMapIterable iterable;

    /**
     * Shortcut constructor
     *
     * @param resourcePath spring resource path to CSV file
     * @param delimiter CSV fields delimiter
     */
    public CsvDataSource(String resourcePath, String delimiter) {
        this(resourcePath, delimiter, UTF8);
    }

    /**
     * Shortcut constructor
     *
     * @param resourcePath spring resource path to CSV file
     * @param delimiter CSV fields delimiter
     * @param encoding CSV file encoding
     */
    public CsvDataSource(String resourcePath, String delimiter, String encoding) {
        this(CtzResourceUtils.RESOURCE_LOADER.getResource(resourcePath), delimiter, encoding);
    }

    /**
     * Main constructor
     * 
     * @param resource CSV file as spring resource
     * @param delimiter CSV fields delimiter
     * @param encoding CSV file encoding
     */
    @SuppressWarnings("unchecked")
    public CsvDataSource(Resource resource, String delimiter, String encoding) {
        this.iterable = new CsvMapIterable(resource, delimiter, encoding, SubstituteNullsFunction.INSTANCE);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Connection getConnection() throws SQLException {
        return new CsvConnection(iterable);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    private enum SubstituteNullsFunction implements Function<Map<String, String>, Map<String, String>> {
        INSTANCE;
        @Override
        public Map<String, String> apply(@Nullable Map<String, String> input) {
            Map<String, String> res = Maps.newLinkedHashMap();
            for(Map.Entry<String, String> en : input.entrySet()) {
                String val = NULL_VALUE.equals(en.getValue()) ? null : en.getValue();
                res.put(en.getKey(), val);
            }
            return res;
        }
    }
}
