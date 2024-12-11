package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.UnbanChatMember;
import common.commands.BaseCommand;
import common.exceptions.WrongArgumentsException;
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
        return "Выгнать пользователя";
    }

    @Override
    public void parseArgs(Interaction interaction, User user) {
        List<String> arguments = interaction.getArguments();

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        // Сохраняем информации об ответном сообщении
        if (((InteractionTelegram) interaction).getContentReply() != null) {
            user.setExcepted(getCommandName(), "user")
                    .setValue(((InteractionTelegram) interaction).getContentReply());
        }

        // Получаем причину кика
        user.setExcepted(getCommandName(), "reason").setValue(interaction.getMessage());
    }

    @Override
    public void run(Interaction interaction) {
        if (interaction.getPlatform() == Interaction.Platform.CONSOLE) {
            output.output(interaction.setLanguageValue("kick.error.notAvailableCommandConsole"));
            return;
        }
        User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.KICK)) {
            try {
                output.output(interaction.setLanguageValue("system.error.accessDenied",
                        List.of(((InteractionTelegram) interaction).getUsername())));
            } catch (WrongArgumentsException err) {
                logger.error(String.format(": %s", err));
            }
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        // Проверяем на приватность чата
        if (interactionTelegram.telegramBot.execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            output.output(interaction.setLanguageValue("kick.error.notAvailableCommandPrivateChat"));
            return;
        }

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Kick command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("kick.replyMessage"));
            return;
        }

        // Сохраняем информации об ответном сообщении
        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            user.setExcepted(getCommandName(), "user").setValue(interactionTelegram.getContentReply());
        }

        // Получаем причину блокировки
        if (!user.isExceptedKey(getCommandName(), "reason")) {
            user.setExcepted(getCommandName(), "reason");
            logger.info("Kick command requested a reason argument");
            output.output(interactionTelegram.setLanguageValue("kick.reason"));
            return;
        }

        try {
            long userId = ((Message) user.getValue(getCommandName(), "user")).from().id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been kicked");
            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            output.output(interactionTelegram.setLanguageValue("kick.accepted",
                    List.of(username, (String) user.getValue(getCommandName(), "reason"))));

        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error("Kick command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}