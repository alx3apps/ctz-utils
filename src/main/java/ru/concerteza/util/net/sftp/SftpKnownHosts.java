package ru.concerteza.util.net.sftp;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.string.function.LowerStringFunction;

import java.util.List;
import java.util.Locale;

import static ru.concerteza.util.crypto.CtzHashUtils.md5Digest;

/**
 * {@code HostKeyRepository} implementation that checks predefined host fingerprints
 *
 * @author alexkasko
 * Date: 2/11/13
 */
public class SftpKnownHosts implements HostKeyRepository {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final ImmutableSet<String> set;

    /**
     * single fingerprint constructor
     *
     * @param fingerprint remote host fingerprint
     */
    public SftpKnownHosts(String fingerprint) {
        this.set = ImmutableSet.of(fingerprint.toLowerCase());
    }

    /**
     * Multiple fingerprints constructor
     *
     * @param fingerprintList list of remote hosts fingerprints
     */
    public SftpKnownHosts(List<String> fingerprintList) {
        this.set = ImmutableSet.copyOf(Lists.transform(fingerprintList, LowerStringFunction.INSTANCE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int check(String host, byte[] remoteKey) {
        // maybe add sha1 fingerprint test no fail
        String remoteFingerprint = md5Fingerprint(remoteKey);
        boolean res = set.contains(remoteFingerprint);
        if (!res) {
            logger.warn("Error on fingerprint check, known: [{}], tested: [{}]", set, remoteFingerprint);
            return NOT_INCLUDED;
        } else {
            return OK;
        }
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

    private static String md5Fingerprint(byte[] key) {
        String md5 = md5Digest(key);
        StringBuilder sb = new StringBuilder();
        // inject semicolons
        // before df19d0ecdcb74ea190aa17c82a7d128e
        // after df:19:d0:ec:dc:b7:4e:a1:90:aa:17:c8:2a:7d:12:8e
        for (int i = 0; i < md5.length(); i += 2) {
            sb.append(md5.charAt(i));
            sb.append(md5.charAt(i + 1));
            if (i + 2 < md5.length()) sb.append(":");
        }
        return sb.toString().toLowerCase(Locale.US);
    }
}
