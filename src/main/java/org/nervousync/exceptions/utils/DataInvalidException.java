package org.nervousync.exceptions.utils;

/**
 * The type Data invalid exception.
 */
public final class DataInvalidException extends RuntimeException {

    private static final long serialVersionUID = -2896313924690716673L;

    /**
     * Instantiates a new Data invalid exception.
     */
    public DataInvalidException() {
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param errorMessage the error message
     */
    public DataInvalidException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param e the e
     */
    public DataInvalidException(Exception e) {
        super(e);
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param errorMessage the error message
     * @param e            the e
     */
    public DataInvalidException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
