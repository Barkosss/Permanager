package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class ResetWarnsCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "resetwarns";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Очистить предупреждения";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
