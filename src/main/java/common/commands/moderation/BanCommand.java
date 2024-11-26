package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
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
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        User user = interactionTelegram.getUser(interaction.getUserId());
        parseArgs(interactionTelegram, user);


        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Ban command requested a user argument");
            output.output(interactionTelegram.setMessage("Reply message target user with command /ban"));
            return;
        }

        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }
        // Валидация пользователя...

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Ban command requested a reason argument");
            output.output(interactionTelegram.setMessage("Enter reason"));
            return;
        }

        // Получаем длительность блокировки
        if (!user.isExceptedKey(getCommandName(), "duration")) {
            user.setExcepted(getCommandName(), "duration");
            logger.info("Ban command requested a duration argument");
            output.output(interactionTelegram.setMessage("Enter duration"));
            return;
        }
        // Валидация даты...

        try {
            long userId = ((Message)user.getValue(getCommandName(), "user")).from().id();
            //((InteractionTelegram)interaction).telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + " has been banned");
            output.output(interactionTelegram.setMessage("The user @" + ((Message) user.getValue(getCommandName(), "user")).from().username()
                    + " has been banned"));
        } catch (Exception err) {
            logger.error("Ban command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}
