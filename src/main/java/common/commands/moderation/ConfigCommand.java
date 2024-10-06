package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ConfigCommand implements BaseCommand {

    public String getCommandName() {
        return "config";
    }

    public String getCommandDescription() {
        return "Настройка конфигурации";
    }

    public void run(List<String> args) {

    }
}
