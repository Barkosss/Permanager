package common.commands;

import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;

public class TestCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandDescription() {
        return "Test command for debug";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {
        logger.debug("Test command is start");
        User user = interaction.getUser(interaction.getUserId());

        if (!user.isExceptedKey(getCommandName(), "firstMessage")) {
            user.setExcepted(getCommandName(), "firstMessage");
            output.output(interaction.setMessage("Enter first message: ").setInline(true));
            logger.debug("Test command requested a first argument");
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "secondMessage")) {
            user.setExcepted(getCommandName(), "secondMessage");
            output.output(interaction.setMessage("Enter second message: ").setInline(true));
            logger.debug("Test command requested a second argument");
            return;
        }

        String firstMessage = (String) user.getValue(getCommandName(), "firstMessage");
        String secondMessage = (String) user.getValue(getCommandName(), "secondMessage");

        output.output(interaction.setMessage("First message: " + firstMessage).setInline(false));
        output.output(interaction.setMessage("Second message: " + secondMessage).setInline(false));
        logger.debug("Test command is end");
        user.clearExpected(getCommandName());
    }
}
