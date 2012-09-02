package ru.concerteza.util.date;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.base.AbstractPartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.concerteza.util.collection.SingleUseIterable;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.string.CtzFormatUtils.format;

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
     * Converts {@link java.util.Date} to joda-time {@code LocalDate}
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
     * Converts {@link java.util.Calendar} to joda-time {@code LocalDate}
     *
     * @param cal calendar to convert
     * @return LocalDateTime instance
     */
    public static LocalDate toLocalDate(Calendar cal) {
        checkNotNull(cal);
        return new LocalDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Converts optional {@link java.util.Date} into optional joda-time {@code LocalDateTime}
     *
     * @param optional optional date
     * @return {@code Optional.absent()} on absent input, converted {@code LocalDateTime}
     *  wrapped into optional otherwise
     */
    public static Optional<LocalDateTime> toOptionalLDT(Optional<? extends Date> optional) {
        if(!optional.isPresent()) return Optional.absent();
        Date date = optional.get();
        return Optional.of(toLocalDateTime(date));
    }

    /**
     * Converts optional {@link java.util.Date} into optional joda-time {@code LocalDate}
     *
     * @param optional optional date
     * @return {@code Optional.absent()} on absent input, converted {@code LocalDate}
     *  wrapped into optional otherwise
     */
    public static Optional<LocalDate> toOptionalLD(Optional<? extends Date> optional) {
        if(!optional.isPresent()) return Optional.absent();
        Date date = optional.get();
        return Optional.of(toLocalDate(date));
    }

    /**
     * Converts optional {@code LocalDatetime} or {@code LocaDate} into optional {@link Date}
     *
     * @param optional optional {@code LocalDatetime} or {@code LocaDate}
     * @return optional {@link Date}
     * @throws IllegalArgumentException if input is not optional {@code LocalDatetime} or {@code LocaDate}
     */
    public static Optional<Date> toOptionalJUD(Optional<? extends ReadablePartial> optional) {
        if(!optional.isPresent()) return Optional.absent();
        ReadablePartial rp = optional.get();
        if(rp instanceof LocalDateTime) {
            LocalDateTime ldt = (LocalDateTime) rp;
            return Optional.of(ldt.toDate());
        } else if(rp instanceof LocalDate) {
            LocalDate ld = (LocalDate) rp;
            return Optional.of(ld.toDate());
        } throw new IllegalArgumentException(format(
                "Only LocalDatetime and LocalDate input are supported, but was: '{}'", rp));
    }

    /**
     * Converts optional date time String (in {@link #DEFAULT_LDT_FORMAT}) into optional joda-time {@code LocalDateTime}
     *
     * @param optional optional date
     * @return {@code Optional.absent()} on absent input, converted {@code LocalDateTime}
     *  wrapped into optional otherwise
     */
    public static Optional<LocalDateTime> parseOptionalLDT(Optional<String> optional) {
        if(!optional.isPresent()) return Optional.absent();
        LocalDateTime date = DEFAULT_LDT_FORMAT.parseLocalDateTime(optional.get());
        return Optional.of(date);
    }

    /**
     * Converts optional date time String (in {@link #DEFAULT_LD_FORMAT}) into optional joda-time {@code LocalDate}
     *
     * @param optional optional date
     * @return {@code Optional.absent()} on absent input, converted {@code LocalDate}
     *  wrapped into optional otherwise
     */
    public static Optional<LocalDate> parseOptionalLD(Optional<String> optional) {
        if(!optional.isPresent()) return Optional.absent();
        LocalDate date = DEFAULT_LD_FORMAT.parseLocalDate(optional.get());
        return Optional.of(date);
    }


    /**
     * @param date nullable date
     * @return {@code CtzDateUtils.DEFAULT_DATE} on null input, provided date otherwise
     */
    @Deprecated // use optional
    public static LocalDateTime defaultDate(@Nullable Date date) {
        return defaultDate(date, DEFAULT_DATE);
    }

    /**
     * @param date nullable date
     * @param defaultDate default date
     * @return default date on null input, provided date otherwise
     */
    @Deprecated // use optional
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

    /**
     * Finds maximum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return max date
     */
    public static <T extends AbstractPartial> T max(T... dates) {
        checkArgument(dates.length > 0, "No dates provided");
        return max(Iterators.forArray(dates));
    }

    /**
     * Finds maximum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return max date
     */
    public static <T extends AbstractPartial> T max(Iterator<T> dates) {
        return max(SingleUseIterable.of(dates));
    }

    /**
     * Finds maximum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return max date
     */
    public static <T extends AbstractPartial> T max(Iterable<T> dates) {
        T max = null;
        for(T t : dates) {
            checkNotNull(t, "Provided date is null");
            if(null == max || t.isAfter(max)) max = t;
        }
        return max;
    }

    /**
     * Finds minimum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return min date
     */
    public static <T extends AbstractPartial> T min(T... dates) {
        checkArgument(dates.length > 0, "No dates provided");
        return min(Iterators.forArray(dates));
    }

    /**
     * Finds minimum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return min date
     */
    public static <T extends AbstractPartial> T min(Iterator<T> dates) {
        return min(SingleUseIterable.of(dates));
    }

    /**
     * Finds minimum between dates
     *
     * @param dates dates list
     * @param <T> date type
     * @return min date
     */
    public static <T extends AbstractPartial> T min(Iterable<T> dates) {
        T min = null;
        for(T t : dates) {
            checkNotNull(t, "Provided date is null");
            if(null == min || t.isBefore(min)) min = t;
        }
        return min;
    }
}
