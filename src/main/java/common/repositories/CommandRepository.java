package common.repositories;

import common.commands.BaseCommand;
import common.utils.LoggerHandler;

import java.util.Map;

public class CommandRepository {
    private final LoggerHandler logger = new LoggerHandler();
    private final Map<String, BaseCommand> commands;

    public CommandRepository(Map<String, BaseCommand> commands) {
        this.commands = commands;
    }

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    public BaseCommand getCommand(String command) {
        return commands.get(command);
    }
}
