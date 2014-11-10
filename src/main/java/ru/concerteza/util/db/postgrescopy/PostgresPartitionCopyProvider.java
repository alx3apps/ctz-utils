package ru.concerteza.util.db.postgrescopy;

/**
 * User: alexkasko
 * Date: 11/10/14
 */
public interface PostgresPartitionCopyProvider extends PostgresCopyProvider {
    long date(byte[] packet);

    int maxSize();
}
