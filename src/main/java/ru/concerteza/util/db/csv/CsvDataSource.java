package ru.concerteza.util.db.csv;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.core.io.Resource;
import ru.concerteza.util.CtzConstants;
import ru.concerteza.util.db.jdbcstub.AbstractDataSource;
import ru.concerteza.util.io.CtzResourceUtils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static ru.concerteza.util.CtzConstants.UTF8;

/**
* User: alexey
* Date: 6/29/12
*/

public class CsvDataSource extends AbstractDataSource {
    private static final String NULL_VALUE = "NULL";
    private final CsvMapIterable iterable;

    public CsvDataSource(String resourcePath, String splitter) {
        this(resourcePath, splitter, UTF8);
    }

    public CsvDataSource(String resourcePath, String splitter, String encoding) {
        this(CtzResourceUtils.RESOURCE_LOADER.getResource(resourcePath), splitter, encoding);
    }

    @SuppressWarnings("unchecked")
    public CsvDataSource(Resource resource, String splitter, String encoding) {
        this.iterable = new CsvMapIterable(resource, splitter, encoding, SubstituteNullsFunction.INSTANCE);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CsvConnection(iterable);
    }

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
