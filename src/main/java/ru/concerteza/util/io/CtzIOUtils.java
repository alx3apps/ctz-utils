package ru.concerteza.util.io;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.concurrency.CallableList;
import ru.concerteza.util.concurrency.CtzConcurrencyUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.FilenameUtils.separatorsToUnix;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.concurrency.CtzConcurrencyUtils.runnable;

/**
 * User: alexey
 * Date: Oct 30, 2010
 */
public class CtzIOUtils {
    private static final DeleteDirsOnExitList DELETE_DIRS_ON_EXIT_LIST = new DeleteDirsOnExitList();

    static {
        Runnable runnable = runnable(DELETE_DIRS_ON_EXIT_LIST);
        Thread thread = new Thread(runnable);
        Runtime.getRuntime().addShutdownHook(thread);
    }

    public static XMLEventReader closeQuietly(XMLEventReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (XMLStreamException ioe) {
            // ignore
        }
        return reader;
    }

    public static Connection closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // ignore
        }
        return conn;
    }

    public static Statement closeQuietly(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            // ignore
        }
        return stmt;
    }

    public static File codeSourceDir(Class<?> clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            File jarOrDir = new File(uri);
            return jarOrDir.isDirectory() ? jarOrDir : jarOrDir.getParentFile();
        } catch (Exception e) {
            throw new UnhandledException(e);
        }
    }

    public static File appendToFile(File file, String in, String encoding) throws RuntimeIOException {
        InputStream stream = null;
        try {
            stream = IOUtils.toInputStream(in, encoding);
            appendToFile(file, stream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return file;
    }

    public static File appendToFile(File file, InputStream in) throws RuntimeIOException {
        OutputStream stream = null;
        try {
            stream = openOutputStream(file, true);
            IOUtils.copy(in, stream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return file;
    }

    public static File mkdirs(File dir) throws RuntimeIOException {
        if(dir.exists()) {
            if(dir.isFile()) throw new RuntimeIOException(format("Cannot write to directory: '{}'", dir.getAbsolutePath()));
        } else {
            boolean res = dir.mkdirs();
            if(!res) throw new RuntimeIOException(format("Cannot create directory: '{}'", dir.getAbsolutePath()));
        }
        return dir;
    }

    public static File createTmpFile(Class<?> clazz) throws RuntimeIOException {
        try {
            File tmp = File.createTempFile(clazz.getName(), ".tmp");
            tmp.deleteOnExit();
            return tmp;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static File createTmpDir(Class<?> clazz) throws RuntimeIOException {
        try {
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            String baseName = format("{}_{}.tmp", clazz.getName(), currentTimeMillis());
            File tmp = new File(baseDir, baseName);
            boolean res = tmp.mkdirs();
            if (!res) throw new IOException(format("Cannot create directory: '{}'", tmp.getAbsolutePath()));
            DELETE_DIRS_ON_EXIT_LIST.add(tmp);
            return tmp;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static List<File> listFiles(File dir) throws RuntimeIOException {
        return listFiles(dir, TrueFileFilter.TRUE);
    }

    // idea from here http://techblog.sharpmind.de/?p=228
    public static List<File> listFiles(File dir, IOFileFilter filter) throws RuntimeIOException {
        File[] files = dir.listFiles();
        if(null == files) throw new RuntimeIOException("Cannot list directory: " + dir.getAbsolutePath());
        // return itself for empty directory
        if(0 == files.length) return ImmutableList.of(dir);

        // process non empty dir
        ImmutableList.Builder<File> builder = new ImmutableList.Builder<File>();
        for (File fi : files) {
            if (filter.accept(fi)) {
                if(fi.isFile()) builder.add(fi);
                else if(fi.isDirectory())  builder.addAll(listFiles(fi, filter));
            }
        }
        return builder.build();
    }


    private static class DeleteDirsOnExitList extends CallableList<File> {
        private final Object lock = new Object();

        public DeleteDirsOnExitList add(File file) {
            synchronized(lock) {
                super.add(new DeleteDirCallable(file));
                return this;
            }
        }

        private class DeleteDirCallable implements Callable<File> {
            private final File dir;

            private DeleteDirCallable(File dir) {
                this.dir = dir;
            }

            @Override
            public File call() throws Exception {
                FileUtils.deleteDirectory(dir);
                return dir;
            }
        }
    }
}
