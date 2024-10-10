package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ConfigCommand implements BaseCommand {

    // Получить короткое описание команды
    public String getCommandName() {
        return "config";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Настройка конфигурации";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
