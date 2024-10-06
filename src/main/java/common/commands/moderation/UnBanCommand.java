package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class UnBanCommand implements BaseCommand {

    public String getCommandName() {
        return "unban";
    }

    public String getCommandDescription() {
        return "Разбинать пользователя";
    }

    public void run(List<String> args) {

    }
}
