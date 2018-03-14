package com.yatty.sevennine.backend.exceptions.security;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class UnauthorizedAccessException extends SevenNineException {
    public UnauthorizedAccessException(String authToken) {
        shortDescription = "Unauthorized access";
        additionalInfo = "Invalid auth token '" + authToken + "'";
    }
}