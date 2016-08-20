package ru.concerteza.util.objectless;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Character.digit;
import static java.lang.System.arraycopy;
import static ru.concerteza.util.date.CtzDateUtils.toMillis;
import static ru.concerteza.util.string.CtzConstants.ASCII_CHARSET;

/**
 * Parses input datetime strings prevent object instantiation on every row.
 * Stateful, keeps state in thread locals (it may have side effects in some environments).
 * Uses current timezone, has no daylight saving support.
 * Supports pattern: {@code yyyyMMddHHmmss}, support for other patterns may be added.
 *
 * @author alexkasko
 * Date: 4/18/13
 */
public class CtzDateParser {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ThreadLocal<byte[]> dateBytes;
    private static final ThreadLocal<Long> dateMillis;
    static {
        dateBytes = new ThreadLocal<byte[]>();
        dateBytes.set(new byte[8]);
        dateMillis = new ThreadLocal<Long>();
        dateMillis.set(0L);
    }

    /**
     * Parse date string into milliseconds using current timezone
     *
     * @param date date string
     * @param offset offset in date string
     * @return date milliseconds
     */
    public static long parseDate(byte[] date, int offset) {
        boolean dateEqual = true;
        for(int i = 0; i < 8; i++) {
            if(dateBytes.get()[i] != date[offset + i]) {
                dateEqual = false;
                break;
            }
        }
        if(!dateEqual) {
            String dateStr = new String(date, offset, 14, ASCII_CHARSET);
            dateMillis.set(toMillis(LocalDate.parse(dateStr, dtf)));
            arraycopy(date, offset, dateBytes.get(), 0, 8);
        }
        int hours = digit((char) date[offset + 8], 10) * 10;
        hours += digit((char) date[offset + 9], 10);
        int minutes = digit((char) date[offset + 10], 10) * 10;
        minutes += digit((char) date[offset + 11], 10);
        int seconds = digit((char) date[offset + 12], 10) * 10;
        seconds += digit((char) date[offset + 13], 10);
        int fullSecs = hours * 3600 + minutes * 60 + seconds;
        return dateMillis.get() + fullSecs * 1000;
    }
}