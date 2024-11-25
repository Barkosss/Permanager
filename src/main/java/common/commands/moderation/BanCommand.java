package common.commands.moderation;

import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.BanChatSenderChat;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.User;
import common.utils.LoggerHandler;

public class BanCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {

    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        parseArgs(interaction, user);

        // Получаем пользователя
        if (!user.isExceptedKey(getCommandName(), "user")) {
            user.setExcepted(getCommandName(), "user");
            logger.info("Ban command requested a user argument");
            output.output(interaction.setMessage("Enter user"));
            return;
        }
        // Валидация пользователя...

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Ban command requested a reason argument");
            output.output(interaction.setMessage("Enter reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Ban command requested a duration argument");
            output.output(interaction.setMessage("Enter duration"));
            return;
        }
        // Валидация даты...

        try {
            long userId = Long.parseLong(user.getValue(getCommandName(), "user"));
            ((InteractionTelegram)interaction).telegramBot.execute(new BanChatSenderChat(interaction.getChatId(), userId));
            ((InteractionTelegram)interaction).telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            logger.info("");
            output.output(interaction.setMessage("The user has been banned"));
        } catch (Exception err) {
            logger.error("");
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
