package ru.concerteza.util.db.blob;

import org.apache.commons.dbcp.DelegatingConnection;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * User: alexey
 * Date: 2/4/11
 */
public class CtzPostgreBlobUtils {

    // return blob stream to write to, it must be closed by the caller
    public static WriteableBlob createLob(Connection conn, boolean compress) throws SQLException {
        try {
            return doCreateLob(conn, compress);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    // result stream must be closed by the caller
    public static ReadableBlob loadLob(Connection conn, long oid, boolean uncompress) throws SQLException {
        try {
            return doLoadLob(conn, oid, uncompress);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    public static void deleteLob(Connection conn, long oid) throws SQLException {
        LargeObjectManager manager = extractManager(conn);
        manager.delete(oid);
    }

    private static WriteableBlob doCreateLob(Connection conn, boolean compress) throws SQLException, IOException {
        LargeObjectManager manager = extractManager(conn);
        long oid = manager.createLO(LargeObjectManager.READWRITE);
        LargeObject lob = manager.open(oid, LargeObjectManager.WRITE);
        OutputStream stream = compress ? new GZIPOutputStream(lob.getOutputStream()) : lob.getOutputStream();
        return new WriteableBlob(oid, compress, stream);
    }

    private static ReadableBlob doLoadLob(Connection conn, long oid, boolean uncompress) throws SQLException, IOException {
        LargeObjectManager manager = extractManager(conn);
        LargeObject lob = manager.open(oid, LargeObjectManager.READ);
        InputStream stream = uncompress ? new GZIPInputStream(lob.getInputStream()) : lob.getInputStream();
        return new ReadableBlob(oid, !uncompress, stream);
    }

    private static LargeObjectManager extractManager(Connection conn) throws SQLException {
        final PGConnection pgConn;
        if (conn instanceof PGConnection) {
            pgConn = (PGConnection) conn;
        } else if (conn instanceof DelegatingConnection) {
            DelegatingConnection wrapped = (DelegatingConnection) conn;
            pgConn = (PGConnection) wrapped.getInnermostDelegate();
        } else {
            throw new SQLException("Cannot extract pg blob api from connection: " + conn);
        }
        return pgConn.getLargeObjectAPI();
    }
}

