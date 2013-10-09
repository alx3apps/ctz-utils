package ru.concerteza.util.net.sftp;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;

/**
 * {@code HostKeyRepository} implementation that does not check anything
 *
 * @author alexkasko
 * Date: 10/5/13
 */
public class SftpLenientKnownHosts implements HostKeyRepository {
    @Override
    public int check(String host, byte[] key) {
        return OK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(HostKey hostkey, UserInfo ui) {
        throw new UnsupportedOperationException("add");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String host, String type) {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String host, String type, byte[] key) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public String getKnownHostsRepositoryID() {
        throw new UnsupportedOperationException("getKnownHostsRepositoryID");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HostKey[] getHostKey() {
        throw new UnsupportedOperationException("getHostKey");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HostKey[] getHostKey(String host, String type) {
        throw new UnsupportedOperationException("getHostKey");
    }
}
