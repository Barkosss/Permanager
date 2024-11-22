package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

public class StartCommand implements BaseCommand {
    Output output = new OutputHandler();

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

    }
}
