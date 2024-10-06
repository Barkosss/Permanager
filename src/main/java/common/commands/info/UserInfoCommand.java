package common.commands.info;

import common.commands.BaseCommand;

import java.util.List;

public class UserInfoCommand implements BaseCommand {

    public String getCommandName() {
        return "userinfo";
    }

    public String getCommandDescription() {
        return "Посмотреть информацию о пользователе";
    }

    public void run(List<String> args) {

    }
}