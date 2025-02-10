package common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, ModerationCommand> lookup;

    ModerationCommand(String commandName) {
        this.commandName = commandName;
        this.lookup = new HashMap<>();
        for (ModerationCommand command : ModerationCommand.values()) {
            lookup.put(command.commandName, command);
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public Optional<ModerationCommand> getCommand(String value) {
        ModerationCommand cmd = lookup.get(value.toUpperCase());
        if (cmd == null) {
            return Optional.empty();
        }
        return Optional.of(cmd);
    }
}
