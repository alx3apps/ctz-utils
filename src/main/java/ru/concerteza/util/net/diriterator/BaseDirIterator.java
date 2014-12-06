package ru.concerteza.util.net.diriterator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public abstract class BaseDirIterator<T extends BaseRemoteFile> implements DirIterator<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseDirIterator.class);

    protected enum State {CREATED, OPEN, CLOSED}

    protected final String host;
    protected final int port;
    protected final String user;
    protected final String password;

    protected final DirIteratorPaths paths;
    protected final Predicate<String> filenameFilter;

    protected ImmutableList<String> fileNames = ImmutableList.of();
    protected int index = 0;

    protected State state = State.CREATED;

    protected BaseDirIterator(String host, int port, String user, String password, DirIteratorPaths paths, Predicate<String> filenameFilter) {
        checkArgument(isNotBlank(host), "Specified host is blank");
        checkArgument(port > 0 && port <= 65535, "Specified port is invalid: [%s]", port);
        this.host = host;
        this.port = port;
        this.user = checkNotNull(user, "Specified user is null");
        this.password = checkNotNull(password, "Specified password is null");
        this.paths = checkNotNull(paths, "Specified paths arg is null");
        this.filenameFilter = checkNotNull(filenameFilter, "Specified filenameFilter is null");
    }

    protected abstract T createFile(String name);

    protected abstract void openInternal() throws Exception;

    protected abstract void closeInternal() throws Exception;

    @Override
    public DirIterator<T> open() {
        if (State.CREATED != state) throw new DirIteratorException("Invalid state: [" + state + "] for 'open' operation");
        try {
            openInternal();
            this.state = State.OPEN;
            return this;
        } catch (Exception e) {
            throw e instanceof DirIteratorException ? (DirIteratorException) e : new DirIteratorException("Open error", e);
        }
    }

    @Override
    public void close() {
        if (State.OPEN != state) return;
        try {
            closeInternal();
        } catch (Exception e) {
            logger.warn("Error closing iterator: [" + this + "]", e);
        } finally {
            state = State.CLOSED;
        }
    }

    @Override
    public boolean hasNext() {
        if (State.OPEN != state) throw new DirIteratorException("Invalid state: [" + state + "] for 'hasNext' operation");
        return index < fileNames.size();
    }

    @Override
    public T next() {
        String filename = fileNames.get(index++);
        return createFile(filename);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("host", host).
                append("port", port).
                append("user", user).
                append("paths", paths).
                append("fileNames", fileNames).
                append("index", index).
                toString();
    }

    protected enum DotPredicate implements Predicate<String> {
        INSTANCE;
        @Override
        public boolean apply(String input) {
            return !".".equals(input) && !"..".equals(input);
        }
    }

}
