package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.Reminder;
import common.models.User;
import common.utils.JSONHandler;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ReminderCommand implements BaseCommand {
    JSONHandler jsonHandler = new JSONHandler();
    LoggerHandler logger = new LoggerHandler();
    ValidateService validate = new ValidateService();
    OutputHandler output = new OutputHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "reminder";
    }

    // Получить описание команды
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
        Optional<LocalDateTime> localDate = Optional.empty();
        Optional<LocalDateTime> localTime = Optional.empty();

        if (arguments.size() >= 2) {
            localDate = validate.isValidDate(String.format("%s %s", arguments.getFirst(),
                    arguments.get(1)));
        } else {
            localTime = validate.isValidDate(arguments.getFirst());
        }

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
            output.output(interaction.setLanguageValue("reminder.start.action").setInline(true));
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
                output.output(interaction.setLanguageValue("reminder.start.againAction").setInline(true));
                break;
            }
        }
    }

    // Метод для вывода справочника команды
    public void help(Interaction interaction, User user) {
        StringBuilder helpOutput;

        String manual = (String) jsonHandler.read(String.format("manual_%s.json",
                        interaction.getLanguageCode().getLang()),
                "manual.reminder.help");

        if (manual.isEmpty()) {
            manual = interaction.getLanguageValue("help.notFoundManual");
        }

        helpOutput = new StringBuilder("--------- HELP \"Reminder Help\" ---------\n");
        helpOutput.append(manual);
        helpOutput.append("\n--------- HELP \"Reminder Help\" ---------\n");

        output.output(interaction.setMessage(String.valueOf(helpOutput)).setInline(false));
        user.clearExpected(getCommandName());
    }

    // Метод для создания напоминания
    public void create(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "date")) {
            user.setExcepted(getCommandName(), "date", InputExpectation.UserInputType.DATE);
            logger.info("Reminder command requested a date argument for create");
            output.output(interaction.setLanguageValue("reminder.create.date").setInline(true));
            return;
        }

        LocalDateTime sendAt = (LocalDateTime) user.getValue(getCommandName(), "date");
        // Проверка на корректность даты
        if (sendAt.isBefore(LocalDateTime.now()) || sendAt.isAfter(sendAt.plusYears(5))) {
            user.clearExpected(getCommandName(), "date");
            logger.info("Reminder command again requested a date argument for create");
            // Пользователь указал время в прошлом
            if (sendAt.isBefore(LocalDateTime.now())) {
                output.output(interaction.setLanguageValue("reminder.create.error.futureDate").setInline(true));
            } else { // Пользователь создал напоминание более чем на 5 лет в будущее
                output.output(interaction.setLanguageValue("reminder.create.error.limitDate").setInline(true));
            }
            create(interaction, user);
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "context")) {
            user.setExcepted(getCommandName(), "context");
            logger.info("Reminder command requested a context argument for create");
            output.output(interaction.setLanguageValue("reminder.create.content").setInline(true));
            return;
        }

        long reminderId = user.getReminders(interaction.getChatId()).size() + 1;
        long chatId = interaction.getChatId();
        long userId = interaction.getUserId();
        String context = (String) user.getValue(getCommandName(), "context");

        Reminder reminder = new Reminder(reminderId, chatId, userId, context,
                LocalDateTime.now(), sendAt, interaction.getPlatform());
        interaction.createReminder(reminder);
        user.addReminder(reminder);

        try {
            output.output(interaction.setLanguageValue("reminder.create.complete",
                    List.of(String.valueOf(reminderId))));
        } catch (Exception err) {
            logger.debug(String.format("Couldn't find replaces symbols (Reminder->create): %s", err));
        }
        logger.info(String.format("User by id(%d, chatId=%d) create reminder by id(%d)",
                user.getUserId(), interaction.getChatId(), reminderId));
        user.clearExpected(getCommandName());
    }

    // Метод для редактирования напоминания
    public void edit(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "index")) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command request a index reminder for edit");
            output.output(interaction.setLanguageValue("reminder.edit.index").setInline(true));
            return;
        }
        long reminderId = (long) user.getValue(getCommandName(), "index");

        if (!user.getReminders(interaction.getChatId()).containsKey(reminderId)) {
            user.setExcepted(getCommandName(), "index");
            logger.info("Reminder command again request a index reminder for edit");
            edit(interaction, user);
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "newDate")) {
            user.setExcepted(getCommandName(), "newDate");
            logger.info("Reminder command request a new time reminder for edit");
            output.output(interaction.setLanguageValue("reminder.edit.newDate").setInline(true));
            return;
        }

        if (user.getValue(getCommandName(), "newDate") != "/skip") {
            LocalDateTime sendAt = (LocalDateTime) user.getValue(getCommandName(), "newDate");
            // Проверка на корректность даты
            if (sendAt.isBefore(LocalDateTime.now()) || sendAt.isAfter(sendAt.plusYears(5))) {
                user.setExcepted(getCommandName(), "newDate");
                logger.info("Reminder command again requested a date argument for edit");
                // Пользователь указал время в прошлом
                if (sendAt.isBefore(LocalDateTime.now())) {
                    output.output(interaction.setLanguageValue("reminder.edit.error.futureDate").setInline(true));
                } else { // Пользователь создал напоминание более чем на 5 лет в будущее
                    output.output(interaction.setLanguageValue("reminder.edit.error.limitDate").setInline(true));
                }
                edit(interaction, user);
                return;
            }
            return;
        }

        if (!user.isExceptedKey(getCommandName(), "newContext")) {
            user.setExcepted(getCommandName(), "newContext");
            logger.info("Reminder command request a new context reminder for edit");
            output.output(interaction.setLanguageValue("reminder.edit.newContent").setInline(true));
            return;
        }

        Object newLocalDate = user.getValue(getCommandName(), "newDate");
        String newContext = (String) user.getValue(getCommandName(), "newContext");
        Reminder reminder = user.getReminders(interaction.getChatId()).get(reminderId);

        // Изменяем описание напоминания, если такое имеется
        if (!Objects.equals(newContext, reminder.getContent()) && !Objects.equals(newContext, "/skip")) {
            reminder.setContent(newContext);
        }

        // Изменяем время отправки, если такое имеется
        if (!Objects.equals(newLocalDate, reminder.getSendAt()) && !Objects.equals(newLocalDate, "/skip")) {
            reminder.setSendAt((LocalDateTime) newLocalDate);
        }

        user.getReminders(interaction.getChatId()).put(reminderId, reminder);

        try {
            output.output(interaction.setLanguageValue("reminder.edit.complete",
                    List.of(String.valueOf(reminderId))));
        } catch (Exception err) {
            logger.debug(String.format("Couldn't find replaces symbols (Reminder->edit): %s", err));
        }
        logger.info(String.format("User by id(%d, chatId=%d) edit reminder by id(%d)",
                user.getUserId(), interaction.getChatId(), reminderId));
        user.clearExpected(getCommandName());
    }

    // Метод для удаления напоминания
    public void remove(Interaction interaction, User user) {

        if (!user.isExceptedKey(getCommandName(), "index")) {
            user.setExcepted(getCommandName(), "index", InputExpectation.UserInputType.INTEGER);
            logger.info("Reminder command requested a index argument for remove");
            output.output(interaction.setLanguageValue("reminder.remove.index").setInline(true));
            return;
        }

        Long reminderId = Long.parseLong(String.valueOf(user.getValue(getCommandName(), "index")));

        if (!user.getReminders(interaction.getChatId()).containsKey(reminderId)) {
            user.setExcepted(getCommandName(), "index", InputExpectation.UserInputType.INTEGER);
            logger.info("Reminder command again request a index reminder for remove");
            remove(interaction, user);
            return;
        }

        user.getReminders(interaction.getChatId()).remove(reminderId);

        try {
            output.output(interaction.setLanguageValue("reminder.remove.complete",
                    List.of(String.valueOf(reminderId))));
        } catch (Exception err) {
            logger.debug(String.format("Couldn't find replaces symbols (Reminder->remove): %s", err));
        }
        logger.info(String.format("User by id(%d, chatId=%d) remove reminder by id(%d)",
                user.getUserId(), interaction.getChatId(), reminderId));
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
            message.append(interaction.getLanguageValue("reminder.list.empty"));
        }

        output.output(interaction.setMessage(String.valueOf(message)));
        user.clearExpected(getCommandName());
    }
}