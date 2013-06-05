package ru.concerteza.util.net.sftp;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.io.finishable.Finishable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.io.finishable.FinishableFlag.FAIL;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * Single file SFTP iterator, degenerate version of SftpDirectoryIterator
 * with the similar public API
 *
 * @author alexkasko
 * Date: 4/19/13
 */
public class SftpFileIterator implements Iterator<SftpFile>, Finishable<Void, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(SftpFileIterator.class);
    private static final byte[] EMPTY = {'e', 'm', 'p', 't', 'y'};

    private final String host;
    private final int port;
    private final String fingerprint;
    private final String user;
    private final String readDir;
    private final String successDir;
    private final String errorDir;

    private final String filename;
    private final Session session;
    private final ChannelSftp sftp;

    private SftpFile processed;
    private boolean closed = false;
    private boolean read = false;

    public SftpFileIterator(String host, int port, String fingerprint, String user, String password,
                            String filename, String readDir, String successDir, String errorDir) {
        checkArgument(isNotBlank(host), "Provided host is blank");
        checkArgument(port > 0, "Provided port is invalid: [%s]", port);
        checkArgument(isNotBlank(fingerprint), "Provided fingerprint is blank");
        checkArgument(isNotBlank(user), "Provided user is blank");
        checkArgument(isNotBlank(filename), "Provided filename is blank");
        checkArgument(isNotBlank(password), "Provided password is blank");
        checkArgument(isNotBlank(readDir), "Provided readDir is blank");
        checkArgument(isNotBlank(successDir), "Provided successDir is blank");
        checkArgument(isNotBlank(errorDir), "Provided errorDir is blank");
        this.host = host;
        this.port = port;
        this.fingerprint = fingerprint;
        this.user = user;
        this.filename = filename;
        this.readDir = readDir;
        this.successDir = successDir;
        this.errorDir = errorDir;
        // open
        try {
            logger.debug("Accessing SFTP, host: [{}], readDir: [{}], filename: [{}]", new Object[]{host, readDir, filename});
            JSch jsch = new JSch();
            SftpKnownHosts hosts = new SftpKnownHosts(fingerprint);
            jsch.setHostKeyRepository(hosts);
            session = jsch.getSession(user, host, port);
            session.setPassword(password.getBytes(UTF8_CHARSET));
            session.connect();
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            sftp.cd(readDir);
            List<ChannelSftp.LsEntry> entries = sftp.ls(".");
            List<String> names = Lists.transform(entries, NameFun.INSTANCE);
            // linear search is deliberate here - list should be small
            List<String> files = Ordering.natural().immutableSortedCopy(Iterables.filter(names, DotPredicate.INSTANCE));
            if(!files.contains(filename)) throw new CtzSftpException(
                              "Requested name: [" + filename + "] was not found in ls: [" + files + "], " +
                                      "host: [" + host + "], port: [" + port + "], read dir: [" + readDir + "]");
            logger.debug("File ensured: [{}]", filename);
        } catch (Exception e) {
            finish(FAIL);
            throw e instanceof CtzSftpException ? (CtzSftpException) e : new CtzSftpException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if (closed) return false;
        return !read;
    }

    @Override
    public SftpFile next() {
        read = true;
        InputStream is = null;
        try {
            logger.debug("Opening snapped stream for file: [{}]", filename);
            InputStream sftpStream = sftp.get(filename);
            processed = new SftpFile(filename, sftpStream);
            return processed;
        } catch (Exception e) {
            processed = new SftpFile(filename, new ByteArrayInputStream(EMPTY));
            closeQuietly(is);
            throw new CtzSftpException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    private void markProcessed(SftpFile rf) throws SftpException {
        logger.debug("Moving file to 'processed': [{}]", rf);
        String processed = successDir + "/" + rf.getName();
        sftp.rename(rf.getName(), processed);
    }

    private void markError(SftpFile rf) throws SftpException {
        logger.warn("Moving file to 'error': [{}]", rf);
        String error = errorDir + "/" + rf.getName();
        sftp.rename(rf.getName(), error);
    }

    @Override
    public void finish(Function<Void, Boolean> fun) {
        if (closed) return;
        logger.debug("Preparing to close SFTP connection to host: [{}]", host);
        boolean success = fun.apply(null);
        if (null != sftp) {
            closeQuietly(processed);
            try {
                if (success) markProcessed(processed);
                else markError(processed);
            } catch (SftpException e) {
                logger.warn(e.getMessage(), e);
            }
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("filename", filename).
                append("read", read).
                append("session", session).
                append("sftp", sftp).
                append("host", host).
                append("port", port).
                append("fingerprint", fingerprint).
                append("user", user).
                append("readDir", readDir).
                toString();
    }
}
