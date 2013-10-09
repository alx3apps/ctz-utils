package ru.concerteza.util.net.sftp;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * Iterator over remote SFTP directory. Not thread-safe.
 *
 * @author alexkasko
 * Date: 4/19/13
 */
public class SftpDirectoryIterator implements Iterator<SftpFile>, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SftpDirectoryIterator.class);

    private final String host;
    private final int port;
    private final HostKeyRepository hostKeys;
    private final String user;
    private final String readDir;
    private final String successDir;
    private final String errorDir;

    private final ImmutableList<String> fileNames;
    private final Session session;
    private final ChannelSftp sftp;

    private boolean closed = false;
    private int index = 0;

    /**
     * Constructor with lenient host checking
     *
     * @param host host name
     * @param port port
     * @param user username
     * @param password password
     * @param readDir read directory
     * @param successDir success directory
     * @param errorDir error directory
     */
    public SftpDirectoryIterator(String host, int port, String user, String password,
                                 String readDir, String successDir, String errorDir) {
        this(host, port, new SftpLenientKnownHosts(), user, password, readDir, successDir, errorDir, Predicates.<String>alwaysTrue());
    }

    /**
     * Constructor
     *
     * @param host host name
     * @param port port
     * @param hostKeys host checker
     * @param user username
     * @param password password
     * @param readDir read directory
     * @param successDir success directory
     * @param errorDir error directory
     * @param fileFiler filter for remote files
     */
    public SftpDirectoryIterator(String host, int port, HostKeyRepository hostKeys, String user, String password,
                                 String readDir, String successDir, String errorDir, Predicate<String> fileFiler) {
        checkArgument(isNotBlank(host), "Provided host is blank");
        checkArgument(port > 0, "Provided port is invalid: [%s]", port);
        checkArgument(null != hostKeys, "Provided hostKeys is mill");
        checkArgument(isNotBlank(user), "Provided user is blank");
        checkArgument(isNotBlank(password), "Provided password is blank");
        checkArgument(isNotBlank(readDir), "Provided readDir is blank");
        checkArgument(isNotBlank(successDir), "Provided successDir is blank");
        checkArgument(isNotBlank(errorDir), "Provided errorDir is blank");
        this.host = host;
        this.port = port;
        this.hostKeys = hostKeys;
        this.user = user;
        this.readDir = readDir;
        this.successDir = successDir;
        this.errorDir = errorDir;
        // open
        try {
            logger.debug("Accessing SFTP, host: [{}], readDir: [{}]", host, readDir);
            JSch jsch = new JSch();
            jsch.setHostKeyRepository(hostKeys);
            session = jsch.getSession(user, host, port);
            session.setPassword(password.getBytes(UTF8_CHARSET));
            session.connect();
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            List<ChannelSftp.LsEntry> entries = sftp.ls(readDir);
            List<String> names = Lists.transform(entries, NameFun.INSTANCE);
            Iterable<String> nodots = Iterables.filter(names, DotPredicate.INSTANCE);
            Iterable<String> filtered = Iterables.filter(nodots, fileFiler);
            fileNames = Ordering.natural().immutableSortedCopy(filtered);
            logger.debug("Files list obtained: [{}]", fileNames);
        } catch (Exception e) {
            close();
            throw new CtzSftpException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (closed) return false;
        return index < fileNames.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SftpFile next() {
        String filename = fileNames.get(index++);
        return new SftpFile(sftp, filename, readDir, successDir, errorDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (closed) return;
        if (null != sftp) {
            try {
                sftp.disconnect();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        if (null != session) {
            try {
                session.disconnect();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        logger.debug("SFTP connection to host: [{}] is closed", host);
        this.closed = true;
    }

    /**
     * Return list of remote file names
     *
     * @return list of remote file names
     */
    public ImmutableList<String> getFileNames() {
        return null != fileNames ? fileNames : ImmutableList.<String>of();
    }

    private enum NameFun implements Function<ChannelSftp.LsEntry, String> {
        INSTANCE;
        @Override
        public String apply(ChannelSftp.LsEntry input) {
            return input.getFilename();
        }
    }

    private enum DotPredicate implements Predicate<String> {
        INSTANCE;
        @Override
        public boolean apply(String input) {
            return !".".equals(input) && !"..".equals(input);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("fileNames", fileNames).
                append("index", index).
                append("session", session).
                append("sftp", sftp).
                append("host", host).
                append("port", port).
                append("hostKeys", hostKeys).
                append("user", user).
                append("readDir", readDir).
                toString();
    }
}
