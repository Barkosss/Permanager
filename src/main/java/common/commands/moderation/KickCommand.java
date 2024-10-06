package common.commands.moderation;

import common.commands.BaseCommand;

public class KickCommand implements BaseCommand {

    public String getCommandName() {
        return "kick";
    }

    public String getCommandDescription() {
        return "Выгнать пользователя";
    }

    public void run() {

    }
}
