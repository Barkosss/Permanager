package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class RemoveWarnCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "removewarn";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Снять предупреждение пользователю";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
