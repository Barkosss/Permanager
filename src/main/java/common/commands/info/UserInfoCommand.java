package common.commands.info;

import common.commands.BaseCommand;

import java.util.List;

public class UserInfoCommand implements BaseCommand {

    // Получить короткое название команды
    public String getCommandName() {
        return "userinfo";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Посмотреть информацию о пользователе";
    }

    // Запустить основной метод команды
    public void run(List<String> args) {

    }
}