package common.commands.custom;

import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.models.Warning;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WarnsCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();
    ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "warns";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Если аргументы пустые - Смотрим у себя
        if (arguments.isEmpty()) {
            user.setExcepted(getCommandName(), "userId").setValue(interaction.getUserId());
            return;
        }

        // Если аргумент не пустой - Смотрим по Id первым аргументом
        Optional<Long> validUserId = validate.isValidLong(arguments.getFirst());
        if (validUserId.isPresent()) {
            logger.debug(String.format("User by id(%s) is valid", validUserId.get()));
            GetChatMemberResponse chatMember = interactionTelegram.telegramBot.execute(
                    new GetChatMember(interaction.getChatId(), validUserId.get()));
            // Если такой пользователь есть в чате
            if (chatMember != null && chatMember.chatMember() != null) {
                logger.debug(String.format("User by id(%s) not found in chat by id(%s)",
                        validUserId.get(), interaction.getChatId()));
                user.setExcepted(getCommandName(), "userId").setValue(chatMember.chatMember().user().id());
                return;
            } else { // Если пользователь не найден
                user.setExcepted(getCommandName(), "userId").setValue(interaction.getUserId());
            }

        } else if (interactionTelegram.getContentReply() != null) {
            logger.debug("Get user from reply message for remove warning");
            user.setExcepted(getCommandName(), "userId")
                    .setValue(interactionTelegram.getContentReply().from().id());

        } else {
            logger.debug("Get user id from caller of the command");
            user.setExcepted(getCommandName(), "userId").setValue(interaction.getUserId());
        }

        // Если есть ответное сообщение - смотрим у кого-то (И первый аргумент пустой или некорректный)
        if (interactionTelegram.getContentReply() != null) {
            long userId = interactionTelegram.getContentReply().from().id();
            logger.debug(String.format("User by id(%s) save to check warns in chat by id(%s)",
                    userId, interaction.getChatId()));
            user.setExcepted(getCommandName(), "userId").setValue(userId);
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;
        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        // Получаем userId целевого пользователя
        long targetUserId = (long) user.getValue(getCommandName(), "userId");
        com.pengrad.telegrambot.model.User targetMember = interactionTelegram.telegramBot
                .execute(new GetChatMember(interaction.getChatId(), targetUserId)).chatMember().user();
        com.pengrad.telegrambot.model.User moderatorMember;
        StringBuilder message = new StringBuilder();
        String warnReason;
        String warnDuration;
        String warnCreatedAt;
        try {
            Map<Long, Warning> warnings = interactionTelegram.findUserById(targetUserId)
                    .getWarnings(interaction.getChatId());

            // Если список предупреждений пуст
            if (warnings.isEmpty()) {
                message.append(interaction.getLanguageValue("warns.empty",
                        List.of(targetMember.username())));
            } else {
                message = new StringBuilder(interaction.getLanguageValue("warns.complete",
                        List.of(targetMember.username()))).append("\n");
            }

            for (Warning warning : warnings.values()) {
                moderatorMember = interactionTelegram.telegramBot
                        .execute(new GetChatMember(interaction.getChatId(), warning.getModeratorId()))
                        .chatMember().user();
                warnReason = (!warning.getReason().startsWith("/skip")) ? warning.getReason()
                        : interaction.getLanguageValue("system.undefined");
                warnDuration = (warning.getDuration() != null) ? String.valueOf(warning.getDuration())
                        : interaction.getLanguageValue("system.undefined");
                warnCreatedAt = String.valueOf(warning.getCreatedAt());

                message.append(interaction.getLanguageValue("warns.warning",
                        List.of(String.valueOf(warning.getId())))).append("\n");

                // Указана ли причина -> Вывести
                if (!warnReason.isEmpty()) {
                    message.append(interaction.getLanguageValue("warns.reason",
                            List.of(warnReason))).append("\n");
                }

                // Указана ли длительность -> Вывести
                if (!warnDuration.isEmpty()) {
                    message.append(interaction.getLanguageValue("warns.duration",
                            List.of(warnDuration))).append("\n");
                }

                // Модератор, который выдал предупреждение
                message.append(interaction.getLanguageValue("warns.moderator",
                        List.of(moderatorMember.username()))).append("\n");

                // Дата создания напоминания
                message.append(interaction.getLanguageValue("warns.createdAt",
                        List.of(warnCreatedAt))).append("\n");

                message.append("\n");
            }

        } catch (Exception err) {
            logger.error(String.format("Error in command \"%s\" (Building message with warnings): %s",
                    getCommandName(), err));
            output.output(interaction.setLanguageValue("system.error.something"));
        }


        try {
            output.output(interaction.setMessage(message.toString()));
            logger.info(String.format("User by id(%d) in chat by id(%d) has look warns at target user by id (%d)",
                    user.getUserId(), interaction.getChatId(), targetUserId));
        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error(String.format("Warns not show for user by id(%d): %s", user.getUserId(), err));
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
