package ru.concerteza.util.db.springjdbc;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import ru.concerteza.util.collection.IgnoreNullImmutableMapBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class ColumnToMapMapper extends AbstractResultSetMapper<Map<String, Object>> {
    private final ColumnMapRowMapper mapper = new ColumnMapRowMapper();

    @Override
    protected Map<String, Object> doApply(ResultSet input) throws SQLException {
        ImmutableMap.Builder<String, Object> builder = new IgnoreNullImmutableMapBuilder<String, Object>();
        builder.putAll(mapper.mapRow(input, 0));
        return builder.build();
    }
}
