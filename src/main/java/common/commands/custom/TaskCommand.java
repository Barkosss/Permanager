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
    ValidateService validate = new ValidateService();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

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
                user.setExcepted(getCommandName(), "action").setValue(firstArg);
                if (arguments.size() >= 1) {
                    user.setExcepted(getCommandName(), "title").setValue(arguments.getFirst());
                    arguments = arguments.subList(1, arguments.size() - 1);
                }
                if (arguments.size() >= 2) {
                    Optional<LocalDateTime> dedLine = validate.isValidDate(arguments.getFirst());
                    arguments = arguments.subList(1, arguments.size() - 1);
                    if (dedLine.isPresent()) {
                        user.setExcepted(getCommandName(), "duration").setValue(dedLine);
                    }
                }
                break;
            }
            case "edit": {
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
            output.output(interaction.setMessage("Enter the title:").setInline(true));
            logger.debug("Task command requested a title");
            return;
        }
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            output.output(interaction.setMessage("Enter the duration or \"/skip\" if you don't need duration:").setInline(true));
            logger.debug("Task command requested a duration");
            return;
        }
        if (!user.isExceptedKey(getCommandName(), "description")) {
            user.setExcepted(getCommandName(), "description");
            output.output(interaction.setMessage("Enter the description or \"/skip\" if you don't need description:").setInline(true));
            logger.trace("Task command requested a description");
            return;
        }

        String title = (String) user.getValue(getCommandName(), "title");
        String description = (String) user.getValue(getCommandName(), "description"); //нижний регистр и убрать пробелы в начале и конце

        if (description.equals("/skip")) {
            description = "";
        }

        user.addTask(new Task(interaction.getUserId(), interaction.getChatId(), title, description));
        user.clearExpected(getCommandName());

        output.output(interaction.setMessage("Task is created."));
    }

    public void remove(Interaction interaction, User user) {
        logger.trace("Remove task is start");
        if (!user.isExceptedKey(getCommandName(), "taskIndex")) {
            user.setExcepted(getCommandName(), "taskIndex", InputExpectation.UserInputType.INTEGER);
            output.output(interaction.setMessage("Enter the taskIndex:"));
            logger.debug("Task command requested a taskIndex");
            return;
        }
        System.out.println(user.getValue(getCommandName(), "taskIndex"));
        long taskId = (Long) user.getValue(getCommandName(), "taskIndex");
        user.getTasks(interaction.getChatId()).remove(taskId);
    }

    public void edit(Interaction interaction, User user) {
        logger.trace("Edit task is start");
        if (!user.isExceptedKey(getCommandName(), "taskIndex")) {
            user.setExcepted(getCommandName(), "taskIndex");
            output.output(interaction.setMessage("Enter the taskIndex:"));
            logger.debug("Task command requested a taskIndex");
            return;
        }
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            output.output(interaction.setMessage("Enter the duration or \"/skip\" if you don't need duration:"));
            logger.debug("Task command requested a duration");
            return;
        }
        if (!user.isExceptedKey(getCommandName(), "description")) {
            user.setExcepted(getCommandName(), "description");
            output.output(interaction.setMessage("Enter the description or \"/skip\" if you don't need description:"));
            logger.debug("Task command requested a description");
            return;
        }

        String title = (String) user.getValue(getCommandName(), "title");
        String description = (String) user.getValue(getCommandName(), "description");

        if (description.equals("/skip")) {
            description = "";
        }
        long taskId = (long) user.getValue(getCommandName(), "taskIndex");
        Task editedTask = user.getTasks(interaction.getChatId()).get(taskId);
        user.getTasks(interaction.getChatId()).remove(taskId);

        String newTitle = (String) user.getValue(getCommandName(), "title");
        if (!newTitle.equals("/skip")) {
            editedTask.setTitle(newTitle);
        }

        if (!user.getValue(getCommandName(), "duration").equals("/skip")) {
            Optional<LocalDateTime> newDuration = validate.isValidDate((String) user.getValue(getCommandName(), "duration"));
            if (newDuration.isEmpty()) {
                output.output(interaction.setMessage(
                        "An incorrect date was entered, specify the date in the format: "
                                + "DD.MM.YYYY or \"/skip\" if you don't need duration"));
                return;
            }
            editedTask.setDeadLine(newDuration.get());
        }

        user.getTasks(editedTask.chatId).put(editedTask.chatId, editedTask);
    }

    public void list(Interaction interaction, User user) {
        if (user.getTasks(interaction.getChatId()).isEmpty()) {
            output.output(interaction.setMessage("You don't have tasks. If you need create task, enter: \"/task create\""));
            return;
        }
        for (long key : user.getTasks(interaction.getChatId()).keySet()) {
            user.getTasks(interaction.getChatId()).get(key).printTask(output, interaction);
        }
    }
}