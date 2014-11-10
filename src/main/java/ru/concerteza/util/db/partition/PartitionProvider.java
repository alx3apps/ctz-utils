package ru.concerteza.util.db.partition;

import java.util.Collection;
import java.util.List;

/**
 * User: alexkasko
 * Date: 11/10/14
 */
public interface PartitionProvider {
    Collection<String> loadPartitions(String prefix);

    void createPartition(String prefix, String postfix);
}
