package common.commands;

import common.models.Interaction;

public abstract class AbstractCommand implements BaseCommand {

    public abstract String getCommandName();

    public String getCommandDescription(Interaction interaction) {
        return "Undefined Command";
    }

    public void commandHandler(Interaction interaction) {
        parseArgs(interaction, interaction.getUser(interaction.getUserId()));
        run(interaction);
    }
}
