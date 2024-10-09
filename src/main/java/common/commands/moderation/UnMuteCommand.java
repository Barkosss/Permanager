package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class UnMuteCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "unmute";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Размьютить пользователя";
    }

    // Запустить основной метод команды
    public void run(List<String> args) {

    }
}
