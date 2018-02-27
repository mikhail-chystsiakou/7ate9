package com.yatty.sevennine.backend.exceptions;

public class SevenNineException extends RuntimeException {
    public SevenNineException() {
        super();
    }

    public SevenNineException(String message) {
        super(message);
    }

    public SevenNineException(String message, Throwable cause) {
        super(message, cause);
    }

    public SevenNineException(Throwable cause) {
        super(cause);
    }

    protected SevenNineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
