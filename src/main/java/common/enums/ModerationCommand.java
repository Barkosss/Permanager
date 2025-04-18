package common.enums;

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
    // Вопрос про static?
    // enum не успевает все константы инициализировать и происходит ошибка NullPointerException
    /*
    Caused by: java.lang.NullPointerException:
    Cannot invoke "[Lcommon.enums.ModerationCommand;.clone()" because "common.enums.ModerationCommand.$VALUES" is null
     */
    private Map<String, ModerationCommand> lookup = new HashMap<>();

    ModerationCommand(String commandName) {
        this.commandName = commandName;
    }

    private void init() {
        if (lookup == null) {
            lookup = new HashMap<>();
            for (ModerationCommand command : ModerationCommand.values()) {
                lookup.put(command.commandName, command);
            }
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public Optional<ModerationCommand> getCommand(String value) {
        init();
        ModerationCommand cmd = lookup.get(value.toUpperCase());
        if (cmd == null || cmd == ALL) {
            return Optional.empty();
        }
        return Optional.of(cmd);
    }
}
