package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class KickCommand implements BaseCommand {

    public String getCommandName() {
        return "kick";
    }

    public String getCommandDescription() {
        return "Выгнать пользователя";
    }

    public void run(List<String> args) {

    }
}
