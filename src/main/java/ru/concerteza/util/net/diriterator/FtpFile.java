package ru.concerteza.util.net.diriterator;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class FtpFile extends BaseRemoteFile {
    private static final Logger logger = LoggerFactory.getLogger(FtpFile.class);

    private final FTPClient ftp;

    public FtpFile(FTPClient ftp, DirIteratorPaths paths, String name) {
        super(name, paths.getDir() + name, paths.getSuccessDir() + name, paths.getErrorDir() + name);
        checkNotNull(ftp, "Specified 'ftp' is null");
        checkState(ftp.isConnected(), "Specified 'FTPClient' is not connected");
        this.ftp = ftp;
    }

    @Override
    protected InputStream openInternal() throws Exception {
        logger.debug("Opening stream for file: [{}]", name);
        return ftp.retrieveFileStream(path);
    }

    @Override
    protected void closeSuccess() throws Exception {
        checkFtp(ftp.completePendingCommand(), "completePendingCommand");
        checkFtp(ftp.rename(path, successPath), "closeSuccess");
    }

    @Override
    protected void closeError() {
        Boolean pres = null;
        try {
            logger.warn("Moving file to 'error': [{}]", name);
            pres = ftp.completePendingCommand();
            checkFtp(ftp.rename(path, errorPath), "closeError");
        } catch (Exception e) {
            logger.warn("Error moving file: [" + this + "] to error dir, pres: [" + pres + "]", e);
        }
    }

    private void checkFtp(boolean reply, String command) {
        if(reply) return;
        throw new DirIteratorException("FTP error, command: [" + command + "], file: [" + this + "]");
    }

}
