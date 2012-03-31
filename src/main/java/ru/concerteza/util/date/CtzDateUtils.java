package ru.concerteza.util.date;

import org.joda.time.LocalDateTime;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/31/11
 */
public class CtzDateUtils {
    public static final LocalDateTime DEFAULT_DATE = new LocalDateTime(0, 1, 1, 0, 0);

    public static LocalDateTime toLocalDateTime(Date date) {
        checkNotNull(date);
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTime(date);
        return toLocalDateTime(cal);
    }

    public static LocalDateTime toLocalDateTime(Calendar cal) {
        checkNotNull(cal);
        return new LocalDateTime(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));
    }

    public static LocalDateTime defaultDate(@Nullable Date date) {
        return defaultDate(date, DEFAULT_DATE);
    }

    public static LocalDateTime defaultDate(@Nullable Date date, LocalDateTime defaultDate) {
        return null == date ? defaultDate : toLocalDateTime(date);
    }
}
