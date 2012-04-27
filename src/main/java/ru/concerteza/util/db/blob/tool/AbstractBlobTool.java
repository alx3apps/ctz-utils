package ru.concerteza.util.db.blob.tool;

import org.apache.commons.io.IOUtils;
import ru.concerteza.util.db.blob.BlobException;
import ru.concerteza.util.db.blob.DetachedBlob;
import ru.concerteza.util.db.blob.ReadableBlob;
import ru.concerteza.util.db.blob.WritableBlob;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * User: alexey
 * Date: 4/13/12
 */
public abstract class AbstractBlobTool implements BlobTool {
    protected final DataSource dataSource;
    private final Compressor compressor;

    protected AbstractBlobTool(DataSource dataSource, Compressor compressor) {
        this.dataSource = dataSource;
        this.compressor = compressor;
    }

    @Override
    public WritableBlob create() {
        try {
            Pair<Long, OutputStream> pair = createInternal();
            OutputStream out = compressor.wrapCompress(pair.getSecond());
            return new WritableBlob(pair.getFirst(), out);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot create blob");
        }
    }

//    @Override
//    public long create(InputStream inputStream) throws SQLException {
//        try {
//            return createInternal(inputStream);
//        } catch (Exception e) {
//            throw new BlobException("Cannot create blob", e);
//        }
//    }

    @Override
    public ReadableBlob load(long id) {
        try {
            InputStream input = loadInternal(id);
            InputStream wrapped = compressor.wrapDecompress(input);
            return new ReadableBlob(id, wrapped);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot load blob, id: {}", id);
        }
    }

    @Override
    public DetachedBlob detach(long id) {
        InputStream input = null;
        try {
            input = loadInternal(id);
            byte[] compressedData = IOUtils.toByteArray(input);
            return new DetachedBlob(id, compressedData, compressor);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot detach blob, id: {}", id);
        } finally {
            closeQuietly(input);
        }
    }

    @Override
    public void delete(long id) {
        try {
            deleteInternal(id);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot delete blob, id: {}", id);
        }
    }

    protected abstract Pair<Long, OutputStream> createInternal() throws Exception;

    // override for RDBMS that doesn't not support OutputStream blobs
//    protected long createInternal(InputStream inputStream) throws Exception {
//        OutputStream blobStream = null;
//        try {
//            Pair<Long, OutputStream> pair = createInternal();
//            blobStream = pair.getSecond();
//            IOUtils.copyLarge(inputStream, blobStream);
//            return pair.getFirst();
//        } finally {
//            IOUtils.closeQuietly(blobStream);
//        }
//    }

    protected abstract InputStream loadInternal(long id) throws Exception;

    protected abstract void deleteInternal(long id) throws Exception;
}
