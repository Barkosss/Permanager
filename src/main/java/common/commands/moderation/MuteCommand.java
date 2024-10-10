package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class MuteCommand implements BaseCommand  {

    // Получить короткое название команды
    public String getCommandName() {
        return "mute";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Замьютить пользователя";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
