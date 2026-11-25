package com.game.func;

import com.game.server.GameRoll;

public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
        GameRoll.COMMAND.commandError(message);
    }
}
