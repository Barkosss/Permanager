package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;

import java.util.Map;

public class TestCommand implements BaseCommand {
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
        Map<String, Map<String, String>> expectedInput = interaction.getUserInputExpectation().getExpectedInput();

        if (!expectedInput.get(getCommandName()).containsKey("firstMessage")) {
            interaction.getUserInputExpectation().getValueInt(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message: ").setInline(true));
            return;
        }

        if (!expectedInput.get(getCommandName()).containsKey("secondMessage")) {
            interaction.getUserInputExpectation().getValue(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message: ").setInline(true));
            return;
        }

        String firstMessage = expectedInput.get(getCommandName()).get("firstMessage");
        String secondMessage = expectedInput.get(getCommandName()).get("secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage));
        output.output(interaction.setMessage("Second message: " + secondMessage));
        interaction.getUserInputExpectation().clearExpectedInput(getCommandName());
    }
}
