package ru.concerteza.util.date;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Date utilities
 *
 * @author alexey
 * Date: 10/31/11
 * @see CtzDateUtilsTest
 */
public class CtzDateUtils {
    public static final LocalDateTime DEFAULT_DATE = new LocalDateTime(0, 1, 1, 0, 0);
    public static final DateTimeFormatter DEFAULT_LDT_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DEFAULT_LD_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * Converts optional {@link java.util.Date} into optional joda-time {@code LocalDateTime}
     *
     * @param optional optional date
     * @return {@code Optional.absent()} on absent input, converted {@code LocalDateTime}
     *  wrapped into optional otherwise
     */
    public static Optional<LocalDateTime> toLocalDateTime(Optional<? extends Date> optional) {
        if(!optional.isPresent()) return Optional.absent();
        Date date = optional.get();
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTime(date);
        return Optional.of(toLocalDateTime(cal));
    }

    /**
     * Converts {@link java.util.Date} to joda-time {@code LocalDateTime}
     *
     * @param date date to convert
     * @return LocalDateTime instance
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        checkNotNull(date);
        Calendar cal = new GregorianCalendar(0, 0, 0);
        cal.setTime(date);
        return toLocalDateTime(cal);
    }

    /**
     * Converts {@link java.util.Calendar} to joda-time {@code LocalDateTime}
     *
     * @param cal calendar to convert
     * @return LocalDateTime instance
     */
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

    /**
     * @param date nullable date
     * @return {@code CtzDateUtils.DEFAULT_DATE} on null input, provided date otherwise
     */
    public static LocalDateTime defaultDate(@Nullable Date date) {
        return defaultDate(date, DEFAULT_DATE);
    }

    /**
     * @param date nullable date
     * @param defaultDate default date
     * @return default date on null input, provided date otherwise
     */
    public static LocalDateTime defaultDate(@Nullable Date date, LocalDateTime defaultDate) {
        return null == date ? defaultDate : toLocalDateTime(date);
    }

    /**
     * Divides date period into date periods with provided duration
     *
     * @param from start date
     * @param to end date
     * @param step period duration
     * @return list of periods
     */
    public static List<FromToPeriod> stepList(LocalDateTime from, LocalDateTime to, Duration step) {
        checkArgument(from.isBefore(to), "fromDate: '%s' must be before toDate: '%s'", from, to);
        ImmutableList.Builder<FromToPeriod> builder = ImmutableList.builder();
        LocalDateTime cur = from;
        while(to.isAfter(cur)) {
            LocalDateTime next = cur.plus(step);
            next = to.isAfter(next) ? next.minusSeconds(1) : to;
            builder.add(new FromToPeriod(cur, next));
            cur = next.plusSeconds(1);
        }
        return builder.build();
    }
}
