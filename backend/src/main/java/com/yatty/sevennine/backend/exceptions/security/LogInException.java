package com.yatty.sevennine.backend.exceptions.security;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class LogInException extends SevenNineException {
    public LogInException(String name) {
        shortDescription = "Wrong credentials";
        additionalInfo = "User '" + name + "' sent wrong credentials";
    }
}