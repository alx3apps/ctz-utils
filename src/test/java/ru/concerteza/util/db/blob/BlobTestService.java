package ru.concerteza.util.db.blob;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concerteza.util.db.blob.tool.BlobTool;

import javax.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.Assert.assertArrayEquals;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 4/25/12
 */
public interface BlobTestService {
    long create() throws IOException;

    void read(long id) throws IOException;

    void detach(long id) throws IOException;

    void delete(long id);
}

@Service
class BlobTestServiceImpl implements BlobTestService {
    private static final byte[] DATA = random(1024).getBytes(UTF8_CHARSET);

    @Inject
    private BlobTool blobTool;

    @Override
    @Transactional
    public long create() throws IOException {
        InputStream is = new ByteArrayInputStream(DATA);
        WritableBlob blob = blobTool.create();
        copyLarge(is, blob.getOutputStream());
        blob.getOutputStream().close();
        return blob.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public void read(long id) throws IOException {
        ReadableBlob blob = blobTool.load(id);
        byte[] readData = toByteArray(blob.getInputStream());
        blob.close();
        assertArrayEquals("Read fail", DATA, readData);
    }

    @Override
    @Transactional(readOnly = true)
    public void detach(long id) throws IOException {
        DetachedBlob blob = blobTool.detach(id);
        byte[] readData = toByteArray(blob.getInputStream());
        assertArrayEquals("Detached fail", DATA, readData);
    }

    @Override
    @Transactional
    public void delete(long id) {
        blobTool.delete(id);
    }
}
