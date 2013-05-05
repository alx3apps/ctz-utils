package ru.concerteza.util.db.postgrescopy;

/**
 * Implementations must provide logic for actually copying data for single row into copy buffer
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public interface PostgresCopyProvider {
    /**
     * Copies data for single row from source byte array into copy buffer
     * in <a href="http://www.postgresql.org/docs/9.2/static/sql-copy.html#AEN66756">binary format</>
     *
     * @param src source byte array
     * @param dest copy buffer
     * @return number of bytes, copied into dest buffer
     */
    int fillCopyBuf(byte[] src, byte[] dest);
}
