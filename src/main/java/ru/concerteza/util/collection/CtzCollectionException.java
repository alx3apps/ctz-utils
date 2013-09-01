package ru.concerteza.util.collection;

/**
 * Specific exception for collection-related classes
 *
 * @author alexkasko
 * Date: 8/31/13
 */
public class CtzCollectionException extends RuntimeException {
    private static final long serialVersionUID = 3640693605726209499L;

    /**
     * {@inheritDoc}
     */
    public CtzCollectionException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public CtzCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public CtzCollectionException(Throwable cause) {
        super(cause);
    }
}
