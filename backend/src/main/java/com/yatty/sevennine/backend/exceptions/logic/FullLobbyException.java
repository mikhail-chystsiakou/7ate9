package com.yatty.sevennine.backend.exceptions.logic;

import com.yatty.sevennine.backend.exceptions.SevenNineException;
import com.yatty.sevennine.backend.model.Game;
import org.slf4j.helpers.MessageFormatter;

public class FullLobbyException extends SevenNineException {
    public FullLobbyException(Game game) {
        super(MessageFormatter.format(
                "Game is full. Game id: {}, players num: {}",
                game.getId(),
                game.getExpectedPlayersNum()
        ).toString());
    }
}
