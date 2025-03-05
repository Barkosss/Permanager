package common.commands;

import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;

public class TestCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());

        if (!user.isExceptedKey(getCommandName(), "firstMessage")) {
            user.setExcepted(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message: ").setInline(true));
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "secondMessage")) {
            user.setExcepted(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message: ").setInline(true));
            return;
        }

        String firstMessage = (String) user.getValue(getCommandName(), "firstMessage");
        String secondMessage = (String) user.getValue(getCommandName(), "secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage).setInline(false));
        output.output(interaction.setMessage("Second message: " + secondMessage).setInline(false));
        user.clearExpected(getCommandName());
    }
}
