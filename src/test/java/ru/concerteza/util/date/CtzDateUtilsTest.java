package ru.concerteza.util.date;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.date.CtzDateUtils.toDate;
import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;

/**
 * User: alexey
 * Date: 10/31/11
 */
public class CtzDateUtilsTest {

    @Test
    public void testDateToLocalDateTimeThereAndBackAgain() {
        Calendar cal = new GregorianCalendar(42, 5, 4, 3, 2, 1);
        Date date1 = cal.getTime();
        LocalDateTime ldt1 = toLocalDateTime(date1);
        Date date2 = toDate(ldt1);
        LocalDateTime ldt2 = toLocalDateTime(date2);

        assertEquals(date1, date2);
        assertEquals(ldt1, ldt2);
    }

    @Test
    public void testConvert() {
        Calendar cal = new GregorianCalendar(42, 5, 4, 3, 2, 1);
        Date date = cal.getTime();
        LocalDateTime ldt = toLocalDateTime(date);
        assertEquals(cal.get(Calendar.YEAR), ldt.getYear());
        assertEquals(cal.get(Calendar.MONTH) + 1, ldt.getMonthValue());
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), ldt.getDayOfMonth());
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), ldt.getHour());
        assertEquals(cal.get(Calendar.MINUTE), ldt.getMinute());
        assertEquals(cal.get(Calendar.SECOND), ldt.getSecond());
        assertEquals(cal.get(Calendar.MILLISECOND), ldt.getNano() / 1_000_000);
    }

    @Test
    public void testMax() {
        LocalDateTime foo = LocalDateTime.of(2012, 1, 1, 0, 0, 1);
        LocalDateTime bar = LocalDateTime.of(2012, 1, 1, 0, 0, 42);
        LocalDateTime baz = LocalDateTime.of(2012, 1, 1, 0, 0, 2);
        LocalDateTime max = ObjectUtils.max(foo, bar, baz);
        assertEquals(bar, max);
    }

    @Test
    public void testMin() {
        LocalDateTime foo = LocalDateTime.of(2012, 1, 1, 0, 0, 1);
        LocalDateTime bar = LocalDateTime.of(2012, 1, 1, 0, 0, 0);
        LocalDateTime baz = LocalDateTime.of(2012, 1, 1, 0, 0, 2);
        LocalDateTime min = ObjectUtils.min(foo, bar, baz);
        assertEquals(bar, min);
    }
}
