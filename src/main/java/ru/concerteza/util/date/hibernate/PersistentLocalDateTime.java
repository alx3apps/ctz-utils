package ru.concerteza.util.date.hibernate;

import org.hibernate.Hibernate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;

import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;

/**
 * Persist org.joda.time.LocalDateTime via hibernate,
 * date -> ldt conversion changed
 *
 * @author Mario Ivankovits (mario@ops.co.at)
 * @author Stephen Colebourne
 * @author alexey
 */
public class PersistentLocalDateTime implements EnhancedUserType, Serializable {

    public static final PersistentLocalDateTime INSTANCE = new PersistentLocalDateTime();
    // to be inlined
    public static final String LOCAL_DATE_TIME_TYPE = "ru.concerteza.util.date.hibernate.PersistentLocalDateTime";

    private static final int[] SQL_TYPES = new int[]{Types.TIMESTAMP,};

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class returnedClass() {
        return LocalDateTime.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        LocalDateTime dtx = (LocalDateTime) x;
        LocalDateTime dty = (LocalDateTime) y;
        return dtx.equals(dty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, Object object) throws HibernateException, SQLException {
        return nullSafeGet(resultSet, strings[0]);
    }

    public Object nullSafeGet(ResultSet resultSet, String string) throws SQLException {
        Object timestamp = Hibernate.TIMESTAMP.nullSafeGet(resultSet, string);
        if (timestamp == null) {
            return null;
        }
        return toLocalDateTime((Date) timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            Hibernate.TIMESTAMP.nullSafeSet(preparedStatement, null, index);
        } else {
            Hibernate.TIMESTAMP.nullSafeSet(preparedStatement, ((LocalDateTime) value).toDate(), index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object assemble(Serializable cached, Object value) throws HibernateException {
        return cached;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String objectToSQLString(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toXMLString(Object object) {
        return object.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromXMLString(String string) {
        return new LocalDateTime(string);
    }
}