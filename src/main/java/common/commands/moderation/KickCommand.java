package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class KickCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "kick";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Выгнать пользователя";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
