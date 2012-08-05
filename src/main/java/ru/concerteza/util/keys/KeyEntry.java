package ru.concerteza.util.keys;

/**
 * Basic interface for join and group operations
 *
 * @author  alexey
 * Date: 7/13/12
 * @see KeyOperations
 */
public interface KeyEntry {
    /**
     * Key, will be used in join and group operations
     *
     * @return non null key
     */
    String key();
}
