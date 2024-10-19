package common.commands;

import common.iostream.Input;
import common.iostream.InputHandler;
import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

import java.util.Map;

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
        Map<String, Map<String, String>> commandStatus = interaction.getCommandStatus();

        if (!commandStatus.get(getCommandName()).containsKey("firstMessage")) {
            interaction.setCommandStatus(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message"));
            return;
        }

        if (!commandStatus.get(getCommandName()).containsKey("secondMessage")) {
            interaction.setCommandStatus(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message"));
            return;
        }

        String firstMessage = commandStatus.get(getCommandName()).get("firstMessage");
        String secondMessage = commandStatus.get(getCommandName()).get("secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage));
        output.output(interaction.setMessage("Second message: " + secondMessage));
    }
}
