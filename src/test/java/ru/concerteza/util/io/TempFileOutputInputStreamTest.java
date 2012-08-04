package ru.concerteza.util.io;

import com.google.common.base.Function;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import ru.concerteza.util.value.Holder;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ru.concerteza.util.string.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class TempFileOutputInputStreamTest {
    @Test
    public void test() throws IOException {
        final Holder<String> dataHolder = new Holder<String>();
        final Holder<Long> lengthHolder = new Holder<Long>();

        class Fun implements Function<TempFileOutputInputStream.TempFile, Void> {
            @Override
            public Void apply(TempFileOutputInputStream.TempFile input) {
                try {
                    String str = IOUtils.toString(input.getDecompressed(), UTF8);
                    dataHolder.set(str);
                    lengthHolder.set(input.getDecompressedLength());
                    return null;
                } catch (IOException e) {
                    throw new UnhandledException(e);
                }
            }
        }

        OutputStream os = new TempFileOutputInputStream(new Fun());
        byte[] data = "foobar".getBytes(UTF8);
        IOUtils.write(data, os);
        assertNull(dataHolder.get());
        os.close();
        assertNotNull(dataHolder.get());
        assertEquals("foobar", dataHolder.get());
        assertEquals(data.length, (long) lengthHolder.get());
    }
}
