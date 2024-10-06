package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class RemoveWarnCommand implements BaseCommand {

    public String getCommandName() {
        return "removewarn";
    }

    public String getCommandDescription() {
        return "Снять предупреждение пользователю";
    }

    public void run(List<String> args) {

    }
}
