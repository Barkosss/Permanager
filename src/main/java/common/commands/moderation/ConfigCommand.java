package common.commands.moderation;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;

public class ConfigCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    private void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        if (arguments.isEmpty()) {
            return;
        }
        String argument = arguments.getFirst().toLowerCase();
        if (List.of("dashboard", "user", "group").contains(argument)) {
            user.setExcepted(getCommandName(), "section").setValue(argument);
        }
    }

    @Override
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        if (!user.isExceptedKey(getCommandName(), "section")) {
            user.setExcepted(getCommandName(), "section");
            output.output(interaction.setMessage(interaction.getLanguageValue("config.start.section")).setInline(true));
            logger.debug("Config command requested a first argument");
            return;
        }

        switch (user.getValue(getCommandName(), "section")) {
            case "dashboard": {
                logger.debug("Run method \"dashboard\" in config command");
                dashboard(interaction, user);
                break;
            }
            case "user": {
                user(interaction, user);
                break;
            }
            case "group": {
                group(interaction, user);
                break;
            }
        }
    }

    private void dashboard(Interaction interaction, User user) {
        output.output(interaction.setMessage(interaction.getLanguageValue("config.dashboard")).setInline(true));
    }

    private void user(Interaction interaction, User user) {
        // ...
    }

    private void group(Interaction interaction, User user) {
        // ...
    }
}
