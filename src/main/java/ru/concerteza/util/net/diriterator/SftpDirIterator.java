package ru.concerteza.util.net.diriterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Charsets.UTF_8;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class SftpDirIterator extends BaseDirIterator<SftpFile> {
    private static final Logger logger = LoggerFactory.getLogger(SftpDirIterator.class);

    private Session session;
    private ChannelSftp sftp;

    public SftpDirIterator(String host, int port, String user, String password, DirIteratorPaths paths) {
       this(host, port, user, password, paths, Predicates.<String>alwaysTrue());
    }

    public SftpDirIterator(String host, int port, String user, String password, DirIteratorPaths paths, Predicate<String> filenameFilter) {
        super(host, port, user, password, paths, filenameFilter);
    }

    @Override
    protected SftpFile createFile(String name) {
        return new SftpFile(sftp, paths, name);
    }

    @Override
    @SuppressWarnings("unchecked") // SFTP API
    protected void openInternal() throws Exception {
        logger.debug("Accessing SFTP, host: [{}], paths: [{}]", host, paths);
        JSch jsch = new JSch();
        jsch.setHostKeyRepository(CredulousHostKeyRepository.INSTANCE);
        session = jsch.getSession(user, host, port);
        session.setPassword(password.getBytes(UTF_8));
        session.connect();
        sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        List<ChannelSftp.LsEntry> entries = sftp.ls(paths.getDir());
        List<String> names = Lists.transform(entries, NameFun.INSTANCE);
        Iterable<String> nodots = Iterables.filter(names, DotPredicate.INSTANCE);
        Iterable<String> filtered = Iterables.filter(nodots, filenameFilter);
        fileNames = Ordering.natural().immutableSortedCopy(filtered);
        logger.debug("Files list obtained: [{}]", fileNames);
    }

    @Override
    protected void closeInternal() {
        if (null != sftp) {
            try {
                sftp.disconnect();
            } catch (Exception e) {
                logger.warn("Error on SFTP disconnect, iter: [" + this + "]", e);
            }
        }
        if (null != session) {
            try {
                session.disconnect();
            } catch (Exception e) {
                logger.warn("Error on SFTP session disconnect, iter: [" + this + "]", e);
            }
        }
        logger.debug("SFTP connection to host: [{}] is closed", host);
    }

    protected enum NameFun implements Function<ChannelSftp.LsEntry, String> {
        INSTANCE;
        @Override
        public String apply(ChannelSftp.LsEntry input) {
            return input.getFilename();
        }
    }

    private enum CredulousHostKeyRepository implements HostKeyRepository {
        INSTANCE;

        @Override
        public int check(String host, byte[] key) {
            return OK;
        }

        @Override
        public void add(HostKey hostkey, UserInfo ui) {
        }

        @Override
        public void remove(String host, String type) {
        }

        @Override
        public void remove(String host, String type, byte[] key) {
        }

        @Override
        public String getKnownHostsRepositoryID() {
            return null;
        }

        @Override
        public HostKey[] getHostKey() {
            return new HostKey[0];
        }

        @Override
        public HostKey[] getHostKey(String host, String type) {
            return new HostKey[0];
        }
    }
}
