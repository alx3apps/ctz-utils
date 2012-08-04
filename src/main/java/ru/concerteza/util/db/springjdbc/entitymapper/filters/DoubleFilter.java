package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import java.util.Collection;

import static ru.concerteza.util.string.CtzFormatUtils.format;

// todo: document me
public class DoubleFilter extends ColumnListFilter<Double> {

    /**
     * @param columns columns to apply this filter to
     */
    public DoubleFilter(String... columns) {
        super(columns);
    }

    /**
     * @param columns columns to apply this filter to
     */
    public DoubleFilter(Collection<String> columns) {
        super(columns);
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value input column value
     * @return output column value
     */
    @Override
    protected Double filterColumn(String colname, Object value) {
        if (value instanceof Double) return (Double)value;
        if (value instanceof String) return Double.parseDouble((String)value);
        throw new IllegalArgumentException(format("Illegal argument, column: '{}', class: '{}'", colname, value.getClass().getSimpleName()));
    }
}
