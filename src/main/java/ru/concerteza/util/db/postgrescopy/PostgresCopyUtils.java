package ru.concerteza.util.db.postgrescopy;

import org.postgresql.PGConnection;
import org.postgresql.copy.PGCopyInputStream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.InputStream;
import java.sql.Connection;

/**
 * User: alexkasko
 * Date: 1/4/15
 */
public class PostgresCopyUtils {

    public static PGConnection unwrap(Connection wrapper) {
        try {
            if(wrapper instanceof PGConnection) return (PGConnection) wrapper;
            return wrapper.unwrap(PGConnection.class);
        } catch (Exception e) {
            throw new PostgresCopyException("Unwrap error for connection: [" + wrapper + "]", e);
        }
    }

    public static InputStream openCopyStream(NamedParameterJdbcTemplate npjt, String sql) {
        try {
            JdbcTemplate jt = (JdbcTemplate) npjt.getJdbcOperations();
            return openCopyStream(jt.getDataSource().getConnection(), sql);
        } catch (Exception e) {
            throw new PostgresCopyException("Get connection error for jt: [" + npjt + "]", e);
        }
    }

    public static InputStream openCopyStream(Connection conn, String sql) {
        try {
            PGConnection pc = unwrap(conn);
            return new PGCopyInputStream(pc, sql);
        } catch (Exception e) {
            throw new PostgresCopyException("Copy stream open error, connection: [" + conn + "], sql: [" + sql + "]", e);
        }
    }
}
