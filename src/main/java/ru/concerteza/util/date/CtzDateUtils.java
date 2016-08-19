package ru.concerteza.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.google.common.base.Preconditions.checkNotNull;

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
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND) * 1_000_000);
    }

    /**
     * Converts {@link LocalDateTime} to {@link Calendar}.
     *
     * @param ldt LocalDateTime to convert
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDateTime ldt) {
        checkNotNull(ldt);
        GregorianCalendar cal = new GregorianCalendar(
                ldt.getYear(),
                ldt.getMonthValue() - 1,
                ldt.getDayOfMonth(),
                ldt.getHour(),
                ldt.getMinute(),
                ldt.getSecond());
        cal.set(Calendar.MILLISECOND, ldt.getNano() / 1_000_000);
        return cal;
    }

    /**
     * Converts {@link Date} to {@link LocalDateTime}
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
     * Converts {@link LocalDateTime} to {@link Date}.
     *
     * @param ldt LocalDateTime to convert
     * @return Date instance
     */
    public static Date toDate(LocalDateTime ldt) {
        return toCalendar(ldt).getTime();
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
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDateTime</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date time.
     */
    public static long toLong(LocalDateTime ldt) {
        return toDate(ldt).getTime();
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
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Converts {@link LocalDate} to {@link Calendar}.
     *
     * @param ld LocalDate to convert
     * @return Calendar instance
     */
    public static Calendar toCalendar(LocalDate ld) {
        checkNotNull(ld);
        GregorianCalendar cal = new GregorianCalendar(
                ld.getYear(),
                ld.getMonthValue() - 1,
                ld.getDayOfMonth());
        return cal;
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
     * Converts {@link LocalDate} to {@link Date}.
     *
     * @param ld LocalDate to convert
     * @return Date instance
     */
    public static Date toDate(LocalDate ld) {
        return toCalendar(ld).getTime();
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
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>LocalDate</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this local date.
     */
    public static long toLong(LocalDate ld) {
        return toDate(ld).getTime();
    }
}
