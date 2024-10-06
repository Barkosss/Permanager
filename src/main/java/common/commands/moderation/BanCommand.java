package common.commands.moderation;

import common.commands.BaseCommand;

public class BanCommand implements BaseCommand {

    public String getCommandName() {
        return "ban";
    }

    public String getCommandDescription() {
        return "Забанить пользователя";
    }

    public void run() {

    }
}
