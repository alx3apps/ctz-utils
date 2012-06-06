package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import java.util.Collection;

/**
 * {@link ru.concerteza.util.db.springjdbc.entitymapper.Filter} implementation to convert {@link String} columns to
 * value of <code>enum</code> type provided to constructor.
 *
 * @author Timofey Gorshkov
 * created 24.05.2012
 * @since  2.5.1
 * @see ColumnListFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class EnumFilter<E extends Enum<E>> extends ColumnListFilter<E> {

    private Class<E> en;

    /**
     * @param en <code>enum</code> type, to which columns should be converted
     * @param columns columns to apply this filter to
     */
    public EnumFilter(Class<E> en, String... columns) {
        super(columns);
        this.en = en;
    }

    /**
     * @param en <code>enum</code> type, to which columns should be converted
     * @param columns columns to apply this filter to
     */
    public EnumFilter(Class<E> en, Collection<String> columns) {
        super(columns);
        this.en = en;
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value input column value
     * @return output column value
     */
    @Override
    protected E filter(String colname, Object value) {
        return Enum.valueOf(en, (String)value);
    }
}
