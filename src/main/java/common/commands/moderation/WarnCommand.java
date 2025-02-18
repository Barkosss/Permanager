package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import common.commands.BaseCommand;
import common.enums.ModerationCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.models.Warning;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WarnCommand implements BaseCommand {
    ValidateService validate = new ValidateService();
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "warn";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        if (arguments.isEmpty()) {
            return;
        }

        Optional<Long> validUserId = validate.isValidLong(arguments.getFirst());
        if (validUserId.isPresent()) {
            ChatMember targetMember = interactionTelegram
                    .execute(new GetChatMember(interaction.getChatId(), validUserId.get()))
                    .chatMember();

            if (targetMember != null) {
                user.setExcepted(getCommandName(), "user")
                        .setValue(targetMember.user());
                arguments = arguments.subList(1, arguments.size());
            }

        } else if (interactionTelegram.getContentReply() != null) {
            Message contentReply = interactionTelegram.getContentReply();
            if (!contentReply.from().isBot() && contentReply.from().id() != interaction.getUserId()) {
                user.setExcepted(getCommandName(), "user").setValue(contentReply.from());
            }
        }

        if (arguments.isEmpty()) {
            return;
        }

        Optional<LocalDateTime> validDate = validate.isValidDate(String.format("%s %s",
                arguments.getFirst(), arguments.get(1)));
        Optional<LocalDateTime> validTime = validate.isValidDate(arguments.getFirst());

        if (validDate.isPresent()) {
            user.setExcepted(getCommandName(), "duration").setValue(validDate.get());
            arguments = arguments.subList(1, arguments.size());
        } else if (validTime.isPresent()) {
            user.setExcepted(getCommandName(), "duration").setValue(validTime.get());
            arguments = arguments.subList(1, arguments.size());
        }

        if (arguments.isEmpty()) {
            return;
        }

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
        if (interactionTelegram
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    interaction.getUserId(), getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), ModerationCommand.WARN)) {
            try {
                logger.debug(String.format("User by id(%s) don't has permissions (WARN) in chat by id(%s",
                        user.getUserId(), interaction.getChatId()));
                output.output(interaction.setLanguageValue("system.error.accessDenied", List.of(
                        interactionTelegram.getUsername()
                )));
            } catch (Exception err) {
                logger.error(String.format("Error in command (%s): %s", getCommandName(), err));
                output.output(interaction.setLanguageValue("system.error.accessDenied"));
            }
            return;
        }

        parseArgs(interactionTelegram, user);

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Warn command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("warn.replyMessage"));
            return;
        }

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
            logger.info("Warn command requested a reason argument");
            output.output(interactionTelegram.setLanguageValue("warn.reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Warn command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("warn.duration"));
            return;
        }


        String userDuration = (String) user.getValue(getCommandName(), "duration");
        Optional<LocalDateTime> validDate = validate.isValidDate(userDuration);
        if (!userDuration.equals("/skip") && validDate.isEmpty()) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Warn command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("warn.duration"));
            return;
        }

        // Если указано прошлое время
        if (!userDuration.equals("/skip") && validDate.get().isBefore(LocalDateTime.now())) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Warn command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("warn.duration"));
            return;
        }

        com.pengrad.telegrambot.model.User targetMember
                = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"));
        User targetUser = interactionTelegram.findUserById(targetMember.id());
        Warning warning = new Warning(interaction.getChatId(), targetMember.id(), interaction.getUserId());
        warning.setId(targetUser.getWarnings(interaction.getChatId()).size() + 1);

        // Если указали причину
        if (user.isExceptedKey(getCommandName(), "reason")) {
            warning.setReason((String) user.getValue(getCommandName(), "reason"));
        }

        // Если указали длительность
        if (user.isExceptedKey(getCommandName(), "duration")
                && !user.getValue(getCommandName(), "duration").equals("/skip")) {
            warning.setDuration((LocalDateTime) user.getValue(getCommandName(), "duration"));
        }

        targetUser.addWarning(interactionTelegram.createWarning(warning));
        try {
            logger.info(String.format("Moderator by id(%s) give warn #%s for user by id(%s) in chat by id(%s)",
                    interaction.getUserId(), warning.getId(), targetMember.id(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("warn.complete",
                    List.of(targetMember.username(), String.valueOf(warning.getId()))));
        } catch (Exception err) {
            logger.error(String.format("Something error in Warn command: %s", err));
            output.output(interaction.setLanguageValue("system.error.something"));

        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
