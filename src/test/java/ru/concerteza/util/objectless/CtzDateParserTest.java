package ru.concerteza.util.objectless;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.text.ParseException;

import static java.lang.System.arraycopy;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.objectless.CtzDateParser.parseDate;
import static ru.concerteza.util.string.CtzConstants.ASCII_CHARSET;

/**
 * User: alexkasko
 * Date: 4/18/13
 */
public class CtzDateParserTest {
    @Test
    public void test() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        assertEquals("first pass fail", parseDate("20120207203049".getBytes(ASCII_CHARSET), 0),
                dtf.parseLocalDateTime("20120207203049").toDate().getTime());
        assertEquals("subsequent pass fail", parseDate("20120207113049".getBytes(ASCII_CHARSET), 0),
                dtf.parseLocalDateTime("20120207113049").toDate().getTime());
        assertEquals("date break fail", parseDate("20120208113049".getBytes(ASCII_CHARSET), 0),
                dtf.parseLocalDateTime("20120208113049").toDate().getTime());
        byte[] arr = new byte[1000];
        arraycopy("20120208113042".getBytes(ASCII_CHARSET), 0, arr, 442, 14);
        assertEquals("offset fail", parseDate(arr, 442),
                dtf.parseLocalDateTime("20120208113042").toDate().getTime());
    }
}
