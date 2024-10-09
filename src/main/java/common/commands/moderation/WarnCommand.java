package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class WarnCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "warn";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Выдать предупреждение пользователю";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
