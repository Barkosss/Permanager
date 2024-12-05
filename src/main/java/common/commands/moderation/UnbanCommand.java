package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;

public class UnbanCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "unban";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {}

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setMessage("This command is not available for the console"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.UNBAN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Unban command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /Unban"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            // Валидация пользователя...
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been unbaned");
            output.output(interactionTelegram.setMessage("The user @" + username + " has been unbaned"));
        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Unban command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
