package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.joda.time.LocalDateTime;
import ru.concerteza.util.date.CtzDateUtils;
import ru.concerteza.util.except.IllegalArgumentTypeException;
import ru.concerteza.util.number.CtzNumberUtils;

/**
 * Static utility methods pertaining to {@link EntityFilter} instances.
 *
 * @author Timofey Gorshkov
 * created 17.04.2013
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class EntityFilters {
    private EntityFilters() {}

    /**
     * Returns an entity filter that switch all column names to lower case.
     * May be useful for <code>select * ...</code> requests for some RDBMS.
     */
    public static EntityFilter columnsToLower() {
        return SimpleFilter.COLUMNS_TO_LOWER;
    }

    /**
     * Returns an entity filter that does nothing.
     */
    public static EntityFilter identity() {
        return SimpleFilter.IDENTITY;
    }

    /**
     * Returns an entity filter that convert {@link String} and {@link Number}
     * (supported by {@link ru.concerteza.util.number.CtzNumberUtils#intValueOf(Number)}) columns to {@link Integer}.
     * <p>
     * Initially it was created for cases where {@link java.sql.ResultSet} may be provided by
     * {@link ru.concerteza.util.db.ResultSetOverCSV}.
     *
     * @param columns to apply this filter to.
     * @see ColumnListFilter
     */
    public static EntityFilter toInteger(String... columns) {
        return new GeneralColumnListFilter(FilterLogic.INTEGER, columns);
    }

    /**
     * Returns an entity filter that convert {@link String} and {@link Number} columns to {@link Double}.
     *
     * @param columns to apply this filter to.
     * @see ColumnListFilter
     */
    public static EntityFilter toDouble(String... columns) {
        return new GeneralColumnListFilter(FilterLogic.DOUBLE, columns);
    }

    /**
     * Returns an entity filter that convert {@link Date} columns to {@link LocalDateTime}.
     *
     * @param columns to apply this filter to.
     * @see ColumnListFilter
     * @see ru.concerteza.util.date.CtzDateUtils#toLocalDateTime(Date)
     */
    public static EntityFilter toLocalDateTime(String... columns) {
        return new GeneralColumnListFilter(FilterLogic.LOCAL_DATE_TIME, columns);
    }

    /**
     * Returns an entity filter that convert {@link String}, {@link Date} and {@link Timestamp} columns
     * to {@link Timestamp}.
     *
     * @param columns to apply this filter to.
     * @see ColumnListFilter
     */
    public static EntityFilter toTimestamp(String... columns) {
        return new GeneralColumnListFilter(FilterLogic.TIMESTAMP, columns);
    }

    /**
     * Returns an entity filter that convert {@link String} column to value of provided <code>enum</code> type.
     *
     * @param column to apply this filter to.
     * @param en enum class.
     */
    public static EntityFilter toEnum(String column, Class<? extends Enum> en) {
        return new EnumFilter(ImmutableMap.<String, Class<? extends Enum>>of(column, en));
    }

    /**
     * Returns an entity filter that convert {@link String} columns to values of corresponded <code>enum</code> type.
     */
    public static EntityFilter toEnum(String column1, Class<? extends Enum> en1,
                                      String column2, Class<? extends Enum> en2) {
        return new EnumFilter(ImmutableMap.<String, Class<? extends Enum>>of(column1, en1, column2, en2));
    }

    /**
     * Returns an entity filter that convert {@link String} columns to values of corresponded <code>enum</code> type.
     *
     * @param columnMap of enum classes to which corresponding columns should be converted.
     */
    public static EntityFilter toEnum(Map<String, Class<? extends Enum>> columnMap) {
        return new EnumFilter(columnMap);
    }

    /**
     * Returns an entity filter that parses {@link String} JSON data column to object.
     *
     * @param gson {@link Gson} object to be used.
     * @param column with JSON data.
     * @param clazz to parse to.
     */
    public static EntityFilter fromJson(Gson gson, String column, Class<?> clazz) {
        return new JsonFilter(gson, ImmutableMap.<String, Class<?>>of(column, clazz));
    }

    /**
     * Returns an entity filter that parses {@link String} JSON data columns to objects.
     *
     * @param gson {@link Gson} object to be used.
     * @param columnMap of classes to parse to to corresponding JSON data columns.
     */
    public static EntityFilter fromJson(Gson gson, Map<String, Class<?>> columnMap) {
        return new JsonFilter(gson, columnMap);
    }

    private static enum SimpleFilter implements EntityFilter {
        COLUMNS_TO_LOWER { @Override public Map<String, ?> apply(Map<String, ?> data) {
            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
            for(Map.Entry<String, ?> en : data.entrySet()) {
                builder.put(en.getKey().toLowerCase(Locale.ENGLISH), en.getValue());
            }
            return builder.build();
        }},
        IDENTITY { @Override public Map<String, ?> apply(Map<String, ?> data) {
            return data;
        }};
    }

    private static class GeneralColumnListFilter extends ColumnListFilter {
        private final FilterLogic logic;
        public GeneralColumnListFilter(FilterLogic logic, String... columns) {
            super(columns);
            this.logic = logic;
        }
        @Override protected Object filterColumn(String colname, Object value) {
            return logic.filterColumn(colname, value);
        }
    }

    private static enum FilterLogic {
        INTEGER { @Override protected Object filterColumn(String colname, Object value) {
            if (value instanceof Number) {
                return CtzNumberUtils.intValueOf((Number)value);
            } else if (value instanceof String) {
                return Integer.parseInt((String)value);
            } else {
                throw new IllegalArgumentTypeException(value.getClass());
            }
        }},
        DOUBLE { @Override protected Object filterColumn(String colname, Object value) {
            if (value instanceof Number) {
                return ((Number)value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String)value);
            } else {
                throw new IllegalArgumentTypeException(value.getClass());
            }
        }},
        LOCAL_DATE_TIME { @Override protected Object filterColumn(String colname, Object value) {
            return CtzDateUtils.toLocalDateTime((Date)value);
        }},
        TIMESTAMP { @Override protected Object filterColumn(String colname, Object value) {
            if (Timestamp.class.isAssignableFrom(value.getClass())) {
                return (Timestamp)value;
            } else if (Date.class.isAssignableFrom(value.getClass())) {
                return new Timestamp(((Date)value).getTime());
            } else if (value instanceof String) {
                return Timestamp.valueOf((String)value);
            } else {
                throw new IllegalArgumentTypeException(value.getClass());
            }
        }};
        protected abstract Object filterColumn(String colname, Object value);
    }

    private static class EnumFilter extends ColumnListFilter {
        private final Map<String, Class<? extends Enum>> columnMap;
        public EnumFilter(Map<String, Class<? extends Enum>> columnMap) {
            super(columnMap.keySet());
            this.columnMap = ImmutableMap.copyOf(columnMap);
        }
        @Override protected Object filterColumn(String colname, Object value) {
            Class<? extends Enum> en = columnMap.get(colname);
            return Enum.valueOf(en, (String)value);
        }
    }

    private static class JsonFilter extends ColumnListFilter {
        private final Gson gson;
        private final Map<String, Class<?>> columnMap;
        public JsonFilter(Gson gson, Map<String, Class<?>> columnMap) {
            super(columnMap.keySet());
            this.gson = gson;
            this.columnMap = ImmutableMap.copyOf(columnMap);
        }
        @Override protected Object filterColumn(String colname, Object value) {
            Class<?> clazz = columnMap.get(colname);
            return gson.fromJson((String) value, clazz);
        }
    }
}
