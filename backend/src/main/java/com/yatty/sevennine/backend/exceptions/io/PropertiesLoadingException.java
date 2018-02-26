package com.yatty.sevennine.backend.exceptions.io;

public class PropertiesLoadingException extends RuntimeException {
    public PropertiesLoadingException() {
        super();
    }

    public PropertiesLoadingException(String message) {
        super(message);
    }

    public PropertiesLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesLoadingException(Throwable cause) {
        super(cause);
    }

    protected PropertiesLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}