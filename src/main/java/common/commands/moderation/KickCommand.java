package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;

import java.util.List;

public class KickCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "kick";
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

        // Получаем причину кика
        user.setExcepted(getCommandName(), "reason").setValue(interaction.getMessage());
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.KICK)) {
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
            logger.info("Kick command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /kick"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            // Валидация пользователя...
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Kick command requested a reason argument");
            output.output(interactionTelegram.setMessage("Enter reason"));
            return;
        }

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been kicked");
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            output.output(interactionTelegram.setMessage(String.format("The user @%s has been kicked with reason: %s",
                    username, user.getValue(getCommandName(), "reason"))));

        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Kick command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}