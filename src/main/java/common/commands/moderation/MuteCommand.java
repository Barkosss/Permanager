package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.RestrictChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MuteCommand implements BaseCommand {
    ValidateService validate = new ValidateService();
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
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
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
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Mute command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("mute.replyMessage"));
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
            output.output(interactionTelegram.setLanguageValue("mute.reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Mute command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("mute.duration"));
            return;
        }
        // Валидация даты...

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            interactionTelegram.telegramBot.execute(new RestrictChatMember(interaction.getChatId(), userId,
                    new ChatPermissions().canSendMessages(false)));
            interactionTelegram.findServerById(interaction.getChatId()).addUserMute(user);
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been muted");
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            output.output(interactionTelegram.setLanguageValue("mute.complete", List.of(
                    username,
                    (String) user.getValue(getCommandName(), "duration"),
                    (String) user.getValue(getCommandName(), "reason")
            )));
        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error("Mute command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
