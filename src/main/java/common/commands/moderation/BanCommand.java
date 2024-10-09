package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class BanCommand implements BaseCommand {

    // Получить короткое название метода
    public String getCommandName() {
        return "ban";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Забанить пользователя";
    }

    // Вызвать основной метод команды
    public void run(List<String> args) {

    }
}
