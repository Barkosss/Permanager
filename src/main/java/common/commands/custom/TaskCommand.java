package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.Task;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskCommand implements BaseCommand {
    private final ValidateService validateService = new ValidateService();
    private final LoggerHandler logger = new LoggerHandler();
    private final OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "task";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        String action = arguments.getFirst().toLowerCase();
        switch (action) {
            case "create": {
                user.setExcepted(getCommandName(), "action").setValue(action);

                // create
                break;
            }

            case "edit": {
                user.setExcepted(getCommandName(), "action").setValue(action);

                // edit
                break;
            }

            case "remove": {
                user.setExcepted(getCommandName(), "action").setValue(action);

                // remove
                break;
            }

            case "list": {
                user.setExcepted(getCommandName(), "action").setValue(action);
                break;
            }
        }
    }

    @Override
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());

        parseArgs(interaction, user);

        if (!user.isExceptedKey(getCommandName(), "action")) {
            user.setExcepted(getCommandName(), "action");
            logger.debug("");
            output.output(interaction.setLanguageValue(".action"));
            return;
        }
    }
}

