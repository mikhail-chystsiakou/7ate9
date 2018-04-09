package com.yatty.sevennine.backend.exceptions.io;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class PropertiesLoadingException extends SevenNineException {
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
}