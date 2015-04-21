package ru.concerteza.util.date.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.EnhancedUserType;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;

/**
 * Persist org.joda.time.LocalDate via hibernate,
 * date -> ld conversion changed
 *
 * @author Mario Ivankovits (mario@ops.co.at)
 * @author Stephen Colebourne
 * @author alexey,
 * Date: 11/1/11
 */
public class PersistentLocalDate implements EnhancedUserType, Serializable {

    public static final PersistentLocalDate INSTANCE = new PersistentLocalDate();
    // to be inlined
    public static final String LOCAL_DATE_TYPE = "ru.concerteza.util.date.hibernate.PersistentLocalDate";

    private static final int[] SQL_TYPES = new int[] { Types.DATE, };

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
        return LocalDate.class;
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
        LocalDate dtx = (LocalDate) x;
        LocalDate dty = (LocalDate) y;
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
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(rs, names[0], session);

    }

    public Object nullSafeGet(ResultSet resultSet, String string, SessionImplementor session) throws SQLException {
        Object timestamp = StandardBasicTypes.DATE.nullSafeGet(resultSet, string, session);
        if (timestamp == null) {
            return null;
        }
        return toLocalDateTime((Date) timestamp).toLocalDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            StandardBasicTypes.DATE.nullSafeSet(st, null, index, session);
        } else {
            StandardBasicTypes.DATE.nullSafeSet(st, ((LocalDate) value).toDate(), index, session);
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
        return new LocalDate(string);
    }

}
