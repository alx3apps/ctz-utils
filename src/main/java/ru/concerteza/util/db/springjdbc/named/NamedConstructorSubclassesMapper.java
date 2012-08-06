package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import ru.concerteza.util.db.springjdbc.RowIterable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Named constructor implementation for class hierarchy mapping. Converts result set row into lower case map,
 * chooses function by discriminator column value and applies it.
 * Columns common for all subclasses, must be always not null in result set, so subclasses must not name
 * their own (not parent) columns to the same names.
 *
 * @author alexey
 * Date: 7/6/12
 * @see NamedConstructorMapper
 * @see NamedConstructorFunction
 * @see NamedConstructor
 */
class NamedConstructorSubclassesMapper<T> extends NamedConstructorMapper<T> {
    private final String discColumn;
    private final Map<String, NamedConstructorFunction<? extends T>> ncMap;
    private final Set<String> commonColumns;

    /**
     * @param ncMap discriminator value -> named constructor function mapping
     * @param discColumn discriminator column
     * @param commonColumns columns common for all subclasses, must be always not null in result set
     */
    NamedConstructorSubclassesMapper(Map<String, NamedConstructorFunction<? extends T>> ncMap, String discColumn,
                                     Set<String> commonColumns) {
        checkArgument(ncMap.size() > 0, "Provided functions map is empty");
        checkArgument(isNotBlank(discColumn), "Provided discriminator column is blank");
        checkArgument(commonColumns.size() > 0, "Provided common (parent) columns collection is empty");
        this.ncMap = ncMap;
        this.discColumn = discColumn.toLowerCase(Locale.ENGLISH);
        this.commonColumns = commonColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        String discVal = null;
        for(RowIterable.Cell cell : RowIterable.of(rs)) {
            if(discColumn.equalsIgnoreCase(cell.getColumnName())) {
                discVal = (String) cell.getValue();
            }
            else if(null == cell.getValue() && commonColumns.contains(cell.getColumnName())) {
                Map<String, ?> logData = new ColumnMapRowMapper().mapRow(rs, rowNum);
                throw new IllegalArgumentException(format(
                        "Null value in common column: '{}', common columns: '{}', row data: '{}', constructors: '{}'",
                        cell.getColumnName(), commonColumns, logData, ncMap));
            }
            else if(null != cell.getValue()) builder.put(cell.getColumnName().toLowerCase(Locale.ENGLISH), cell.getValue());
        }
        if(null == discVal) throw new IllegalArgumentException(format(
                "Null or absent value of disc column: '{}' in row data: '{}'", discColumn, logRS(rs, rowNum)));
        NamedConstructorFunction<? extends T> ef = ncMap.get(discVal);
        checkArgument(null != ef, "Cannot find subclass for discriminator: '%s', keys: '%s', row data: '%s'",
                discVal, ncMap.keySet(), logRS(rs, rowNum));
        return ef.apply(builder.build());
    }
}
