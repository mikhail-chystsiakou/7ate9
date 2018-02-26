package com.yatty.sevennine.backend.exceptions.io;

public class MessageSendingException extends RuntimeException {
    public MessageSendingException() {
        super();
    }

    public MessageSendingException(String message) {
        super(message);
    }

    public MessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSendingException(Throwable cause) {
        super(cause);
    }

    protected MessageSendingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
