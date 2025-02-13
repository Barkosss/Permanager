package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class WorkCommand implements BaseCommand {
    private final OutputHandler output = new OutputHandler();
    private final ValidateService validate = new ValidateService();
    private final LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "work";
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

        String action = arguments.getFirst();
        switch (action) {
            case "create": {
                arguments = arguments.subList(1, arguments.size());
                user.setExcepted(getCommandName(), "action").setValue(action);

                if (arguments.isEmpty()) {
                    return;
                }

                // create...

                break;
            }
            case "edit": {
                arguments = arguments.subList(1, arguments.size());
                user.setExcepted(getCommandName(), "action").setValue(action);

                if (arguments.isEmpty()) {
                    return;
                }

                // edit...

                break;
            }
            case "remove": {
                arguments = arguments.subList(1, arguments.size());
                user.setExcepted(getCommandName(), "action").setValue(action);

                if (arguments.isEmpty()) {
                    return;
                }

                Optional<Long> validateIndex = validate.isValidLong(arguments.getFirst());

                validateIndex.ifPresent(index -> user.setExcepted(getCommandName(), "index").setValue(index));

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
            logger.info("");
            output.output(interaction.setLanguageValue(""));
            return;
        }

        String action = ((String) user.getValue(getCommandName(), "action")).toLowerCase();
        switch (action) {
            case "create": {
                create(interaction, user);
                break;
            }
            case "edit": {
                edit(interaction, user);
                break;
            }
            case "remove": {
                remove(interaction, user);
                break;
            }
            case "list": {
                list(interaction, user);
                break;
            }
            default: {
                user.setExcepted(getCommandName(), "action");
                logger.info("");
                output.output(interaction.setLanguageValue(""));
                break;
            }
        }
    }

    private void create(Interaction interaction, User user) {


    }

    private void edit(Interaction interaction, User user) {}

    private void remove(Interaction interaction, User user) {}

    private void list(Interaction interaction, User user) {}
}
