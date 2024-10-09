package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class WarnsCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "warns";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Посмотреть список предупреждений";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
