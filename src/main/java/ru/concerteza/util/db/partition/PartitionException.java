package ru.concerteza.util.db.partition;

/**
 * User: alexkasko
 * Date: 11/10/14
 */
public class PartitionException extends RuntimeException {
    private static final long serialVersionUID = -3624270376763484656L;

    public PartitionException(String message) {
        super(message);
    }

    public PartitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
