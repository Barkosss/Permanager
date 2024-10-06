package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ClearCommand implements BaseCommand {

    public String getCommandName() {
        return "clear";
    }

    public String getCommandDescription() {
        return "Настройка конфигурации";
    }

    public void run(List<String> args) {

    }
}
