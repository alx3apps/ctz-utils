package ru.concerteza.util.archive;

import com.google.common.collect.Collections2;
import org.apache.commons.io.IOUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static ru.concerteza.util.math.CtzMathUtils.defaultInt;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
import static ru.concerteza.util.io.CtzIOUtils.listFiles;

/**
 * Utility to create <a href="http://en.wikipedia.org/wiki/Zip_%28file_format%29">ZIP</a> archives from directories,
 * for fine tuning use {@link ZipFunction} directly. Separated from {@link CtzTarUtils} to be available for client code
 * without <a href="http://commons.apache.org/compress/">Apache Commons Compress library</> dependency
 *
 * @author alexey,
 * Date: 5/4/12
 * @see ZipFunction
 */
public class CtzZipUtils {
    /**
     * Convenient method to create <a href="http://en.wikipedia.org/wiki/Zip_%28file_format%29">ZIP</a> archives from directories,
     * directory name will be preserved in archive.
     * @param dir root directory to make archive of
     * @param target <a href="http://docs.oracle.com/javase/6/docs/api/java/util/zip/ZipOutputStream.html">ZipOutputStream</a>
     * to write files to
     * @return number of entries written to archive
     * @throws RuntimeIOException IO error happened
     */
    public static int zipDirectory(File dir, File target) throws RuntimeIOException {
        if(!(dir.exists() && dir.isDirectory())) throw new RuntimeIOException("Directory doesn't exist: " + dir);
        ZipOutputStream zipStream = null;
        try {
            OutputStream out = openOutputStream(target);
            zipStream = new ZipOutputStream(out);
            ZipFunction fun = new ZipFunction(dir, zipStream);
            Collection<File> children = listFiles(dir, true);
            Collection<String> zipped = Collections2.transform(children, fun);
            return defaultInt(fireTransform(zipped));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
    }
}
