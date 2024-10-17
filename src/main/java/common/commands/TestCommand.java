package common.commands;

import common.iostream.Input;
import common.iostream.InputHandler;
import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

public class TestCommand implements BaseCommand {
    public Input input = new InputHandler();
    public Output output = new OutputHandler();

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
        output.output(interaction.setMessage(String.join(" ", interaction.getArguments())));
    }
}
