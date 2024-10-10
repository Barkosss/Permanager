package common.commands.info;

import common.commands.BaseCommand;

import java.util.List;

public class InfoCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "info";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Посмотреть информацию о боте";
    }

    // Вызов основной метод команды
    public void run(List<String> args) {

    }
}
