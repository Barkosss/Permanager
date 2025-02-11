package common.commands.moderation;

import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import common.commands.BaseCommand;
import common.enums.ModerationCommand;
import common.iostream.OutputHandler;
import common.models.Interaction;
import common.models.InteractionTelegram;
import common.utils.LoggerHandler;
import common.utils.ValidateService;

import java.util.List;
import java.util.Optional;

public class KickCommand implements BaseCommand {
    LoggerHandler logger = new LoggerHandler();
    OutputHandler output = new OutputHandler();
    ValidateService validate = new ValidateService();

    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public String getCommandDescription(Interaction interaction) {
        return interaction.getLanguageValue("commands." + getCommandName() + ".description");
    }

    @Override
    public void parseArgs(Interaction interaction, common.models.User user) {
        List<String> arguments = interaction.getArguments();
        InteractionTelegram interactionTelegram = ((InteractionTelegram) interaction);

        // Проверка на наличие аргументов
        if (arguments.isEmpty()) {
            return;
        }

        logger.info("Start of user validation in the chat by id(" + arguments.getFirst() + ")");
        Optional<Long> validateUserId = validate.isValidLong(arguments.getFirst());
        if (validateUserId.isPresent()) {
            GetChatMemberResponse member = interactionTelegram
                    .execute(new GetChatMember(interaction.getChatId(), validateUserId.get()));
            if (member != null) {
                user.setExcepted(getCommandName(), "user").setValue(member.chatMember().user());
                logger.info("Validation of the user by id(s" + arguments.getFirst() + ") has been completed");
            }
        }

        logger.info("Starting to save information about the reply message");
        // Сохраняем информации об ответном сообщении
        if (!user.isExceptedKey(getCommandName(), "user") && interactionTelegram.getContentReply() != null) {
            user.setExcepted(getCommandName(), "user")
                    .setValue(interactionTelegram.getContentReply().from());
            logger.info("");
        }

        // Получаем причину кика
        user.setExcepted(getCommandName(), "reason").setValue(interaction.getMessage());
        logger.info("Saving the reason");
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
        if (interactionTelegram
                .execute(new GetChat(interaction.getChatId())).chat().type() == ChatFullInfo.Type.Private) {
            logger.info(String.format("User by id(%d) use command \"%s\" in Chat by id(%d)",
                    interaction.getUserId(), getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.notAvailableCommandPrivateChat"));
            return;
        }

        logger.debug(String.format("Checking the user's access rights (%s) by id(%s) in the chat by id(%s)",
                ModerationCommand.KICK.getCommandName(), user.getUserId(), interaction.getChatId()));
        if (!user.hasPermission(interaction.getChatId(), ModerationCommand.KICK)) {
            logger.info(String.format("The user by id(%s) doesn't have access rights (%s) in chat by id(%s)",
                    user.getUserId(), ModerationCommand.KICK.getCommandName(), interaction.getChatId()));
            output.output(interaction.setLanguageValue("system.error.accessDenied",
                    List.of(((InteractionTelegram) interaction).getUsername())));
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
            interactionTelegram.execute(new BanChatMember(interaction.getChatId(), userId));
            interactionTelegram.execute(new UnbanChatMember(interaction.getChatId(), userId));
            logger.info("User by id(" + userId + ") in chat by id(" + interaction.getChatId() + ") has been kicked");

            String username = ((com.pengrad.telegrambot.model.User) user.getValue(getCommandName(), "user"))
                    .username();
            String reason = (String) user.getValue(getCommandName(), "reason");
            if (!reason.equals("/skip")) {
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