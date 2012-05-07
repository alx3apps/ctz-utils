package ru.concerteza.util.archive;

import com.google.common.collect.Collections2;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
import static ru.concerteza.util.io.CtzIOUtils.listFiles;

/**
 * User: alexey
 * Date: 5/4/12
 */

public class CtzTarUtils {

    public static int tarDirectory(File dir, File target) throws RuntimeIOException {
        checkArgument(dir.exists() && dir.isDirectory(), "Directory doesn't exist: '%s'", dir);
        TarArchiveOutputStream tarStream = null;
        try {
            OutputStream out = openOutputStream(target);
            tarStream = new TarArchiveOutputStream(out);
            tarStream.setLongFileMode(LONGFILE_GNU);
            TarFunction fun = new TarFunction(dir, tarStream);
            Collection<File> children = listFiles(dir, true);
            Collection<String> zipped = Collections2.transform(children, fun);
            return fireTransform(zipped);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(tarStream);
        }
    }
}
