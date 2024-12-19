package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
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
        return "Запретить отправку сообщений пользователю";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        Optional<Long> validUserId = validate.isValidLong(arguments.getFirst());
        if (validUserId.isPresent()) {
            logger.debug("...");
            user.setExcepted(getCommandName(), "user")
                    .setValue(interactionTelegram.telegramBot
                            .execute(new GetChatMember(interaction.getChatId(), validUserId.get())).chatMember().user());
            arguments = arguments.subList(1, arguments.size());
        }

        if (arguments.isEmpty()) {
            return;
        }

        Optional<LocalDate> validDate = validate.isValidDate(String.format("%s, %s",
                arguments.getFirst(), arguments.get(1)));
        Optional<LocalDate> validTime = validate.isValidTime(arguments.getFirst());
        // Получаем длительность мута
        if (validDate.isPresent()) {
            logger.debug("...");
            user.setExcepted(getCommandName(), "duration").setValue(validDate.get());
            arguments = arguments.subList(2, arguments.size());
        } else if (validTime.isPresent()) {
            logger.debug("...");
            user.setExcepted(getCommandName(), "duration").setValue(validTime.get());
            arguments = arguments.subList(1, arguments.size());
        }

        if (arguments.isEmpty()) {
            return;
        }

        // Получаем причину мута
        user.setExcepted(getCommandName(), "reason").setValue(String.join(" ", arguments));
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Проверяем на приватность чата
        if (interactionTelegram.telegramBot
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    interaction.getUserId(), getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.MUTE)) {
            try {
                output.output(interaction.setLanguageValue("system.error.accessDenied", List.of(
                        interactionTelegram.getUsername()
                )));
            } catch (Exception err) {
                logger.error("...");
                output.output(interaction.setLanguageValue("system.error.accessDenied"));
            }
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Mute command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("mute.replyMessage"));
            return;
        }

        // Получаем пользователя из ответного сообщения
        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            Message content = interactionTelegram.getContentReply();

            // Если пользователь это бот или взаимодействующий
            if (content.from().isBot() || content.from().id() == interaction.getUserId()) {
                user.setExcepted(getCommandName(), "user");
                output.output(interaction.setLanguageValue("warn.replyMessage"));
                return;
            }

            user.setExcepted(getCommandName(), "user").setValue(content.from());
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

        String userDuration = (String) user.getValue(getCommandName(), "duration");
        Optional<LocalDate> validDate = validate.isValidDate(userDuration);
        if (!userDuration.equals("/skip") && validDate.isEmpty()) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Mute command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("mute.duration"));
            return;
        }

        // Если указано прошлое время
        if (!userDuration.equals("/skip") && validDate.get().isBefore(LocalDate.now())) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Mute command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("mute.duration"));
            return;
        }

        try {
            com.pengrad.telegrambot.model.User targetMember
                    = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"));
            long targetMemberId = targetMember.id();
            User targetUser = interactionTelegram.findUserById(targetMemberId);
            interactionTelegram.telegramBot.execute(new RestrictChatMember(interaction.getChatId(), targetMemberId,
                    new ChatPermissions().canSendMessages(false)));
            interactionTelegram.findServerById(interaction.getChatId()).addUserMute(targetUser);
            // TODO: Переработать duration
            LocalDate duration = (LocalDate) user.getValue(getCommandName(), "duration");
            String reason = (String) user.getValue(getCommandName(), "reason");
            String muteDuration = (!(String.valueOf(duration)).startsWith("/skip")) ? String.valueOf(duration)
                    : (interaction.getLanguageValue("system.undefined"));
            String muteReason = (!reason.startsWith("/skip")) ? (reason)
                    : (interaction.getLanguageValue("system.undefined"));
            logger.info(String.format("User by id(%s) in chat by id(%s) has been muted",
                    targetMemberId, interaction.getChatId()));
            String username = targetMember.username();
            output.output(interactionTelegram.setLanguageValue("mute.complete", List.of(
                    username,
                    muteDuration,
                    muteReason
            )));
        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error("Mute command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
