package ru.concerteza.util.net.sftp;

import com.google.common.base.Function;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.io.finishable.Finishable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Remote SFTP file that will be moved to 'success' or 'error' directory after processing
 *
 * @author alexkasko
 * Date: 4/19/13
 */
@Deprecated // use SftpDirIterator
public class SftpFile implements Finishable<Void, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(SftpFile.class);

    private final ChannelSftp sftp;
    private final String name;
    private final String path;
    private final String successPath;
    private final String errorPath;
    private InputStream is;

    /**
     * Constructor
     *
     * @param sftp SFTP channel, won't be closed
     * @param name file name
     * @param readDir dir to read file from
     * @param successDir dir to move successfully processed file into
     * @param errorDir dir to move errored file into
     */
    SftpFile(ChannelSftp sftp, String name, String readDir, String successDir, String errorDir) {
        checkNotNull(sftp, "Provided sftp is null");
        checkState(!sftp.isClosed() && sftp.isConnected(), "Provided sftp is not connected");
        checkArgument(isNotBlank(name), "Provided name is blank");
        checkArgument(isNotBlank(readDir), "Provided readDir is blank");
        checkArgument(isNotBlank(successDir), "Provided successDir is blank");
        checkArgument(isNotBlank(errorDir), "Provided errorDir is blank");
        this.sftp = sftp;
        this.name = name;
        this.path = readDir + "/" + name;
        this.successPath = successDir + "/" + name;
        this.errorPath = errorDir + "/" + name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish(Function<Void, Boolean> fun) {
        closeQuietly(is);
        if(fun.apply(null)) {
            try {
                logger.debug("Moving file to 'processed': [{}]", name);
                sftp.rename(path, successPath);
            } catch (SftpException e) {
                throw new CtzSftpException("Error moving file: [" + this + "] to success dir", e);
            }
        } else {
            try {
                logger.warn("Moving file to 'error': [{}]", name);
                sftp.rename(path, errorPath);
            } catch (Exception e) {
                logger.warn("Error moving file: [" + this + "] to error dir", e);
            }
        }
    }

    /**
     * Opens and returns input stream to remote file.
     * Reference to stream will be retained in this object
     * and stream will be closed on {@link #finish(com.google.common.base.Function)} call
     *
     * @return input stream to remote file
     */
    public InputStream getStream() {
        if (null != this.is) throw new IllegalStateException("Input stream is already open for file: [" + path + "]");
        try {
            logger.debug("Opening snapped stream for file: [{}]", name);
            this.is = sftp.get(path);
            return this.is;
        } catch (SftpException e) {
            throw new CtzSftpException(e);
        }
    }

    /**
     * Returns file name
     *
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("name", name).
                append("path", path).
                append("successPath", successPath).
                append("errorPath", errorPath).
                append("is", is).
                toString();
    }
}

