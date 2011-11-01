package ru.concerteza.util.date;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 10/31/11
 */
public class CtzDateUtilsTest {
    @Test
    public void testConvert() {
        Calendar cal = new GregorianCalendar(42, 5, 4, 3, 2, 1);
        LocalDateTime ldt = CtzDateUtils.toLocalDateTime(cal.getTime());
        assertEquals(cal.get(Calendar.YEAR), ldt.getYear());
        assertEquals(cal.get(Calendar.MONTH) + 1, ldt.getMonthOfYear());
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), ldt.getDayOfMonth());
        assertEquals(cal.get(Calendar.HOUR_OF_DAY), ldt.getHourOfDay());
        assertEquals(cal.get(Calendar.MINUTE), ldt.getMinuteOfHour());
        assertEquals(cal.get(Calendar.SECOND), ldt.getSecondOfMinute());
        assertEquals(cal.get(Calendar.MILLISECOND), ldt.getMillisOfSecond());
    }
}
