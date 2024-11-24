package common.commands.moderation;

import common.commands.BaseCommand;
import common.models.Interaction;

public class ConfigCommand implements BaseCommand {

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void run(Interaction interaction) {

    }
}
