package ru.concerteza.util.io;

import com.google.common.base.Function;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.junit.Assert;
import org.junit.Test;
import ru.concerteza.util.CtzConstants;
import ru.concerteza.util.value.Holder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ru.concerteza.util.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class TempFileOutputInputStreamTest {
    @Test
    public void test() throws IOException {
        final Holder<String> holder = new Holder<String>();

        class Fun implements Function<InputStream, Void> {
            @Override
            public Void apply(InputStream input) {
                try {
                    String str = IOUtils.toString(input, UTF8);
                    holder.set(str);
                    return null;
                } catch (IOException e) {
                    throw new UnhandledException(e);
                }
            }
        }

        OutputStream os = new TempFileOutputInputStream(new Fun());
        IOUtils.write("foobar", os, UTF8);
        assertNull(holder.get());
        os.close();
        assertNotNull(holder.get());
        assertEquals("foobar", holder.get());
    }
}
