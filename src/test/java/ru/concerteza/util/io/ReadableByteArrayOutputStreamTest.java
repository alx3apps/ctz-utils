package ru.concerteza.util.io;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * User: alexey
 * Date: 8/29/12
 */
public class ReadableByteArrayOutputStreamTest {
    @Test
    public void test() {
        ReadableByteArrayOutputStream rbaos = new ReadableByteArrayOutputStream();
        rbaos.write(41);
        byte[] data = {42, 43};
        rbaos.write(data, 0, 2);
        assertEquals(41, rbaos.read());
        byte[] readed = new byte[2];
        rbaos.read(readed, 0, 2);
        assertArrayEquals(data, readed);
    }
}
