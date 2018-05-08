package com.yatty.sevennine.backend.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class UserTest {
    @Test
    public void testPlayerIdSaltRemoval() {
        String playerId = "mike";
        String saltedPlayerId = playerId + User.generateLoginSalt();
        String samePlayerId = User.removeLoginSalt(saltedPlayerId);
        assertEquals(playerId, samePlayerId);
    }
}
