package ru.concerteza.util.db.springjdbc;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.jdbc.core.StatementCreatorUtils.setParameterValue;
import static org.springframework.jdbc.core.namedparam.NamedParameterUtils.*;

/**
 * {@code PreparedStatementCreator} implementation, that places created {@link PreparedStatement}
 * into provided {@link AtomicReference} for possible cancellation from other thread.
 * Other thread must check atomic reference payload for nullability and must.
 *
 * @author alexkasko
 * Date: 11/6/12
 */
// todo testme
public class CancellableStatementCreator implements PreparedStatementCreator {
    private final AtomicReference<Statement> stmtRef;
    private final String sql;
    private final SqlParameterSource params;

    /**
     * Main constructor
     *
     * @param stmtRef statement will be placed here
     * @param sql sql string with spring-jdbc named parameters
     * @param params parameter source
     */
    public CancellableStatementCreator(AtomicReference<Statement> stmtRef, String sql, SqlParameterSource params) {
        this.stmtRef = stmtRef;
        this.sql = sql;
        this.params = params;
    }

    /**
     * Shortcut constructor
     *
     * @param stmtRef statement will be placed here
     * @param sql sql string without parameters
     */
    public CancellableStatementCreator(AtomicReference<Statement> stmtRef, String sql) {
        this(stmtRef, sql, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        final PreparedStatement stmt;
        if(null != params) {
            // substitute named params,
            // see NamedParameterJdbcTemplate#getPreparedStatementCreator(String sql, SqlParameterSource paramSource)
            ParsedSql parsedSql = parseSqlStatement(sql);
            String sqlToUse = substituteNamedParameters(parsedSql, params);
            stmt = con.prepareStatement(sqlToUse);
            Object[] parArray = buildValueArray(parsedSql, params, null);
            List<SqlParameter> parList = buildSqlParameterList(parsedSql, params);
            for(int i = 0; i < parArray.length; i++) {
                setParameterValue(stmt, i + 1, parList.get(i), parArray[i]);
            }
        } else stmt = con.prepareStatement(sql);
        stmtRef.set(stmt);
        return stmt;
    }
}