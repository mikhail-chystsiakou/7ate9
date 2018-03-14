package com.yatty.sevennine.backend.exceptions.logic;

import com.yatty.sevennine.backend.exceptions.SevenNineException;

public class PlayerNotFoundException extends SevenNineException {
    public PlayerNotFoundException(String authToken) {
        super("Player with authToken'" + authToken + "' not found");
    }
}
