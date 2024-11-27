package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.Task;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.util.List;

public class TaskCommand implements BaseCommand {
    Validate validate = new Validate();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "task";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        String firstArg = arguments.getFirst();
        switch (firstArg) {
            case "create": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                if (arguments.size() >= 3) {
                    user.setExcepted(getCommandName(), "duration")
                            .setValue(validate.isValidDate(arguments.get(1) + " " + arguments.get(2)));
                }
                if (arguments.size() >= 4) {
                    if (user.isExceptedKey(getCommandName(), "duration")) {
                        user.setExcepted(getCommandName(), "description")
                                .setValue(String.join(" ", arguments));
                    } else {
                        user.setExcepted(getCommandName(), "description")
                                .setValue(String.join(" ", arguments.subList(3, arguments.size() - 1)));
                    }
                }
                break;
            }
            case "edit": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                user.setExcepted(getCommandName(), "taskIndex").setValue(validate.isValidInteger(arguments.get(1)));
                user.setExcepted(getCommandName(), "newDuration").setValue(validate
                        .isValidDate(arguments.get(2) + " " + arguments.get(3)));
                user.setExcepted(getCommandName(), "newDescription")
                        .setValue(String.join(" ", arguments.subList(3, arguments.size() - 1)));
                break;
            }
            case "remove": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                user.setExcepted(getCommandName(), "taskIndex").setValue(arguments.get(1));
                break;
            }
            case "list": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                break;
            }
            default: {
                return;
            }
        }
    }

    @Override
    public void run(Interaction interaction) {
        logger.debug("Task command is start");
        User user = interaction.getUser(interaction.getUserId());

        if (!user.isExceptedKey(getCommandName(), "action")) {
            user.setExcepted(getCommandName(), "action");
            output.output(interaction.setMessage("Enter the action:"));
            logger.debug("Task command requested a first argument");
            return;
        }

        String action = (String) user.getValue(getCommandName(), "action");

        switch (action) {
            case "create": {
                create(interaction, user);
                break;
            }
        }

    }

    public void create(Interaction interaction, User user) {
        logger.debug("Create task is start");
        if (!user.isExceptedKey(getCommandName(), "title")) {
            user.setExcepted(getCommandName(), "title");
            output.output(interaction.setMessage("Enter the title:"));
            logger.debug("Task command requested a title");
        }
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            output.output(interaction.setMessage("Enter the duration or \"-\" if you don't need duration:"));
            logger.debug("Task command requested a duration");
        }
        if (!user.isExceptedKey(getCommandName(), "description")) {
            user.setExcepted(getCommandName(), "description");
            output.output(interaction.setMessage("Enter the description or \"/skip\" if you don't need description:"));
            logger.debug("Task command requested a description");
        }

        String title = (String) user.getValue(getCommandName(), "title");
        String description = (String) user.getValue(getCommandName(), "description");
        if(description.equals("/skip")){
            description = "";
        }
        user.addTask(new Task(interaction.getUserId(), interaction.getChatId(), title, description));
        user.clearExpected(getCommandName());
    }
}
