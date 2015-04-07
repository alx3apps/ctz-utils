package ru.concerteza.util.net.sftp;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Destination settings for writing files to remote server over SFTP
 *
 * @author alexkasko
 * Date: 6/30/13
 */
public class SftpDestination implements Serializable {
    private static final long serialVersionUID = 7680058149228262809L;

    private String hostname;
    private int port;
    private String fingerprint;
    private String username;
    private String password;
    private String processingDirectory;
    private String writtenDirectory;

    /**
     * Hostname accessor
     *
     * @return hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Port accessor
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Fingerprint accessor
     *
     * @return fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * Username accessor
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Password accessor
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Processing directory accessor
     *
     * @return processing directory
     */
    public String getProcessingDirectory() {
        return processingDirectory;
    }

    /**
     * Written directory accessor
     *
     * @return written directory
     */
    public String getWrittenDirectory() {
        return writtenDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("hostname", hostname).
                append("port", port).
                append("fingerprint", fingerprint).
                append("username", username).
                append("processingDirectory", processingDirectory).
                append("writtenDirectory", writtenDirectory).
                toString();
    }
}
