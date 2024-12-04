package common.commands.moderation;

import common.commands.BaseCommand;
import common.models.Interaction;
import common.models.User;

public class RemWarnCommand implements BaseCommand {
    @Override
    public String getCommandName() {
        return "remWarn";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {

    }
}
