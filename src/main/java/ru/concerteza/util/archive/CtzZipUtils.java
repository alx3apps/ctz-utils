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
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
import static ru.concerteza.util.io.CtzIOUtils.listFiles;

/**
 * User: alexey
 * Date: 5/4/12
 */

// tar utils separated for using ZipUtils without commons-compress lib
public class CtzZipUtils {
    public static int zipDirectory(File dir, File target) throws RuntimeIOException {
        checkArgument(dir.exists() && dir.isDirectory(), "Directory doesn't exist: '%s'", dir);
        ZipOutputStream zipStream = null;
        try {
            OutputStream out = openOutputStream(target);
            zipStream = new ZipOutputStream(out);
            ZipFunction fun = new ZipFunction(dir, zipStream);
            Collection<File> children = listFiles(dir, true);
            Collection<String> zipped = Collections2.transform(children, fun);
            return fireTransform(zipped);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
    }
}
