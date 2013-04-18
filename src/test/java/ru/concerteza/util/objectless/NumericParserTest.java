package ru.concerteza.util.objectless;

import org.junit.Test;

import static java.lang.System.arraycopy;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.objectless.NumericParser.parseInt;
import static ru.concerteza.util.objectless.NumericParser.parseIpV4;
import static ru.concerteza.util.objectless.NumericParser.parseLong;
import static ru.concerteza.util.string.CtzConstants.ASCII_CHARSET;

/**
 * User: alexkasko
 * Date: 4/18/13
 */
public class NumericParserTest {
    @Test
    public void testLong() {
        assertEquals(Long.parseLong("8611563489"), parseLong("8611563489".getBytes(ASCII_CHARSET)));
        assertEquals(Long.parseLong("8611825726"), parseLong("8611825726".getBytes(ASCII_CHARSET)));
        byte[] data = "8611825742".getBytes(ASCII_CHARSET);
        byte[] arr = new byte[1000];
        arraycopy(data, 0, arr, 442, data.length);
        assertEquals(Long.parseLong("8611825742"), parseLong(arr, 442, data.length));
    }

    @Test
    public void testInt() {
        assertEquals(Integer.parseInt("611563489"), parseInt("611563489".getBytes(ASCII_CHARSET)));
        assertEquals(Integer.parseInt("611825726"), parseInt("611825726".getBytes(ASCII_CHARSET)));
        byte[] data = "611825742".getBytes(ASCII_CHARSET);
        byte[] arr = new byte[1000];
        arraycopy(data, 0, arr, 442, data.length);
        assertEquals(Integer.parseInt("611825742"), parseInt(arr, 442, data.length));
    }

    @Test
    public void testIpV4() {
        assertEquals(3378010069L, parseIpV4("201.88.87.213".getBytes(ASCII_CHARSET)));
        assertEquals(2823141469L, parseIpV4("168.69.184.93".getBytes(ASCII_CHARSET)));
        assertEquals(167779082L, parseIpV4("10.0.27.10  ".getBytes(ASCII_CHARSET)));
        byte[] data = "10.0.27.10\0".getBytes(ASCII_CHARSET);
        byte[] arr = new byte[1000];
        arraycopy(data, 0, arr, 442, data.length);
        assertEquals(167779082L, parseIpV4(arr, 442));
    }
}

