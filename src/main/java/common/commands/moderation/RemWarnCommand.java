package common.commands.moderation;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import common.commands.BaseCommand;
import common.iostream.OutputHandler;
import common.models.InputExpectation;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.models.User;
import common.utils.LoggerHandler;

public class RemWarnCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();

    @Override
    public String getCommandName() {
        return "remwarn";
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
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandConsole"));
            return;
        }

        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = (InteractionTelegram) interaction;

        if (interactionTelegram.telegramBot.execute(new GetChatMemberCount(interaction.getChatId())).count() <= 2) {
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.REMWARN)) {
            output.output(interaction.setLanguageValue("system.error.accessDenied"));
            return;
        }

        parseArgs(interactionTelegram, user);

        if (interactionTelegram.getContentReply() == null) {
            logger.info("");
            output.output(interaction.setLanguageValue("remwarn.replyMessage"));
        }
        user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());

        if (!user.isExceptedKey(getCommandName(), "index")) {
            logger.info("");
            user.setExcepted(getCommandName(), "index", InputExpectation.UserInputType.INTEGER);
            output.output(interaction.setLanguageValue("remwarn.index"));
            return;
        }

        long userId = ((Message)user.getValue(getCommandName(), "user")).from().id();
        long index = (long) user.getValue(getCommandName(), "index");

        // TODO: Переделать нахождение WarningRepository
        if (interactionTelegram.existsWarningById(interaction.getChatId(), index)) {
            interactionTelegram.removeWarning(interaction.getChatId(), index);
            output.output(interaction.setLanguageValue("..."));
            user.clearExpected(getCommandName());
        } else {
            user.setExcepted(getCommandName(), "index");
            output.output(interaction.setLanguageValue("..."));
        }
    }
}
