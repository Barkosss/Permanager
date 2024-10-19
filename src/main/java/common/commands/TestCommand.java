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
        Map<String, Map<String, String>> commandStatus = interaction.getCommandStatus();

        if (!commandStatus.get(getCommandName()).containsKey("firstMessage")) {
            interaction.getValue(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message: ").setInline(true));
            return;
        }

        if (!commandStatus.get(getCommandName()).containsKey("secondMessage")) {
            interaction.getValue(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message: ").setInline(true));
            return;
        }

        String firstMessage = commandStatus.get(getCommandName()).get("firstMessage");
        String secondMessage = commandStatus.get(getCommandName()).get("secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage).setInline(false));
        output.output(interaction.setMessage("Second message: " + secondMessage).setInline(false));
        interaction.clearCommandStatus(getCommandName());
    }
}
