package ru.concerteza.util.io;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import ru.concerteza.util.io.noclose.NoCloseOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import static org.junit.Assert.assertFalse;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class WriteOnlyOutputStreamTest {
    @Test
    public void test() throws IOException {
        ImportantOutputStream out = new ImportantOutputStream();
        OutputStream guard = NoCloseOutputStream.of(out);
        Writer writer = new CrapWriter(guard);
        writer.flush();
        writer.close();
        assertFalse("flush fail", out.isFlushed());
        assertFalse("close fail", out.isClosed());
    }

    private class CrapWriter extends Writer {
        private final OutputStream target;

        private CrapWriter(OutputStream target) {
            this.target = target;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public void flush() throws IOException {
            target.flush();
        }

        @Override
        public void close() throws IOException {
            target.close();
        }
    }


    private class ImportantOutputStream extends OutputStream {
        private boolean flushed = false;
        private boolean closed = false;

        @Override
        public void write(int b) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public void flush() throws IOException {
            flushed = true;
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        public boolean isFlushed() {
            return flushed;
        }

        public boolean isClosed() {
            return closed;
        }
    }

}
