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
import common.utils.Validate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WarnsCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();
    Validate validate = new Validate();

    @Override
    public String getCommandName() {
        return "warns";
    }

    @Override
    public String getCommandDescription() {
        return "Посмотреть список предупреждений";
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
        Optional<Integer> validInteger = validate.isValidInteger(arguments.getFirst());
        if (validInteger.isPresent()) {
            GetChatMemberResponse chatMember = interactionTelegram.telegramBot.execute(
                    new GetChatMember(interaction.getChatId(), validInteger.get()));
            // Если такой пользователь есть в чате
            if (chatMember != null) {
                user.setExcepted(getCommandName(), "userId").setValue(chatMember.chatMember().user().id());
                return;
            }
        }

        // Если есть ответное сообщение - смотрим у кого-то (И первый аргумент пустой или некорректный)
        if (interactionTelegram.getContentReply() != null) {
            long userId = interactionTelegram.getContentReply().from().id();
            user.setExcepted(getCommandName(), "userId").setValue(userId);
        }
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        // Получаем userId целевого пользователя
        long targetUserId = (long) user.getValue(getCommandName(), "userId");
        StringBuilder message = new StringBuilder();
        String warnReason;
        LocalDate warnDuration;
        try {
            message = new StringBuilder(interaction.getLanguageValue("warns.complete",
                    List.of(String.valueOf(targetUserId))));

            Map<Long, Warning> warnings = user.getWarnings(interaction.getChatId());
            for (Warning warning : warnings.values()) {
                warnReason = warning.getReason();
                warnDuration = warning.getDuration();

                message.append(interaction.getLanguageValue("warns.warn", List.of(
                        String.valueOf(warning.getId()),
                        String.valueOf(warning.getModeratorId())
                )));

                // Указана ли причина
                if (warnReason != null) {
                    message.append(interaction.getLanguageValue("warns.warnReason", List.of(warnReason)));
                }

                // Указана ли длительность
                if (warnDuration != null) {
                    message.append(interaction.getLanguageValue("warns.warnDuration", List.of(
                            String.valueOf(warnDuration)
                    )));
                }

                message.append("\n\n");
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
