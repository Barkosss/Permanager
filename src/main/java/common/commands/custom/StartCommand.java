package common.commands.custom;

import common.commands.AbstractCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;

public class StartCommand extends AbstractCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        // ignore
    }

    @Override
    public void run(Interaction interaction) {
        try {
            User user = interaction.getUser(interaction.getUserId());
            logger.info(String.format("User (id: %s) triggered /start command", user.getUserId()));
            output.output(interaction.setLanguageValue("start.message"));
        } catch (Exception e) {
            logger.warning("Error occurred while processing /start command: " + e.getMessage());
            output.output(interaction.setLanguageValue("system.error.something"));
        }
    }
}
