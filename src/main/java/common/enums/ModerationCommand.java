package common.enums;

import java.util.Optional;

public enum ModerationCommand {
    KICK("KICK"),
    BAN("BAN"),
    UNBAN("UNBAN"),
    MUTE("MUTE"),
    UNMUTE("UNMUTE"),
    WARN("WARN"),
    REMWARN("REMWARN"),
    RESETWARNS("RESETWARNS"),
    CLEAR("CLEAR"),
    CONFIG("CONFIG"),
    ALL("ALL");

    private final String commandName;

    ModerationCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public Optional<ModerationCommand> getCommand(String value) {
        for (ModerationCommand command : ModerationCommand.values()) {
            if (command.getCommandName().equalsIgnoreCase(value) && command != ALL) {
                return Optional.of(command);
            }
        }

        return Optional.empty();
    }
}
