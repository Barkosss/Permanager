package common.repositories;

import common.commands.BaseCommand;

import java.util.Map;

public class CommandRepository {
    Map<String, BaseCommand> commands;

    public CommandRepository(Map<String, BaseCommand> commands) {
        this.commands = commands;
    }

    public Map<String, BaseCommand> getCommands() { return commands; }

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    public BaseCommand getCommand(String command) {
        return commands.get(command);
    }
}
