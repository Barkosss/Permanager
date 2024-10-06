package common.commands.moderation;

import common.commands.BaseCommand;

import java.util.List;

public class MuteCommand implements BaseCommand  {

    public String getCommandName() {
        return "mute";
    }

    public String getCommandDescription() {
        return "Замьютить пользователя";
    }

    public void run(List<String> args) {

    }
}
