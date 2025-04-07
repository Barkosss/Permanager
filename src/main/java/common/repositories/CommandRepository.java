package common.repositories;

import common.commands.BaseCommand;
import common.utils.LoggerHandler;

import java.util.Map;

public class CommandRepository {
    Map<String, BaseCommand> commands;
    LoggerHandler logger = new LoggerHandler();

    public CommandRepository(Map<String, BaseCommand> commands) {
        this.commands = commands;
        logger.info(String.format("CommandRepository initialized with %s commands", commands.size()), true);
    }

    public Map<String, BaseCommand> getCommands() {
        logger.debug("Fetching all commands from the repository.");
        return commands;
    }

    public boolean hasCommand(String command) {
        boolean exists = commands.containsKey(command);
        logger.debug(String.format("Command \"%s\" exists (%s)", command, exists));
        return exists;
    }

    public BaseCommand getCommand(String command) {
        BaseCommand baseCommand = commands.get(command);
        if (baseCommand != null) {
            logger.debug(String.format("Command \"%s\" fetched successfully", command));
        } else {
            logger.warning(String.format("Command \"%s\" not found in the repository", command));
        }
        return baseCommand;
    }
}
