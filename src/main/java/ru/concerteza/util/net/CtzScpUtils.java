package ru.concerteza.util.net;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.regex.Pattern;
import org.apache.commons.lang3.builder.*;
import org.slf4j.Logger;
import org.slf4j.*;
import ru.concerteza.util.namedregex.*;

import static java.lang.String.format;
import static ru.concerteza.util.crypto.CtzHashUtils.md5Digest;

/**
 * User: alexey
 * Date: 4/1/11
 */
// todo: document me
// source:
//  - http://www.jcraft.com/jsch/examples/ScpFrom.java
//  - http://kickjava.com/src/org/apache/tools/ant/taskdefs/optional/ssh/ScpFromMessage.java.htm
//  - https://github.com/shikhar/sshj/blob/c56f9997f47d30933a502d067b25687410bbd7fc/src/main/java/net/schmizz/sshj/xfer/scp/SCPDownloadClient.java
// info: http://blogs.sun.com/janp/entry/how_the_scp_protocol_works
public class CtzScpUtils {
    private static final Logger logger = LoggerFactory.getLogger(CtzScpUtils.class);

    // C0644 299 filename
    private static final String LENGTH_GROUP = "length";
    private static final String FILENAME_GROUP = "filename";
    private static final NamedPattern C0664_PATTERN = NamedPattern.compile("^C0644\\s(?<" + LENGTH_GROUP + ">\\d+)\\s(?<" + FILENAME_GROUP + ">.+\n)$", Pattern.CASE_INSENSITIVE);
    private static final String MESSAGE_GROUP = "message";
    private static final NamedPattern ERROR1_PATTERN = NamedPattern.compile("^1\\s(?<" + MESSAGE_GROUP + ">.*)\n$");
    private static final NamedPattern ERROR2_PATTERN = NamedPattern.compile("^2\\s(?<" + MESSAGE_GROUP + ">.*)\n$");


    /*
        $ scp -vvv -P 2223 amber@192.177.0.1:/u01/siebel/amber/siebel_task_1299142567576/AmberIntegrationTestAtt1.txt .
        debug1: Sending command: scp -v -f /u01/siebel/amber/siebel_task_1299142567576/AmberIntegrationTestAtt1.txt
        debug2: channel 0: request exec confirm 1
        debug2: fd 3 setting TCP_NODELAY
        debug2: callback done
        debug2: channel 0: open confirm rwindow 0 rmax 32768
        debug1: Remote: Channel 0 set: LANG=en_US.UTF-8
        debug2: channel 0: rcvd adjust 131072
        debug2: channel_input_confirm: type 99 id 0
        debug2: exec request accepted on channel 0
        debug2: channel 0: rcvd ext data 58
        Sending file modes: C0644 11 AmberIntegrationTestAtt1.txt
        debug2: channel 0: written 58 to efd 6
        Sink: C0644 11 AmberIntegrationTestAtt1.txt
        AmberIntegrationTestAtt1.txt                                                                   100%   11     0.0KB/s   00:00
        debug2: channel 0: rcvd eof
        debug2: channel 0: output open -> drain
        debug2: channel 0: obuf empty
        debug2: channel 0: close_write
        debug2: channel 0: output drain -> closed
        debug1: client_input_channel_req: channel 0 rtype exit-status reply 0
     */
    // to get fingerprint use on of these commands:
    // - ssh-keygen -l -F remote_host
    // - ssh -o "HostKeyAlias random_string" remote_host
    public static InputStream readFile(String host, int port, String hostFingerprint, String user, String password, String remotePath) throws IOException {
        try {
            logger.debug("Initializing SSH connect, host: {}, port: {}, user: {}, path: {}", new Object[]{host, port, user, remotePath});
            JSch jsch = new JSch();
            HostKeyRepository hkr = new SingleKnownHost(hostFingerprint);
            jsch.setHostKeyRepository(hkr);
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
            session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
            session.setConfig("compression_level", "9");
            session.connect();
            logger.debug("Connection established");
            String command = "scp -f " + remotePath;
            logger.debug("Executing remote SCP command: {}", command);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            logger.debug("SCP session established");
            logger.debug("Sending ACK...");
            sendAck(out);
            logger.debug("Reading file info...");
            ScpFileInfo info = readFileInfo(in);
            logger.debug("Info read: {}", info);
            logger.debug("Sending ACK...");
            sendAck(out);
            logger.debug("Returns remote stream to caller");
            return new RemoteScpFileInputStream(in, out, session, info.length);
        } catch (JSchException e) {
            throw new IOException(e);
        }
    }

    private static void checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        switch (b) {
            case 0: break;
            case 1:
            case 2:
                String remoteError = readLine(in);
                throw new IOException(format("Remote error: %s", remoteError));
            case -1: throw new IOException(format("Got EOF instead of ACK"));
            default:
                throw new IOException(format("Ack must be in (0, 1, 2, -1), but was: %d", b));
        }
    }

    private static String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = in.read();
            if (c == -1) throw new IOException(format("Expects line ends with '\\n', but EOF found, buffer: %s", sb));
            sb.append((char) c);
        }
        while (c != '\n');
        return sb.toString();
    }

    // read C0644 299 filepath
    private static ScpFileInfo readFileInfo(InputStream in) throws IOException {
        String str;
        NamedMatcher matcher;
        while(true) {
            str = readLine(in);
            matcher = C0664_PATTERN.matcher(str);
            if (matcher.matches()) {
                break; // C0664 received, only correct while exit
            } else { // check whether error received
                NamedMatcher error1Matcher = ERROR1_PATTERN.matcher(str);
                NamedMatcher error2Matcher = ERROR2_PATTERN.matcher(str);
                if(error1Matcher.matches()) {
                    String message = error1Matcher.group(MESSAGE_GROUP);
                    throw new IOException(format("Remote error: %s", message));
                }
                else if(error2Matcher.matches()) {
                    String message = error2Matcher.group(MESSAGE_GROUP);
                    throw new IOException(format("Remote error: %s", message));
                } else {
                    logger.warn("Awaiting C0664 but received: '{}', continueing...", str);
                }
            }
        }
        if(2 != matcher.groupCount()) throw new IOException(format(
                "Invalid groups count matched for string: '%s', must be: %d, but was: %d, regex: %s", str, 2, matcher.groupCount(), C0664_PATTERN.toString()));
        long length = Long.parseLong(matcher.group(LENGTH_GROUP));
        String path = matcher.group(FILENAME_GROUP);
        return new ScpFileInfo(length, path);
    }

    // send '\0'
    private static void sendAck(OutputStream out) throws IOException {
        out.write(0);
        out.flush();
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
        return sb.toString();
    }

    // not threadsafe
    static class RemoteScpFileInputStream extends InputStream {
        private final Logger logger = LoggerFactory.getLogger(getClass());
        // already buffered
        final private InputStream stream;
        final private OutputStream outputStream;
        final Session session;
        final private long length;
        private long bytesAlreadyRead;
        private boolean open;

        RemoteScpFileInputStream(InputStream stream, OutputStream outputStream, Session session, long length) {
            this.stream = stream;
            this.outputStream = outputStream;
            this.session = session;
            this.length = length;
            this.bytesAlreadyRead = 0;
            open = true;
        }

        @Override
        public int read() throws IOException {
            if(logger.isTraceEnabled()) logger.trace("Reading from stream, bytesAlreadyRead: {}, total length: {}", bytesAlreadyRead, length);
            if (bytesAlreadyRead < length) {
                int res = stream.read();
                bytesAlreadyRead += 1;
                return res;
            } else {
                if (open) {
                    logger.debug("Checking remote ACK of successfull copy...");
                    checkAck(stream);
                    logger.debug("Remote ACK received");
                    close();
                }
                return -1;
            }
        }

        @Override
        // added for compatibility, not used now
        public int available() throws IOException {
            long res = length - bytesAlreadyRead;
            return res <= Integer.MAX_VALUE ? (int)res : Integer.MAX_VALUE;
        }

        public long length() {
            return length;
        }

        @Override
        // will be called automatically on successful copy
        public void close() throws IOException {
            if (open) { // do it only once
                logger.debug("Closing SSH session...");
                open = false;
                sendAck(outputStream);
                session.disconnect();
                logger.debug("Session closed successfully");
            }
        }
    }

    private static class ScpFileInfo {
        final long length;
        final String filename;

        private ScpFileInfo(long length, String path) {
            this.length = length;
            this.filename = path;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                    append("length", length).
                    append("filename", filename).
                    toString();
        }
    }

    private static class SingleKnownHost implements HostKeyRepository {
        private final String fingerprint;

        private SingleKnownHost(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        @Override
        public int check(String host, byte[] remoteKey) {
            // maybe add sha1 fingerprint test no fail
            String remoteFingerprint = md5Fingerprint(remoteKey);
            boolean res = fingerprint.equalsIgnoreCase(remoteFingerprint);
            if(!res) {
                logger.warn("Error on fingerprint check, known: {}, tested: {}", fingerprint, remoteFingerprint);
                return NOT_INCLUDED;
            } else {
                return OK;
            }
        }

        // don't need these methods
        @Override
        public void add(HostKey hostkey, UserInfo ui) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(String host, String type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(String host, String type, byte[] key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getKnownHostsRepositoryID() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HostKey[] getHostKey() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HostKey[] getHostKey(String host, String type) {
            throw new UnsupportedOperationException();
        }
    }
}

