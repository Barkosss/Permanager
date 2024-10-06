package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class UnMuteCommand implements BaseCommand {

    public String getCommandName() {
        return "unmute";
    }

    public String getCommandDescription() {
        return "Размьютить пользователя";
    }

    public void run(List<String> args) {

    }
}
