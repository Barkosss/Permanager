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

public class UnmuteCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "unmute";
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

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.UNMUTE)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        if (interactionTelegram.telegramBot.execute(new GetChatMemberCount(interaction.getChatId())).count() <= 2) {
            output.output(interaction.setMessage("This command is not available for private chat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Unmute command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /unmute"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            // Валидация пользователя...
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            interactionTelegram.telegramBot.execute(new RestrictChatMember(interaction.getChatId(), userId,
                    new ChatPermissions().canSendMessages(true)));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been unmuted");
            output.output(interactionTelegram.setMessage("The user @" + username + " has been unmuted"));
        } catch (Exception err) {
            output.output(interaction.setMessage("Something went wrong... :("));
            logger.error("Unmute command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
