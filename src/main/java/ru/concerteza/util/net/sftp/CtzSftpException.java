package ru.concerteza.util.net.sftp;

/**
 * Exception class for SFTP-related errors
 *
 * @author alexkasko
 * Date: 4/19/13
 */
public class CtzSftpException extends RuntimeException {
    private static final long serialVersionUID = -2221257236826937352L;

    /**
     * {@inheritDoc}
     */
    public CtzSftpException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public CtzSftpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public CtzSftpException(String message) {
        super(message);
    }
}
