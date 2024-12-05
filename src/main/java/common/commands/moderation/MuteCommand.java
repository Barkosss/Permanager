package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MuteCommand implements BaseCommand {
    Validate validate = new Validate();
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "mute";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // Получаем длительность мута
        Optional<LocalDate> durationDate = validate.isValidDate(arguments.get(arguments.size() - 2)
                + " " + arguments.getLast());
        durationDate.ifPresent(localDate -> user.setExcepted(getCommandName(), "duration").setValue(localDate));

        // Получаем причину мута
        if (arguments.size() > 1) {
            user.setExcepted(getCommandName(), "reason").setValue(String.join(" ",
                    arguments.subList(0, arguments.size() - 1)));
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.MUTE)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        if (interactionTelegram.telegramBot.execute(new GetChatMemberCount(interaction.getChatId())).count() <= 2) {
            output.output(interaction.setMessage("This command is not available for private chat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Mute command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /mute"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            // Валидация пользователя...
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Mute command requested a reason argument");
            output.output(interactionTelegram.setMessage("Enter reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Mute command requested a duration argument");
            output.output(interactionTelegram.setMessage("Enter duration"));
            return;
        }
        // Валидация даты...

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            interactionTelegram.telegramBot.execute(new RestrictChatMember(interaction.getChatId(), userId,
                    new ChatPermissions().canSendMessages(false)));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been muted");
            output.output(interactionTelegram.setMessage("The user @"
                    + username
                    + " has been mute to " + user.getValue(getCommandName(), "duration")
                    + " with reason: " + user.getValue(getCommandName(), "reason")));
        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Mute command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
