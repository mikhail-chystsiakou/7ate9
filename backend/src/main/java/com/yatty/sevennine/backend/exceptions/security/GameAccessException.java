package com.yatty.sevennine.backend.exceptions.security;

import com.yatty.sevennine.backend.exceptions.SevenNineException;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.LoginedUser;

/**
 * Occurs when user tries to make move for game without joining.
 */
public class GameAccessException extends SevenNineException {
    public GameAccessException(LoginedUser user, Game game) {
        shortDescription = "Unauthorized access";
        additionalInfo = "You are not joined the game '" + game.getName() + "'";
    }
}
