package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class UnBanCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "unban";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Разбинать пользователя";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
