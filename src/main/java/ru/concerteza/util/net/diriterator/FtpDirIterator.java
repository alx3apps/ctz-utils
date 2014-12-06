package ru.concerteza.util.net.diriterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class FtpDirIterator extends BaseDirIterator<FtpFile> {
    private static final Logger logger = LoggerFactory.getLogger(FtpDirIterator.class);
    private static final int DEFAULT_CONTROL_KEEPALIVE_TIMEOUT = 600;

    private final int controlKeepAliveTimeout;
    private FTPClient ftp;

    public FtpDirIterator(String host, int port, String user, String password, DirIteratorPaths paths) {
        this(host, port, user, password, paths, Predicates.<String>alwaysTrue(), DEFAULT_CONTROL_KEEPALIVE_TIMEOUT);
    }
    public FtpDirIterator(String host, int port, String user, String password, DirIteratorPaths paths,
                             Predicate<String> filenameFilter, int controlKeepAliveTimeout) {
        super(host, port, user, password, paths, filenameFilter);
        this.controlKeepAliveTimeout = controlKeepAliveTimeout;
    }

    @Override
    protected FtpFile createFile(String name) {
        return new FtpFile(ftp, paths, name);
    }

    @Override
    protected void openInternal() throws Exception {
        logger.debug("Accessing SFTP, host: [{}], paths: [{}]", host, paths);
        this.ftp = new FTPClient();
        ftp.setControlKeepAliveTimeout(controlKeepAliveTimeout);
        ftp.connect(host, port);
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            throw new DirIteratorException("Error opening FTP: [" + this + "], connect reply: [" + ftp.getReplyCode() + "]");
        }
        checkFtp(ftp.login(user, password), "login");
        checkFtp(ftp.setFileType(FTP.BINARY_FILE_TYPE), "setFileType");
        checkFtp(ftp.changeWorkingDirectory(paths.getDir()), "changeWorkingDirectory");
        List<FTPFile> entries = newArrayList((ftp.listFiles()));
        List<String> names = Lists.transform(entries, NameFun.INSTANCE);
        Iterable<String> nodots = Iterables.filter(names, DotPredicate.INSTANCE);
        Iterable<String> filtered = Iterables.filter(nodots, filenameFilter);
        fileNames = Ordering.natural().immutableSortedCopy(filtered);
        logger.debug("Files list obtained: [{}]", fileNames);

    }

    @Override
    protected void closeInternal() throws Exception {
        if (null != ftp && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
        logger.debug("FTP connection to host: [{}] is closed", host);

    }

    private void checkFtp(boolean reply, String command) {
        if(reply) return;
        throw new DirIteratorException("FTP error, command: [" + command + "], file: [" + this + "]");
    }

    private enum NameFun implements Function<FTPFile, String> {
        INSTANCE;
        @Override
        public String apply(FTPFile input) {
            return input.getName();
        }
    }

}
