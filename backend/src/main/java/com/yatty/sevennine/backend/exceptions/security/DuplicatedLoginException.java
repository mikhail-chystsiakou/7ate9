package com.yatty.sevennine.backend.exceptions.security;

import com.yatty.sevennine.backend.exceptions.SevenNineException;
import com.yatty.sevennine.backend.model.Game;
import com.yatty.sevennine.backend.model.LoginedUser;

/**
 * Insert description vere
 *
 * @author Mike
 * @version 04/14/2018
 */
public class DuplicatedLoginException extends SevenNineException {
    public DuplicatedLoginException(String name) {
        shortDescription = "Name '" + name + "' is already used.";
    }
}