package ru.concerteza.util.db.springjdbc;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;

import static java.sql.Types.*;

/**
 * JDBC utilities
 *
 * @author alexey
 * Date: 8/30/12
 */
public class CtzJdbcUtils {
//    http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
//    8.9.1 JDBC Types Mapped to Java Types
    /**
     * Mapping from JDBC types to Java types, clobs and other complex types are omitted.
     */
    public static final Map<Integer, Class<?>> SQL_TYPES_MAP = ImmutableMap.<Integer, Class<?>>builder()
            .put(CHAR, String.class)
            .put(VARCHAR, String.class)
            .put(LONGVARCHAR, String.class)
            .put(NCHAR, String.class)
            .put(NVARCHAR, String.class)
            .put(LONGNVARCHAR, String.class)
            .put(NUMERIC, BigDecimal.class)
            .put(DECIMAL, BigDecimal.class)
            .put(BIT, boolean.class)
            .put(BOOLEAN, boolean.class)
            .put(TINYINT, byte.class)
            .put(SMALLINT, short.class)
            .put(INTEGER, int.class)
            .put(BIGINT, long.class)
            .put(REAL, float.class)
            .put(FLOAT, double.class)
            .put(DOUBLE, double.class)
            .put(BINARY, byte[].class)
            .put(VARBINARY, byte[].class)
            .put(LONGVARBINARY, byte[].class)
            .put(ROWID, byte[].class)
            .put(DATE, java.sql.Date.class)
            .put(TIME, Time.class)
            .put(TIMESTAMP, Timestamp.class)
            .build();

    /**
     * Finds appropriate Java class for JDBC type
     *
     * @param type JDBC type
     * @param <T> returned class parameter
     * @return mapped class from {@link #SQL_TYPES_MAP}, {@code Object.class} if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> javaClassFromSqlType(int type) {
        Class<?> mapped = SQL_TYPES_MAP.get(type);
        if(null != mapped) return (Class<T>) mapped;
        else return (Class<T>) Object.class;
    }


}
