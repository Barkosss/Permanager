package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class WarnCommand implements BaseCommand {

    public String getCommandName() {
        return "warn";
    }

    public String getCommandDescription() {
        return "Выдать предупреждение пользователю";
    }

    public void run(List<String> args) {

    }
}
