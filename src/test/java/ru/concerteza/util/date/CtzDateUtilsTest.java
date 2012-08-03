package ru.concerteza.util.date;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Test
    public void testSteps() {
        LocalDateTime start = new LocalDateTime(2012, 1, 1, 0, 0, 0);
        LocalDateTime end = new LocalDateTime(2012, 1, 1, 12, 0, 0);
        List<FromToPeriod> periods = CtzDateUtils.stepList(start, end, Duration.standardHours(4));
        assertEquals("Size fail", 3, periods.size());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 0, 0, 0), periods.get(0).getFrom());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 3, 59, 59), periods.get(0).getTo());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 4, 0, 0), periods.get(1).getFrom());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 7, 59, 59), periods.get(1).getTo());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 8, 0, 0), periods.get(2).getFrom());
        assertEquals("Date fail", new LocalDateTime(2012, 1, 1, 12, 0, 0), periods.get(2).getTo());
    }
}
