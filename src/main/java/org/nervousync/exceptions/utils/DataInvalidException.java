package org.nervousync.exceptions.utils;

public final class DataInvalidException extends RuntimeException {

    private static final long serialVersionUID = -2896313924690716673L;

    public DataInvalidException() {
    }

    public DataInvalidException(String errorMessage) {
        super(errorMessage);
    }

    public DataInvalidException(Exception e) {
        super(e);
    }

    public DataInvalidException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
