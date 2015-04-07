package ru.concerteza.util.net.sftp;

import com.google.common.base.Function;
import com.jcraft.jsch.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.io.finishable.Finishable;

import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * File writer to remote server over SFTP
 *
 * @author alexkasko
 * Date: 6/30/13
 */
public class SftpFileWriter implements Finishable<Void, Boolean> {
    private enum State {CREATED, OPEN, CLOSED}
    private static final Logger logger = LoggerFactory.getLogger(SftpFileWriter.class);
    private final SftpDestination dest;
    private Session session;
    private ChannelSftp sftp;
    private String processingPath;
    private String writtenPath;
    private OutputStream out;
    private State state;

    /**
     * Constructor
     *
     * @param dest SFTP destination settings
     */
    public SftpFileWriter(SftpDestination dest) {
        this.dest = dest;
        this.state = State.CREATED;
    }

    /**
     * Opens SFTP connection and output stream to destination file
     *
     * @param filename destination file name
     * @return writer itself
     */
    public OutputStream open(String filename) {
        try {
            checkState(State.CREATED == state, "Illegal state: [%s]", state);
            logger.debug("Writing to SFTP: [{}], file: [{}]", dest, filename);
            this.processingPath = dest.getProcessingDirectory() + "/" + filename;
            this.writtenPath = dest.getWrittenDirectory() + "/" + filename;
            JSch jsch = new JSch();
            SftpKnownHosts hosts = new SftpKnownHosts(dest.getFingerprint());
            jsch.setHostKeyRepository(hosts);
            this.session = jsch.getSession(dest.getUsername(), dest.getHostname(), dest.getPort());
            this.session.setPassword(dest.getPassword().getBytes(UTF8_CHARSET));
            this.session.connect();
            this.sftp = (ChannelSftp) session.openChannel("sftp");
            this.sftp.connect();
            this.out = sftp.put(processingPath);
            this.state = State.OPEN;
            return out;
        } catch (JSchException e) {
            throw new CtzSftpException("Error opening sftp: [" + dest + "]");
        } catch (SftpException e) {
            throw new CtzSftpException("Error opening sftp: [" + dest + "]");
        }
    }

    /**
     * Unbuffered output stream accessor
     *
     * @return Unbuffered output stream
     */
    public OutputStream outputStream() {
        return out;
    }

    /**
     * Moves file to 'written' dir on success, deletes file on error.
     *
     * @param fun returns success flag
     */
    @Override
    public void finish(Function<Void, Boolean> fun) {
        if(State.CLOSED == state) return;
        if(fun.apply(null)) closeSuccess();
        else closeError();
    }

    private void closeSuccess() {
        try {
            closeQuietly(out);
            sftp.rename(processingPath, writtenPath);
            closeSftp();
        } catch (SftpException e) {
            throw new CtzSftpException("Error success-closing sftp: [" + dest + "], file: [" + processingPath + "]", e);
        }
    }

    private void closeError() {
        closeQuietly(out);
        if(null != sftp) {
            try {
                sftp.rm(processingPath);
            } catch (SftpException e) {
                logger.warn("Error deleting processing path: [" + processingPath + "] for sftp: [" + dest + "]", e);
            }
        }
        closeSftp();
    }

    private void closeSftp() {
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
        logger.debug("SFTP connection is closed: [{}]", dest);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("dest", dest).
                append("session", session).
                append("sftp", sftp).
                append("processingPath", processingPath).
                append("writtenPath", writtenPath).
                append("out", out).
                append("state", state).
                toString();
    }
}
