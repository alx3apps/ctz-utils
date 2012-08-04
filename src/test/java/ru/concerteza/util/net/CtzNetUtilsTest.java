package ru.concerteza.util.net;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.net.CtzNetUtils.convertIpToLong;
import static ru.concerteza.util.net.CtzNetUtils.convertIpToString;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class CtzNetUtilsTest {
    @Test
    public void test() {
        assertEquals("201.88.87.213", convertIpToString(convertIpToLong("201.88.87.213")));
        assertEquals("168.69.184.93", convertIpToString(convertIpToLong("168.69.184.93")));
        assertEquals("128.147.27.10", convertIpToString(convertIpToLong("128.147.27.10")));
        assertEquals("128.0.0.10", convertIpToString(convertIpToLong("128.0.0.10")));
//        Assert.assertEquals("0.10.030.10", convertIpToString(convertIpToLong("0.10.030.10")));
    }
}
