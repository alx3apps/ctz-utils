package ru.concerteza.util.poi;

/**
 * Specific exception for XLSX report errors
 *
 * @author alexkasko
 * Date: 6/1/13
 */
public class XlsxStreamReporterException extends RuntimeException {
    private static final long serialVersionUID = -8918376602309411789L;

    public XlsxStreamReporterException(String message) {
        super(message);
    }

    public XlsxStreamReporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public XlsxStreamReporterException(Throwable cause) {
        super(cause);
    }
}
