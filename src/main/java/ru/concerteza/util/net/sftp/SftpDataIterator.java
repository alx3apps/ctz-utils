package ru.concerteza.util.net.sftp;

import com.google.common.collect.AbstractIterator;
import org.iq80.snappy.SnappyInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.net.sftp.SftpFile;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Iterator wrapper for files read through SFTP
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public abstract class SftpDataIterator extends AbstractIterator<byte[]> implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SftpDataIterator.class);

    private enum State {CREATED, OPEN, CLOSED}

    private final byte[] buf;

    private SftpFile file;
    private InputStream stream;
    private State state = State.CREATED;

    protected SftpDataIterator(int bufferSize) {
        this.buf = new byte[bufferSize];
    }

    /**
     * Starts iterator
     *
     * @param file remote SFTP file
     * @return iterator itself
     */
    public SftpDataIterator open(SftpFile file) {
        checkNotNull(file, "Provided SFTP file is null");
        try {
            this.file = file;
            this.stream = new SnappyInputStream(new BufferedInputStream(file.getStream()));
            this.state = State.OPEN;
            return this;
        } catch (IOException e) {
            throw new CtzSftpException("Error opening stream for file: [" + file + "]", e);
        }
    }

    /**
     * Closes SFTP file
     */
    @Override
    public void close() {
        if(State.CLOSED == state) return;
        closeQuietly(file);
        state = State.CLOSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] computeNext() {
        if (State.CLOSED == state) return endOfData();
        if (State.CREATED == state) throw new CtzSftpException("Iterator wasn't initialized");
        try {
            return read();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            close();
            throw new CtzSftpException(e);
        }
    }

    /**
     * Implementation should read next data portion from stream
     *
     * @param stream data stream
     * @param buf dest buffer
     * @param name current file name, for error reporting
     * @return number of bytes read into buffer
     * @throws IOException on io error
     */
    protected abstract int readFromStream(InputStream stream, byte[] buf, String name) throws IOException;

    private byte[] read() throws IOException {
        int curlen = readFromStream(stream, buf, file.getName());
        if (-1 == curlen) return finish();
        return buf;
    }

    private byte[] finish() {
        close();
        return endOfData();
    }
}
