package common.commands.custom;

import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;

public class WarnsCommand implements BaseCommand {
    OutputHandler output = new OutputHandler();
    LoggerHandler logger = new LoggerHandler();

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

        // Если аргументы не пустые - смотрим у кого-то
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

        try {
            output.output(interaction.setLanguageValue("", List.of(String.valueOf(targetUserId))));
            logger.info(String.format("User by id(%d) in chat by id(%d) has look warns at target user by id (%d)",
                    user.getUserId(), interaction.getChatId(), targetUserId));
            user.clearExpected(getCommandName());
        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error(String.format("Warns not show for user by id(%d)", user.getUserId()));
        }
    }
}
