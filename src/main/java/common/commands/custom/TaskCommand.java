package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.Task;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskCommand implements BaseCommand {
    ValidateService validate = new ValidateService();
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

        if (arguments.isEmpty()) {
            user.setExcepted(getCommandName(), "action");
            user.setExcepted(getCommandName(), "title");
            user.setExcepted(getCommandName(), "description");
            user.setExcepted(getCommandName(), "duration");
            return;
        }

        String firstArg = arguments.getFirst();
        arguments = arguments.subList(1, arguments.size() - 1);
        switch (firstArg) {
            case "create": {
                // if(arguments.size() >= )
                break;
            }
            case "edit": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                if (arguments.size() >= 2) {
                    Optional<LocalDateTime> date = validate.isValidDate(arguments.get(arguments.size() - 2)
                            + " " + arguments.getLast());
                    Optional<LocalDateTime> time = validate.isValidTime(arguments.getLast());
                    arguments.subList(0, arguments.size() - 2);
                } else if (!arguments.isEmpty()) {
                    Optional<LocalDateTime> time = validate.isValidTime(arguments.getFirst());
                    if (time.isPresent()) {
                        user.setExcepted(getCommandName(), "duration").setValue(time);
                    }
                    user.setExcepted(getCommandName(), "duration");
                } else {
                    user.setExcepted(getCommandName(), "title");
                    user.setExcepted(getCommandName(), "description");
                    user.setExcepted(getCommandName(), "duration");
                    return;
                }
                if (!arguments.isEmpty()) {
                    user.setExcepted(getCommandName(), "title").setValue(String.join(" ", arguments));
                } else {
                    user.setExcepted(getCommandName(), "title");
                }
                break;
            }
            case "remove": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                if (arguments.isEmpty()) {
                    user.setExcepted(getCommandName(), "taskIndex");
                    return;
                } else {
                    Optional<Integer> taskIndex = validate.isValidInteger(arguments.getFirst());
                    if (taskIndex.isPresent()) {
                        user.setExcepted(getCommandName(), "taskIndex").setValue(taskIndex);
                        return;
                    }
                    user.setExcepted(getCommandName(), "taskIndex");
                }
                break;
            }
            case "list": {
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
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
                logger.info("Task command requested a action argument");
                output.output(interaction.setMessage("Enter action (create, edit, remove, list): ").setInline(true));
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

        if (description.equals("/skip")) {
            description = "";
        }

        user.addTask(new Task(interaction.getUserId(), interaction.getChatId(), title, description));
        user.clearExpected(getCommandName());
    }

    public void remove(Interaction interaction, User user) {
        logger.debug("Remove task is start");
        if (!user.isExceptedKey(getCommandName(), "taskIndex")) {
            user.setExcepted(getCommandName(), "taskIndex");
            output.output(interaction.setMessage("Enter the taskIndex:"));
            logger.debug("Task command requested a taskIndex");
        }
        long taskId = (long) user.getValue(getCommandName(), "taskIndex");
        user.getTasks(interaction.getChatId()).remove(taskId);
    }

    public void edit(Interaction interaction, User user) {
        logger.debug("Remove task is start");
        if (!user.isExceptedKey(getCommandName(), "taskIndex")) {
            user.setExcepted(getCommandName(), "taskIndex");
            output.output(interaction.setMessage("Enter the taskIndex:"));
            logger.debug("Task command requested a taskIndex");
        }
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            output.output(interaction.setMessage("Enter the duration or \"/skip\" if you don't need duration:"));
            logger.debug("Task command requested a duration");
        }
        if (!user.isExceptedKey(getCommandName(), "description")) {
            user.setExcepted(getCommandName(), "description");
            output.output(interaction.setMessage("Enter the description or \"/skip\" if you don't need description:"));
            logger.debug("Task command requested a description");
        }

        String title = (String) user.getValue(getCommandName(), "title");
        String description = (String) user.getValue(getCommandName(), "description");

        if (description.equals("/skip")) {
            description = "";
        }
        long taskId = (long) user.getValue(getCommandName(), "taskIndex");
        Task editedTask = user.getTasks(interaction.getChatId()).get(taskId);
        user.getTasks(interaction.getChatId()).remove(taskId);
    }

    public void list(Interaction interaction, User user) {
        return;
    }
}