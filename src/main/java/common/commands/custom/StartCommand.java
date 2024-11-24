package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;

public class StartCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public String getCommandDescription() {
        return "The command to receive a welcome message";
    }

    @Override
    public void run(Interaction interaction) {
        String message = "";


        output.output(interaction.setMessage(message));
    }
}
