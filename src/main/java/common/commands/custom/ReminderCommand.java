package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.Reminder;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReminderCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    Validate validate = new Validate();
    OutputHandler output = new OutputHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "reminder";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Управление напоминаниями";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        // Проверка на пустоту аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // ---- Ищем действие в аргументах ----

        // Проверка на первый аргумент
        switch (arguments.getFirst()) {
            case "create": {
                user.setExcepted(getCommandName(), "action").setValue("create");
                arguments = arguments.subList(1, arguments.size());
                break;
            }

            case "edit": {
                user.setExcepted(getCommandName(), "action").setValue("edit");
                arguments = arguments.subList(1, arguments.size());
                break;
            }

            case "remove": {
                user.setExcepted(getCommandName(), "action").setValue("remove");
                arguments = arguments.subList(1, arguments.size());
                break;
            }

            case "list": {
                user.setExcepted(getCommandName(), "action").setValue("list");
                arguments = arguments.subList(1, arguments.size());
                break;
            }

            case "help": {
                user.setExcepted(getCommandName(), "action").setValue("help");
                arguments = arguments.subList(1, arguments.size());
                break;
            }
        }

        // Проверка на пустоту аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // ---- Ищем время в аргументах ----

        Optional<LocalDate> localDate = validate.isValidDate(arguments.getFirst() + " " + arguments.get(1));
        Optional<LocalDate> localTime = validate.isValidTime(arguments.getFirst());

        // Если указано время (дата и время)
        if (localDate.isPresent()) {
            user.setExcepted(getCommandName(), "date").setValue(localDate.get());
            arguments = arguments.subList(2, arguments.size());
        }

        // Если указано время на сегодня
        if (!user.isExceptedKey(getCommandName(), "date")) {
            localTime.ifPresent(time -> user.setExcepted(getCommandName(), "date").setValue(time));
            arguments = arguments.subList(1, arguments.size());
        }

        // ---- Ищем содержимое напоминания ----

        // Проверка на пустоту аргументов
        if (arguments.isEmpty()) {
            return;
        }

        user.setExcepted(getCommandName(), "context").setValue(String.join(" ", arguments));
    }


    // Вызвать основной метод команды
    public void run(Interaction interaction) {
        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        if (!user.isExceptedKey(getCommandName(), "action")) {
            user.setExcepted(getCommandName(), "action");
            logger.info("Reminder command requested a action argument");
            output.output(interaction.setMessage("Enter action (create, edit, remove, list)"));
            return;
        }

        if (!"list".equals(user.getValue(getCommandName(), "action")) && !user.isExceptedKey(getCommandName(), "date")) {
            user.setExcepted(getCommandName(), "date");
            logger.info("Reminder command requested a date argument");
            output.output(interaction.setMessage("Enter date for send reminder"));
            return;
        }

        if (!"list".equals(user.getValue(getCommandName(), "action")) && !user.isExceptedKey(getCommandName(), "context")) {
            user.setExcepted(getCommandName(), "context");
            logger.info("Reminder command requested a context argument");
            output.output(interaction.setMessage("Enter context for reminder"));
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

            case "help": {
                help(interaction, user);
                break;
            }

            default: {
                user.setExcepted(getCommandName(), "action");
                logger.info("Reminder command requested a action argument");
                output.output(interaction.setMessage("Enter action (create, edit, remove, list)"));
                break;
            }
        }
    }

    // Метод для вывода справочника команды
    public void help(Interaction interaction, User user) {
        user.clearExpected(getCommandName());
    }

    // Метод для создания напоминания
    public void create(Interaction interaction, User user) {
        long reminderId = user.getReminders(interaction.getChatId()).size() + 1;
        long chatId = interaction.getChatId();
        long userId = interaction.getUserId();
        String context = (String) user.getValue(getCommandName(), "context");
        LocalDate sendAt = (LocalDate) user.getValue(getCommandName(), "date");

        Reminder reminder = new Reminder(reminderId, chatId, userId, context, null, sendAt, interaction.getPlatform());
        interaction.getReminderRepository().create(reminder);
        user.addReminder(reminder);

        output.output(interaction.setMessage("Reminder is create").setInline(true));
        user.clearExpected(getCommandName());
    }

    // Метод для редактирования напоминания
    public void edit(Interaction interaction, User user) {
        user.clearExpected(getCommandName());
    }

    // Метод для удаления напоминания
    public void remove(Interaction interaction, User user) {
        user.clearExpected(getCommandName());
    }

    // Метод для просмотра напоминаний
    public void list(Interaction interaction, User user) {
        Map<Long, Reminder> reminders = user.getReminders(interaction.getChatId());
        StringBuilder message = new StringBuilder();

        if (!reminders.isEmpty()) {
            for (long reminderId : reminders.keySet()) {
                Reminder reminder = reminders.get(reminderId);
                message.append("#").append(reminder.getId()).append(": ").append(reminder.getContent()).append("\n");
            }
        } else {
            message.append("Reminders not found");
        }

        output.output(interaction.setMessage(String.valueOf(message)).setInline(true));
        user.clearExpected(getCommandName());
    }
}