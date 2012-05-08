package ru.concerteza.util.archive;

import com.google.common.collect.Iterables;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
import static ru.concerteza.util.io.CtzIOUtils.iterateFiles;

/**
 * Utility to create <a href="http://en.wikipedia.org/wiki/Tar">TAR</a> archives from directories,
 * for fine tuning use {@link TarFunction} directly
 *
 * @author alexey,
 * Date: 5/4/12
 * @see TarFunction
 */
public class CtzTarUtils {

    /**
     * Convenient method to create <a href="http://en.wikipedia.org/wiki/Tar">TAR</a> archives from directories,
     * directory name will be preserved in archive.
     * @param dir root directory to make archive of
     * @param target <a href="http://commons.apache.org/compress/apidocs/org/apache/commons/compress/archivers/tar/TarArchiveOutputStream.html">TarArchiveOutputStream</a>
     * to write files to
     * @return number of entries written to archive
     * @throws RuntimeIOException IO error happened
     */
    public static int tarDirectory(File dir, File target) throws RuntimeIOException {
        if(!(dir.exists() && dir.isDirectory())) throw new RuntimeIOException("Directory doesn't exist: " + dir);
        TarArchiveOutputStream tarStream = null;
        try {
            OutputStream out = openOutputStream(target);
            tarStream = new TarArchiveOutputStream(out);
            tarStream.setLongFileMode(LONGFILE_GNU);
            TarFunction fun = new TarFunction(dir, tarStream);
            Iterable<File> children = iterateFiles(dir, TRUE, TRUE, true);
            Iterable<String> zipped = Iterables.transform(children, fun);
            return fireTransform(zipped);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(tarStream);
        }
    }
}
