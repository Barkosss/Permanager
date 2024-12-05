package common.commands.moderation;

import common.commands.BaseCommand;
import common.models.Interaction;
import common.models.User;

public class ResetWarnsCommand implements BaseCommand {
    @Override
    public String getCommandName() {
        return "resetWarns";
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
