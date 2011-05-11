package ru.concerteza.util;

import org.junit.Assert;
import org.junit.Test;

import static ru.concerteza.util.IpAddressUtils.convertIpToLong;
import static ru.concerteza.util.IpAddressUtils.convertIpToString;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class IpAddressUtilsTest {
    @Test
    public void test() {
        Assert.assertEquals("201.88.87.213", convertIpToString(convertIpToLong("201.88.87.213")));
        Assert.assertEquals("168.69.184.93", convertIpToString(convertIpToLong("168.69.184.93")));
        Assert.assertEquals("128.147.27.10", convertIpToString(convertIpToLong("128.147.27.10")));
        Assert.assertEquals("128.0.0.10", convertIpToString(convertIpToLong("128.0.0.10")));
//        Assert.assertEquals("0.10.030.10", convertIpToString(convertIpToLong("0.10.030.10")));
    }
}
