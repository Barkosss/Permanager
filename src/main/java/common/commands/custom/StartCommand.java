package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;

public class StartCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {}

    @Override
    public void run(Interaction interaction) {
        output.output(interaction.setLanguageValue("start.message"));
    }
}
