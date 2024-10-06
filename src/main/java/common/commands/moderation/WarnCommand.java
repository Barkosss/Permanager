package common.commands.moderation;

import common.commands.BaseCommand;

public class WarnCommand implements BaseCommand {

    public String getCommandName() {
        return "warn";
    }

    public String getCommandDescription() {
        return "Выдать предупреждение пользователю";
    }

    public void run() {

    }
}
