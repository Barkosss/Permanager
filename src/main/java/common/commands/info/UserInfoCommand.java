package common.commands.info;

import common.commands.BaseCommand;

public class UserInfoCommand implements BaseCommand {

    public String getCommandName() {
        return "userinfo";
    }

    public String getCommandDescription() {
        return "Посмотреть информацию о пользователе";
    }

    public void run() {

    }
}