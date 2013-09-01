package ru.concerteza.util.net;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.net.CtzNetUtils.*;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class CtzNetUtilsTest {
    @Test
    public void testIp() {
        assertEquals("201.88.87.213", printIpV4(parseIpV4("201.88.87.213")));
        assertEquals("168.69.184.93", printIpV4(parseIpV4("168.69.184.93")));
        assertEquals("128.147.27.10", printIpV4(parseIpV4("128.147.27.10")));
        assertEquals("128.0.0.10", printIpV4(parseIpV4("128.0.0.10")));
    }

    public void testMac() {
        assertEquals("60:67:20:96:f9:80", printMac48(parseMac48("60:67:20:96:f9:80")));
    }
}
