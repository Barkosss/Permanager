package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import common.commands.BaseCommand;
import common.exceptions.WrongArgumentsException;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.models.Permissions;
import common.utils.LoggerHandler;
import common.utils.Validate;

import java.util.List;
import java.util.Optional;

public class KickCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    Validate validate = new Validate();

    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public String getCommandDescription() {
        return "Выгнать пользователя";
    }

    @Override
    public void parseArgs(Interaction interaction, common.models.User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        Optional<Integer> validateUserId = validate.isValidInteger(arguments.getFirst());
        GetChatMemberResponse member = interactionTelegram.telegramBot
                .execute(new GetChatMember(interaction.getChatId(), validateUserId.get()));
        // ...
        if (member != null) {
            user.setExcepted(getCommandName(), "user").setValue(member);
        }

        // Сохраняем информации об ответном сообщении
        if (!user.isExceptedKey(getCommandName(), "user") && interactionTelegram.getContentReply() != null) {
            user.setExcepted(getCommandName(), "user")
                    .setValue(interactionTelegram.getContentReply().from());
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

        common.models.User user = interaction.getUser(interaction.getUserId());
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Проверяем на приватность чата
        if (interactionTelegram.telegramBot
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"kick\" in Chat by id(%d)",
                    interaction.getUserId(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        if (!user.hasPermission(interaction.getChatId(), Permissions.Permission.KICK)) {
            try {
                output.output(interaction.setLanguageValue("system.error.accessDenied",
                        List.of(((InteractionTelegram) interaction).getUsername())));
            } catch (WrongArgumentsException err) {
                logger.error(String.format("Get User from reply message (Kick): %s", err));
            }
            return;
        }

        // Парсинг аргументов
        parseArgs(interactionTelegram, user);

        // Получаем пользователя
        if (interactionTelegram.getContentReply() == null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Kick command requested a user argument");
            output.output(interactionTelegram.setLanguageValue("kick.replyMessage"));
            return;
        }

        // Сохраняем информации об ответном сообщении
        if (interactionTelegram.getContentReply() != null && !user.isExceptedKey(getCommandName(), "user")) {
            logger.info("Kick command get reply message with User");
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
            long userId = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user")).id();
            interactionTelegram.telegramBot.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.telegramBot.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been kicked");

            String username = ((Message) user.getValue(getCommandName(), "user")).from().username();
            String reason = (String) user.getValue(getCommandName(), "reason");
            if (reason != "/skip") {
                output.output(interactionTelegram.setLanguageValue("kick.complete", List.of(username)));
            } else {
                output.output(interactionTelegram.setLanguageValue("kick.completeWithReason",
                        List.of(username, reason)));
            }


        } catch (Exception err) {
            output.output(interaction.setLanguageValue("system.error.something"));
            logger.error("Kick command: " + err);
        } finally {
            user.clearExpected(getCommandName());
        }
    }
}