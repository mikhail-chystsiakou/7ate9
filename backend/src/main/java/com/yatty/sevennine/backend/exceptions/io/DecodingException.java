package com.yatty.sevennine.backend.exceptions.io;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class DecodingException extends SevenNineException {
    public DecodingException() {
        super();
    }

    public DecodingException(String message) {
        super(message);
    }

    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecodingException(Throwable cause) {
        super(cause);
    }
}
