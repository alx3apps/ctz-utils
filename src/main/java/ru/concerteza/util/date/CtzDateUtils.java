package ru.concerteza.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Calendar.*;

/**
 * Date utilities
 *
 * @author alexey
 * Date: 10/31/11
 * @see CtzDateUtilsTest
 */
public class CtzDateUtils {

    /**
     * Converts {@link Calendar} to {@link LocalDateTime}
     *
     * @param cal calendar to convert
     * @return LocalDateTime instance
     */
    public static LocalDateTime toLocalDateTime(Calendar cal) {
        checkNotNull(cal);
        return LocalDateTime.of(
                cal.get(YEAR),
                cal.get(MONTH) + 1,
                cal.get(DAY_OF_MONTH),
                cal.get(HOUR_OF_DAY),
                cal.get(MINUTE),
                cal.get(SECOND),
                cal.get(MILLISECOND) * 1_000_000);
    }

    /**
     * Set {@link Calendar} fields from {@link LocalDateTime}.
     *
     * @param cal to set fields to
     * @param ldt to get fields from
     * @return {@code cal}
     */
    public static Calendar setCalendar(Calendar cal, LocalDateTime ldt) {
        checkNotNull(cal);
        checkNotNull(ldt);
        cal.set(ldt.getYear(),
                ldt.getMonthValue() - 1,
                ldt.getDayOfMonth(),
                ldt.getHour(),
                ldt.getMinute(),
                ldt.getSecond());
        cal.set(MILLISECOND, ldt.getNano() / 1_000_000);
        return cal;
    }

    /**
     * Converts {@link LocalDateTime} to {@link Calendar}.
     *
     * @param ldt LocalDateTime to convert
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDateTime ldt) {
        checkNotNull(ldt);
        GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
        return setCalendar(cal, ldt);
    }

    /**
     * Converts {@link LocalDateTime} to {@link Calendar} at given {@link TimeZone}.
     *
     * @param ldt LocalDateTime to convert
     * @param timeZone target time zone
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDateTime ldt, TimeZone timeZone) {
        checkNotNull(ldt);
        Calendar cal = Calendar.getInstance(timeZone);
        return setCalendar(cal, ldt);
    }

    /**
     * Converts {@link Date} to {@link LocalDateTime}.
     *
     * @param date date to
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        checkNotNull(date);
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTime(date);
        return toLocalDateTime(cal);
    }

    /**
     * Converts {@link Date} at given {@link TimeZone} to {@link LocalDateTime}
     *
     * @param date date to convert
     * @param timeZone date time zone
     * @return LocalDateTime instance
     */
    public static LocalDateTime toLocalDateTime(Date date, TimeZone timeZone) {
        checkNotNull(date);
        checkNotNull(timeZone);
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        return toLocalDateTime(cal);
    }

    /**
     * Converts {@link LocalDateTime} to {@link Date}.
     *
     * @param ldt LocalDateTime to convert
     * @return Date instance
     */
    public static Date toDate(LocalDateTime ldt) {
        return toCalendar(ldt).getTime();
    }

    /**
     * Converts {@link LocalDateTime} to {@link Date} at given {@link TimeZone}.
     *
     * @param ldt LocalDateTime to convert
     * @param timeZone target time zone
     * @return Date instance
     */
    public static Date toDate(LocalDateTime ldt, TimeZone timeZone) {
        return toCalendar(ldt, timeZone).getTime();
    }

    /**
     * Converts milliseconds to {@link LocalDateTime}
     *
     * @param millis milliseconds
     * @return LocalDateTime instance
     */
    public static LocalDateTime toLocalDateTime(long millis) {
        Date date = new Date(millis);
        return toLocalDateTime(date);
    }

    /**
     * Converts milliseconds at given {@link TimeZone} to {@link LocalDateTime}
     *
     * @param millis milliseconds
     * @param timeZone milliseconds time zone
     * @return LocalDateTime instance
     */
    public static LocalDateTime toLocalDateTime(long millis, TimeZone timeZone) {
        Date date = new Date(millis);
        return toLocalDateTime(date, timeZone);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDateTime</tt> object.
     *
     * @param ldt to count milliseconds
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date time.
     */
    public static long toMillis(LocalDateTime ldt) {
        return toDate(ldt).getTime();
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDateTime</tt> object at given {@link TimeZone}.
     *
     * @param ldt to count milliseconds
     * @param timeZone target time zone
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date time.
     */
    public static long toMillis(LocalDateTime ldt, TimeZone timeZone) {
        return toDate(ldt, timeZone).getTime();
    }

    /**
     * Converts {@link Calendar} to {@link LocalDate}
     *
     * @param cal calendar to convert
     * @return LocalDateTime instance
     */
    public static LocalDate toLocalDate(Calendar cal) {
        checkNotNull(cal);
        return LocalDate.of(
                cal.get(YEAR),
                cal.get(MONTH) + 1,
                cal.get(DAY_OF_MONTH));
    }

    /**
     * Set {@link Calendar} fields from {@link LocalDate}.
     *
     * @param cal to set fields to
     * @param ld to get fields from
     * @return {@code cal}
     */
    public static Calendar setCalendar(Calendar cal, LocalDate ld) {
        checkNotNull(cal);
        checkNotNull(ld);
        cal.set(ld.getYear(),
                ld.getMonthValue() - 1,
                ld.getDayOfMonth());
        return cal;
    }

    /**
     * Converts {@link LocalDate} to {@link Calendar}.
     *
     * @param ld LocalDate to convert
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDate ld) {
        checkNotNull(ld);
        GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
        return setCalendar(cal, ld);
    }

    /**
     * Converts {@link LocalDate} to {@link Calendar} at given {@link TimeZone}.
     *
     * @param ld LocalDate to convert
     * @param timeZone target time zone
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDate ld, TimeZone timeZone) {
        checkNotNull(ld);
        Calendar cal = Calendar.getInstance(timeZone);
        return setCalendar(cal, ld);
    }

    /**
     * Converts {@link Date} to {@link LocalDate}
     *
     * @param date date to convert
     * @return LocalDate instance
     */
    public static LocalDate toLocalDate(Date date) {
        checkNotNull(date);
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTime(date);
        return toLocalDate(cal);
    }

    /**
     * Converts {@link Date} at given {@link TimeZone} to {@link LocalDate}
     *
     * @param date date to convert
     * @param timeZone date time zone
     * @return LocalDate instance
     */
    public static LocalDate toLocalDate(Date date, TimeZone timeZone) {
        checkNotNull(date);
        checkNotNull(timeZone);
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        return toLocalDate(cal);
    }

    /**
     * Converts {@link LocalDate} to {@link Date}.
     *
     * @param ld LocalDate to convert
     * @return Date instance
     */
    public static Date toDate(LocalDate ld) {
        return toCalendar(ld).getTime();
    }

    /**
     * Converts {@link LocalDate} to {@link Date} at given {@link TimeZone}.
     *
     * @param ld LocalDate to convert
     * @param timeZone target time zone
     * @return Date instance
     */
    public static Date toDate(LocalDate ld, TimeZone timeZone) {
        return toCalendar(ld, timeZone).getTime();
    }

    /**
     * Converts milliseconds to {@link LocalDate}
     *
     * @param millis milliseconds
     * @return LocalDate instance
     */
    public static LocalDate toLocalDate(long millis) {
        Date date = new Date(millis);
        return toLocalDate(date);
    }

    /**
     * Converts milliseconds at given {@link TimeZone} to {@link LocalDate}
     *
     * @param millis milliseconds
     * @param timeZone milliseconds time zone
     * @return LocalDate instance
     */
    public static LocalDate toLocalDate(long millis, TimeZone timeZone) {
        Date date = new Date(millis);
        return toLocalDate(date, timeZone);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDate</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date.
     */
    public static long toMillis(LocalDate ld) {
        return toDate(ld).getTime();
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDate</tt> object at given {@link TimeZone}.
     *
     * @param timeZone target time zone
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date.
     */
    public static long toMillis(LocalDate ld, TimeZone timeZone) {
        return toDate(ld, timeZone).getTime();
    }
}
