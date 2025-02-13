package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.Task;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WorkCommand implements BaseCommand {
    private static final Logger log = LoggerFactory.getLogger(WorkCommand.class);
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
                break;
            }
            case "edit": {}
            case "remove": {
                arguments = arguments.subList(1, arguments.size());
                user.setExcepted(getCommandName(), "action").setValue(action);

                if (arguments.isEmpty()) {
                    return;
                }

                Optional<Long> validateIndex = validate.isValidLong(arguments.getFirst());
                if (validateIndex.isEmpty()) {
                    return;
                }

                user.setExcepted(getCommandName(), "index").setValue(validateIndex.get());
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
                logger.debug("");
                output.output(interaction.setLanguageValue(""));
                break;
            }
        }
    }

    private void create(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "title")) {
            user.setExcepted(getCommandName(), "title");
            logger.debug("");
            output.output(interaction.setLanguageValue(""));
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "description")) {
            user.setExcepted(getCommandName(), "description");
            logger.debug("");
            output.output(interaction.setLanguageValue(""));
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.debug("");
            output.output(interaction.setLanguageValue(""));
            return;
        }


        String stringDuration = (String) user.getValue(getCommandName(), "duration");
        LocalDateTime duration = null;
        // ...
        if (!stringDuration.equalsIgnoreCase("/skip")) {
            Optional<LocalDateTime> validDate = validate.isValidDate(stringDuration);
            if (validDate.isEmpty() || validDate.get().isBefore(LocalDateTime.now())) {
                output.output(interaction.setLanguageValue(""));
                return;
            }

            duration = validDate.get();
        }

        String title = (String) user.getValue(getCommandName(), "title");
        String description = (String) user.getValue(getCommandName(), "description");

        // ...
        if (description.equalsIgnoreCase("/skip")) {
            description = "";
        }

        Task task = new Task(interaction.getUserId(), interaction.getChatId(), title, description);
        // ...
        if (duration != null) {
            task.setDeadLine(duration);
        }

        user.addTask(task);
        output.output(interaction.setLanguageValue(""));

        user.clearExpected(getCommandName());
    }

    private void edit(Interaction interaction, User user) {}

    private void remove(Interaction interaction, User user) {}

    private void list(Interaction interaction, User user) {}
}
