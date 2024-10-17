package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

public class TestCommand implements BaseCommand {
    public Output outputHandler = new OutputHandler();

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandDescription() {
        return "Test command for debug";
    }

    @Override
    public void run(Interaction interaction) {
        outputHandler.output(interaction.setMessage("Arguments: " + interaction.getArguments().toString()).setInline(false));
    }
}
