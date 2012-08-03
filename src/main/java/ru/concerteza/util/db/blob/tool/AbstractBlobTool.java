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
 * BLOB tool supertype
 *
 * @author alexey
 * Date: 4/13/12
 * @see BlobTool
 */
public abstract class AbstractBlobTool implements BlobTool {
    protected final DataSource dataSource;
    protected final Compressor compressor;

    /**
     * @param dataSource data source
     * @param compressor compressor
     */
    protected AbstractBlobTool(DataSource dataSource, Compressor compressor) {
        this.dataSource = dataSource;
        this.compressor = compressor;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadableBlob load(long id) {
        try {
            InputStream input = loadInternal(id);
            InputStream wrapped = compressor.wrapDecompress(input);
            return new ReadableBlob(id, wrapped);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot load blob, id: '{}'", id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DetachedBlob detach(long id) {
        InputStream input = null;
        try {
            input = loadInternal(id);
            byte[] compressedData = IOUtils.toByteArray(input);
            return new DetachedBlob(id, compressedData, compressor);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot detach blob, id: '{}'", id);
        } finally {
            closeQuietly(input);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(long id) {
        try {
            deleteInternal(id);
        } catch (Exception e) {
            throw new BlobException(e, "Cannot delete blob, id: '{}'", id);
        }
    }

    /**
     * Must create BLOB in database and return its ID and input stream
     *
     * @return BLOB ID and input stream
     * @throws Exception
     */
    protected abstract Pair<Long, OutputStream> createInternal() throws Exception;

    /**
     * Must open BLOB in database and return its input stream
     *
     * @param id BLOB ID
     * @return BLOB input stream
     * @throws Exception
     */
    protected abstract InputStream loadInternal(long id) throws Exception;

    /**
     * Must delete BLOB in database
     *
     * @param id BLOB ID
     * @throws Exception
     */
    protected abstract void deleteInternal(long id) throws Exception;
}
