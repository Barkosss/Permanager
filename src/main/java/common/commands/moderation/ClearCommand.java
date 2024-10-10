package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ClearCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "clear";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Настройка конфигурации";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
