package common.commands.custom;

import common.commands.BaseCommand;
import common.models.Interaction;
import common.models.User;

public class EventCommand implements BaseCommand {

    @Override
    public String getCommandName() {
        return "event";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {

    }
}
