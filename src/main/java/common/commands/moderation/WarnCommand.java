package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChat;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.models.Warning;
import common.utils.LoggerHandler;

import java.util.List;
import java.time.LocalDate;

public class WarnCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

    @Override
    public String getCommandName() {
        return "warn";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        if (arguments.isEmpty()) {
            return;
        }

        // Парсинг длительности предупреждения и причины

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

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.WARN)) {
            try {
                output.output(interaction.setLanguageValue("system.error.accessDenied", List.of(
                        interactionTelegram.getUsername()
                )));
            } catch (Exception err) {
                logger.error("");
                output.output(interaction.setLanguageValue("system.error.accessDenied"));
            }
            return;
        }

        parseArgs(interaction, user);

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
                user.clearExpected(getCommandName(), "user");
                return;
            }

            user.setExcepted(getCommandName(), "user").setValue(content);
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

        // Если указано прошлое время
        if (((LocalDate) user.getValue(getCommandName(), "duration")).isBefore(LocalDate.now())) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Warn command requested a duration argument");
            output.output(interactionTelegram.setLanguageValue("warn.duration"));
            return;
        }

        com.pengrad.telegrambot.model.User targetMember = ((Message) user.getValue(getCommandName(), "user")).from();
        Warning warning = new Warning(interaction.getChatId(), targetMember.id(), interaction.getUserId());
        warning.setId(user.getWarnings(interaction.getChatId()).size());

        // Если указали причину
        if (user.isExceptedKey(getCommandName(), "reason")) {
            warning.setReason((String) user.getValue(getCommandName(), "reason"));
        }

        // Если указали длительность
        if (user.isExceptedKey(getCommandName(), "duration")) {
            warning.setDuration((LocalDate) user.getValue(getCommandName(), "duration"));
        }

        user.addWarning(warning);
        try {
            logger.info(String.format("Moderator by id(%s) give warn #%s for user by id(%s) in chat by id(%s)",
                    interaction.getUserId(), warning.getId(), targetMember.id(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("warn.complete",
                    List.of(targetMember.username(), String.valueOf(warning.getId()))));
        } catch (Exception err) {
            logger.error("...");
            output.output(interaction.setLanguageValue("system.error.something"));

        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
