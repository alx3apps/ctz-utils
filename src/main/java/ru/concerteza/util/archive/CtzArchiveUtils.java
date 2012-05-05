package ru.concerteza.util.archive;

import com.google.common.collect.Collections2;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.FilenameUtils.separatorsToUnix;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;

/**
 * User: alexey
 * Date: 5/4/12
 */
public class CtzArchiveUtils {

    public static int zipDirectory(File dir, File target) throws RuntimeIOException {
        ZipOutputStream zipStream = null;
        try {
            OutputStream out = openOutputStream(target);
            zipStream = new ZipOutputStream(out);
            return zipDirectory(dir, zipStream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
    }

    public static int zipDirectory(File dir, ZipOutputStream zipStream) {
        return zipDirectory(dir, zipStream, TrueFileFilter.TRUE);
    }

    public static int zipDirectory(File dir, ZipOutputStream zipStream, IOFileFilter filter) {
        checkArgument(dir.exists() && dir.isDirectory(), "Directory doesn't exist: '%s'", dir);
        ZipFunction fun = new ZipFunction(dir, zipStream);
        Collection<File> children = FileUtils.listFiles(dir, filter, filter);
        Collection<String> zipped = Collections2.transform(children, fun);
        return fireTransform(zipped);
    }

    public static int tarDirectory(File dir, File target) throws RuntimeIOException {
        TarArchiveOutputStream tarStream = null;
        try {
            OutputStream out = openOutputStream(target);
            tarStream = new TarArchiveOutputStream(out);
            tarStream.setLongFileMode(LONGFILE_GNU);
            return tarDirectory(dir, tarStream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(tarStream);
        }
    }

    public static int tarDirectory(File dir, TarArchiveOutputStream tarStream) {
        return tarDirectory(dir, tarStream, TrueFileFilter.TRUE);
    }

    public static int tarDirectory(File dir, TarArchiveOutputStream tarStream, IOFileFilter filter) {
        checkArgument(dir.exists() && dir.isDirectory(), "Directory doesn't exist: '%s'", dir);
        TarFunction fun = new TarFunction(dir, tarStream);
        Collection<File> children = FileUtils.listFiles(dir, filter, filter);
        Collection<String> zipped = Collections2.transform(children, fun);
        return fireTransform(zipped);
    }

    static String relative(String parent, File child) {
        String path = separatorsToUnix(child.getPath());
        // check whether actual child
        checkArgument(parent.equals(path.substring(0, parent.length())));
        return path.substring(parent.length() + 1);
    }
}
