package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.Reminder;
import common.models.User;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ReminderCommand implements BaseCommand {
    JSONHandler jsonHandler = new JSONHandler();
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

        if (arguments.isEmpty()) {
            return;
        }

        // ---- Ищем время в аргументах ----

        Optional<LocalDate> localDate = validate.isValidDate(arguments.getFirst() + " " + arguments.get(1));
        Optional<LocalDate> localTime = validate.isValidTime(arguments.getFirst());

        // Если указано время (дата и время)
        if (localDate.isPresent()) {
            user.setExcepted(getCommandName(), "date", InputExpectation.UserInputType.DATE)
                    .setValue(localDate.get());
            arguments = arguments.subList(2, arguments.size());
        } else if (!user.isExceptedKey(getCommandName(), "date") && localTime.isPresent()) {
            user.setExcepted(getCommandName(), "date", InputExpectation.UserInputType.DATE)
                    .setValue(localTime.get());
            arguments = arguments.subList(1, arguments.size());
        }

        // ---- Ищем содержимое напоминания ----

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
            output.output(interaction.setMessage("Enter action (create, edit, remove, list, help): ").setInline(true));
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
                output.output(interaction.setMessage("Enter action (create, edit, remove, list, help): ")
                        .setInline(true));
                break;
            }
        }
    }

    // Метод для вывода справочника команды
    public void help(Interaction interaction, User user) {
        StringBuilder helpOutput;

        String manual = (String) jsonHandler.read("manual.json",
                "manual.reminder.create." + interaction.getLanguageCode().getLang());

        if (manual.isEmpty()) {
            manual = interaction.getLanguageValue("help.notFoundManual");
        }

        helpOutput = new StringBuilder("--------- HELP \"Reminder Create\" ---------\n");
        helpOutput.append(manual);
        helpOutput.append("\n--------- HELP \"Reminder Create\" ---------\n");

        output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));
        user.clearExpected(getCommandName());
    }

    // Метод для создания напоминания
    public void create(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "date")) {
                user.setExcepted(getCommandName(), "date", InputExpectation.UserInputType.DATE);
            logger.info("Reminder command requested a date argument for create");
            output.output(interaction.setMessage("Enter date for send reminder: ").setInline(true));
            return;
        }

        LocalDate sendAt = user.getValue(getCommandName(), "date");
        // Проверка на корректность даты
        if (sendAt.isBefore(LocalDate.now()) || sendAt.isAfter(sendAt.plusYears(5))) {
            user.setExcepted(getCommandName(), "date");
            logger.info("Reminder command again requested a date argument for create");
            // Пользователь указал время в прошлом
            if (sendAt.isBefore(LocalDate.now())) {
                output.output(interaction.setMessage("The date should be in the future. Enter date for send reminder: ")
                        .setInline(true));
            } else { // Пользователь создал напоминание более чем на 5 лет в будущее
                output.output(interaction.setMessage(
                        "You cannot create a reminder for a date older than 5 years. Enter date for send reminder: "
                ).setInline(true));
            }
            create(interaction, user);
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "context")) {
            user.setExcepted(getCommandName(), "context");
            logger.info("Reminder command requested a context argument for create");
            output.output(interaction.setMessage("Enter context for reminder: ").setInline(true));
            return;
        }

        long reminderId = user.getReminders(interaction.getChatId()).size() + 1;
        long chatId = interaction.getChatId();
        long userId = interaction.getUserId();
        String context = (String) user.getValue(getCommandName(), "context");

        Reminder reminder = new Reminder(reminderId, chatId, userId, context,
                null, sendAt, interaction.getPlatform());
        interaction.getReminderRepository().create(reminder);
        user.addReminder(reminder);

        output.output(interaction.setMessage("Reminder is create"));
        logger.info("User by id(" + user.getUserId() + ", chatId=" + interaction.getChatId()
                + ") create reminder by id(" + reminderId + ")");
        user.clearExpected(getCommandName());
    }

    // Метод для редактирования напоминания
    public void edit(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "index")) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command request a index reminder for edit");
            output.output(interaction.setMessage("Enter reminder's index: ").setInline(true));
            return;
        }
        long reminderId = (long) user.getValue(getCommandName(), "index");

        if (!user.getReminders(interaction.getChatId()).containsKey(reminderId)) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command again request a index reminder for edit");
            edit(interaction, user);
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "newTime")) {
            user.setExcepted(getCommandName(), "newTime");
            logger.info("Reminder command request a new time reminder for edit");
            output.output(interaction.setMessage("Enter new time (or \"/skip\" if you don't need): ").setInline(true));
            return;
        }

        if (user.getValue(getCommandName(), "newTime") != "/skip") {
            LocalDate sendAt = (LocalDate) user.getValue(getCommandName(), "newTime");
            // Проверка на корректность даты
            if (sendAt.isBefore(LocalDate.now()) || sendAt.isAfter(sendAt.plusYears(5))) {
                user.setExcepted(getCommandName(), "newTime");
                logger.info("Reminder command again requested a date argument for edit");
                // Пользователь указал время в прошлом
                if (sendAt.isBefore(LocalDate.now())) {
                    output.output(interaction.setMessage(
                            "The date should be in the future. Enter date for send reminder: "
                    ).setInline(true));
                } else { // Пользователь создал напоминание более чем на 5 лет в будущее
                    output.output(interaction.setMessage("You cannot create a reminder for a date older than 5 years."
                            + "Enter date for send reminder: ").setInline(true));
                }
                edit(interaction, user);
                return;
            }
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "newContext")) {
            user.setExcepted(getCommandName(), "newContext");
            logger.info("Reminder command request a new context reminder for edit");
            output.output(interaction.setMessage("Enter new content (or \"/skip\" if you don't need): ")
                    .setInline(true));
            return;
        }

        Object newLocalDate = user.getValue(getCommandName(), "newTime");
        String newContext = (String) user.getValue(getCommandName(), "newContext");
        Reminder reminder = user.getReminders(interaction.getChatId()).get(reminderId);

        // Изменяем описание напоминания, если такое имеется
        if (!Objects.equals(newContext, reminder.getContent()) && !Objects.equals(newContext, "/skip")) {
            reminder.setContent(newContext);
        }

        // Изменяем время отправки, если такое имеется
        if (!Objects.equals(newLocalDate, reminder.getSendAt()) && !Objects.equals(newLocalDate, "/skip")) {
            reminder.setSendAt((LocalDate) newLocalDate);
        }

        user.getReminders(interaction.getChatId()).put(reminderId, reminder);

        output.output(interaction.setMessage("Reminder is edit"));
        logger.info("User by id(" + user.getUserId() + ", chatId=" + interaction.getChatId()
                + ") edit reminder by id(" + reminderId + ")");
        user.clearExpected(getCommandName());
    }

    // Метод для удаления напоминания
    public void remove(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "index")) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command requested a index argument for remove");
            output.output(interaction.setMessage("Enter index reminder: ").setInline(true));
            return;
        }

        long reminderId = (long) user.getValue(getCommandName(), "index");

        if (!user.getReminders(interaction.getChatId()).containsKey(reminderId)) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command again request a index reminder for remove");
            remove(interaction, user);
            return;
        }

        user.getReminders(interaction.getChatId()).remove(reminderId);

        output.output(interaction.setMessage("Reminder is remove"));
        logger.info("User by id(" + user.getUserId() + ", chatId=" + interaction.getChatId()
                + ") remove reminder by id(" + reminderId + ")");
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

        output.output(interaction.setMessage(String.valueOf(message)));
        user.clearExpected(getCommandName());
    }
}