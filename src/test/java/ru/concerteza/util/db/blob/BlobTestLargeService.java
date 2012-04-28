package ru.concerteza.util.db.blob;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concerteza.util.crypto.SHA1InputStream;
import ru.concerteza.util.db.blob.tool.BlobTool;

import javax.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.junit.Assert.assertArrayEquals;
import static ru.concerteza.util.CtzConstants.UTF8_CHARSET;

/**
 * User: alexey
 * Date: 4/25/12
 */
public interface BlobTestLargeService {
    long create(File file) throws IOException;

    String readSha1(long id) throws IOException;

    void delete(long id);
}

@Service
class BlobTestLargeServiceImpl implements BlobTestLargeService {

    @Inject
    private BlobTool blobTool;

    @Override
    @Transactional
    public long create(File file) throws IOException {
        InputStream is = FileUtils.openInputStream(file);
        WritableBlob blob = blobTool.create();
        copyLarge(is, blob.getOutputStream());
        blob.getOutputStream().close();
        return blob.getId();
    }

    @Override
    @Transactional
    public String readSha1(long id) throws IOException {
        ReadableBlob blob = blobTool.load(id);
        SHA1InputStream sha1stream = new SHA1InputStream(blob.getInputStream());
        IOUtils.copyLarge(sha1stream, new NullOutputStream());
        sha1stream.close();
        return sha1stream.digest();
    }

    @Override
    @Transactional
    public void delete(long id) {
        blobTool.delete(id);
    }
}
