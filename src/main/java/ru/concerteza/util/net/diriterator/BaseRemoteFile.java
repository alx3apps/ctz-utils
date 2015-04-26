package ru.concerteza.util.net.diriterator;

import com.google.common.base.Function;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public abstract class BaseRemoteFile implements RemoteFile {
    private static final Logger logger = LoggerFactory.getLogger(SftpFile.class);

    protected enum State {CREATED, OPEN, CLOSED}

    protected final String name;
    protected final String path;
    protected final String successPath;
    protected final String errorPath;

    protected InputStream stream;
    protected State state = State.CREATED;

    protected BaseRemoteFile(String name, String path, String successPath, String errorPath) {
        this.name = name;
        this.path = path;
        this.successPath = successPath;
        this.errorPath = errorPath;
    }

    protected abstract InputStream openInternal() throws Exception;

    protected abstract void closeSuccess() throws Exception;

    protected abstract void closeError();

    @Override
    public void finish(Function<Void, Boolean> fun) {
        if (State.OPEN != state) return;
        if(fun.apply(null)) {
            try {
                stream.close();
                closeSuccess();
                state = State.CLOSED;
            } catch (Exception e) {
                throw e instanceof DirIteratorException ? (DirIteratorException) e :
                        new DirIteratorException("Error on success-closing file: [" + this + "]", e);
            }
        } else {
            closeQuietly(stream);
            closeError();
            state = State.CLOSED;
        }
    }

    @Override
    public InputStream open() {
        if (State.CREATED != state) throw new DirIteratorException("Invalid state: [" + state + "] for 'open' operation");
        try {
            InputStream is = openInternal();
            state = State.OPEN;
            return is;
        } catch (Exception e) {
            throw e instanceof DirIteratorException ? (DirIteratorException) e : new DirIteratorException("Open error", e);
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSuccessPath() {
        return successPath;
    }

    public String getErrorPath() {
        return errorPath;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("name", name).
                append("path", path).
                append("successPath", successPath).
                append("errorPath", errorPath).
                append("stream", stream).
                append("state", state).
                toString();
    }
}
