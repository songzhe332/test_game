package com.game.func;

import com.game.server.GameRoll;

public class CommandException extends RuntimeException {
    public CommandException(String message,Command command){
        super(message);
        command.commandError(message);
    }
    public CommandException(String message,Command command,String name,double health) {
        this(message, command);
        GameRoll.getInstance().getArchive().showEntityArchive(name, health);
    }
}
