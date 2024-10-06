package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class BanCommand implements BaseCommand {

    public String getCommandName() {
        return "ban";
    }

    public String getCommandDescription() {
        return "Забанить пользователя";
    }

    public void run(List<String> args) {

    }
}
