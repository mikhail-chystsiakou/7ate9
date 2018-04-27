package com.yatty.sevennine.backend.exceptions.data;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class DatabaseException extends SevenNineException {
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
