package ru.concerteza.util.net.diriterator;

import com.jcraft.jsch.ChannelSftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class SftpFile extends BaseRemoteFile {
    private static final Logger logger = LoggerFactory.getLogger(SftpFile.class);

    private final ChannelSftp sftp;

    protected SftpFile(ChannelSftp sftp, DirIteratorPaths paths, String name) {
        super(name, paths.getDir() + name, paths.getSuccessDir() + name, paths.getErrorDir() + name);
        checkNotNull(sftp, "Specified 'sftp' is null");
        checkState(!sftp.isClosed() && sftp.isConnected(), "Specified 'ChannelSftp' is not connected");
        this.sftp = sftp;
    }

    @Override
    protected InputStream openInternal() throws Exception {
        logger.debug("Opening stream for file: [{}]", name);
        return sftp.get(path);
    }

    @Override
    protected void closeSuccess() throws Exception {
        logger.debug("Moving file to 'success': [{}]", name);
        sftp.rename(path, successPath);
    }

    @Override
    protected void closeError() {
        try {
            logger.warn("Moving file to 'error': [{}]", name);
            sftp.rename(path, errorPath);
        } catch (Exception e) {
            logger.warn("Error moving file: [" + this + "] to error dir", e);
        }
    }
}
